package austeretony.oxygen_groups.server;

import java.util.UUID;

import austeretony.oxygen_core.common.EnumActivityStatus;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.OxygenPlayerData;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_groups.common.Group;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.EnumGroupsPrivilege;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.client.CPAddNewGroupMember;
import austeretony.oxygen_groups.common.network.client.CPLeaveGroup;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPUpdateGroupLeader;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsDataManagerServer {

    private final GroupsManagerServer manager;

    protected GroupsDataManagerServer(GroupsManagerServer manager) {
        this.manager = manager;
    }

    public void playerLoaded(EntityPlayerMP playerMP) {    
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);    
        if (group != null) {
            PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerUUID);
            OxygenPlayerData oxygenData = OxygenHelperServer.getOxygenPlayerData(playerUUID);
            for (UUID memberUUID : group.getMembers()) {
                if (OxygenHelperServer.isPlayerOnline(memberUUID)) {
                    if (!memberUUID.equals(playerUUID)) {
                        if (oxygenData != null)
                            oxygenData.addTrackedEntity(memberUUID, true);
                        OxygenHelperServer.addTrackedEntity(memberUUID, playerUUID, true);
                    }
                    OxygenHelperServer.sendPlayerSharedData(sharedData, CommonReference.playerByUUID(memberUUID));
                    OxygenHelperServer.sendPlayerSharedData(OxygenHelperServer.getPlayerSharedData(memberUUID), playerMP);
                }
            }
            OxygenMain.network().sendTo(new CPSyncGroup(group), playerMP);
        }
    }

    public void playerUnloaded(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        if (group != null)
            for (UUID memberUUID : group.getMembers())
                if (!memberUUID.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenHelperServer.removePlayerSharedData(playerUUID, CommonReference.playerByUUID(memberUUID));
    }

    public void playerChangedStatusActivity(EntityPlayerMP playerMP, EnumActivityStatus newStatus) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        if (group != null) {
            PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerUUID);

            for (UUID memberUUID : group.getMembers())
                if (!memberUUID.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenHelperServer.sendPlayerSharedData(sharedData, CommonReference.playerByUUID(memberUUID));
        }
    }

    public void inviteToGroup(EntityPlayerMP senderMP, int targetIndex) {
        UUID 
        senderUUID = CommonReference.getPersistentUUID(senderMP),
        targetUUID;
        if (PrivilegesProviderServer.getAsBoolean(senderUUID, EnumGroupsPrivilege.ALLOW_GROUP_CREATION.id(), true)) {
            if (OxygenHelperServer.isPlayerOnline(targetIndex)) {
                targetUUID = OxygenHelperServer.getPlayerSharedData(targetIndex).getPlayerUUID();
                if (!senderUUID.equals(targetUUID) 
                        && this.canInvite(senderUUID) 
                        && this.canBeInvited(targetUUID)) {
                    EntityPlayerMP targetMP = CommonReference.playerByUUID(targetUUID);
                    OxygenHelperServer.sendRequest(senderMP, targetMP, new GroupInviteRequest(senderUUID, CommonReference.getName(senderMP)));

                    if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Groups] Player {}/{} sent group invitation to player <{}/{}>",
                                CommonReference.getName(senderMP),
                                CommonReference.getPersistentUUID(senderMP),
                                CommonReference.getName(targetMP),
                                CommonReference.getPersistentUUID(targetMP));
                } else
                    OxygenManagerServer.instance().sendStatusMessage(senderMP, EnumOxygenStatusMessage.REQUEST_RESET);
            }
        }
    }

    public void processGroupCreation(EntityPlayerMP playerMP, UUID leaderUUID) {
        if (this.canBeInvited(CommonReference.getPersistentUUID(playerMP))) {
            if (this.manager.getGroupsDataContainer().getGroup(leaderUUID) != null)
                this.addNewGroupMember(playerMP, leaderUUID);
            else
                this.createGroup(playerMP, leaderUUID);

            this.manager.getGroupsDataContainer().setChanged(true);
        }
    }

    private void createGroup(EntityPlayerMP playerMP, UUID leaderUUID) {
        UUID invitedUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = new Group();
        group.setId(this.manager.getGroupsDataContainer().createId());
        group.setLeader(leaderUUID);
        group.addMember(leaderUUID);
        group.addMember(invitedUUID);
        this.manager.getGroupsDataContainer().addGroup(group);
        this.manager.getGroupsDataContainer().playerJoinedGroup(leaderUUID, group.getId());
        this.manager.getGroupsDataContainer().playerJoinedGroup(invitedUUID, group.getId());

        OxygenHelperServer.addTrackedEntity(leaderUUID, invitedUUID, true);
        OxygenHelperServer.addTrackedEntity(invitedUUID, leaderUUID, true);

        OxygenHelperServer.addObservedPlayer(leaderUUID, invitedUUID);
        OxygenHelperServer.addObservedPlayer(invitedUUID, leaderUUID);

        OxygenHelperServer.sendPlayerSharedData(invitedUUID, playerMP);
        OxygenHelperServer.sendPlayerSharedData(leaderUUID, playerMP);

        EntityPlayerMP senderMP = CommonReference.playerByUUID(leaderUUID);
        OxygenMain.network().sendTo(new CPSyncGroup(group), senderMP);
        OxygenMain.network().sendTo(new CPSyncGroup(group), playerMP);

        this.manager.sendStatusMessage(playerMP, EnumGroupsStatusMessage.JOINED_GROUP);

        if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
            OxygenMain.LOGGER.info("[Groups] Player {}/{} accepted group invitation from player <{}/{}>. Group created.",
                    CommonReference.getName(playerMP),
                    CommonReference.getPersistentUUID(playerMP),
                    CommonReference.getName(senderMP),
                    CommonReference.getPersistentUUID(senderMP));
    }   

    private void addNewGroupMember(EntityPlayerMP playerMP, UUID leaderUUID) {   
        UUID invitedUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);
        if (group != null) {
            for (UUID memberUUID : group.getMembers()) {
                if (OxygenHelperServer.isPlayerOnline(memberUUID)) {
                    OxygenHelperServer.addTrackedEntity(invitedUUID, memberUUID, true);
                    OxygenHelperServer.addTrackedEntity(memberUUID, invitedUUID, true);
                }

                OxygenHelperServer.addObservedPlayer(invitedUUID, memberUUID);
                OxygenHelperServer.addObservedPlayer(memberUUID, invitedUUID);
            }

            group.addMember(invitedUUID);
            this.manager.getGroupsDataContainer().playerJoinedGroup(invitedUUID, group.getId());

            PlayerSharedData invitedSharedData = OxygenHelperServer.getPlayerSharedData(invitedUUID);
            OxygenManagerServer.instance().getSharedDataManager().syncObservedPlayersData(playerMP);
            for (UUID memberUUID : group.getMembers()) {
                if (!memberUUID.equals(invitedUUID) && OxygenHelperServer.isPlayerOnline(memberUUID)) {
                    OxygenMain.network().sendTo(new CPAddNewGroupMember(invitedSharedData), CommonReference.playerByUUID(memberUUID));
                    OxygenHelperServer.sendPlayerSharedData(OxygenHelperServer.getPlayerSharedData(memberUUID), playerMP);
                }
            }
            OxygenMain.network().sendTo(new CPSyncGroup(group), playerMP);

            this.manager.sendStatusMessage(playerMP, EnumGroupsStatusMessage.JOINED_GROUP);

            if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Groups] Player {}/{} joined group of player <{}>.",
                        CommonReference.getName(playerMP),
                        CommonReference.getPersistentUUID(playerMP),
                        leaderUUID);
        }
    }

    public void leaveGroup(UUID playerUUID) {
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        if (group != null) {
            if (group.getSize() <= 2) {
                this.disbandGroup(group);
                return;
            }
            if (group.isLeader(playerUUID)) {
                UUID uuid = group.getRandomOnlinePlayer();
                if (uuid == null) {
                    this.disbandGroup(group);
                    return;
                }
                group.setLeader(uuid);
            }

            group.removeMember(playerUUID);
            this.manager.getGroupsDataContainer().playerLeftGroup(playerUUID);

            for (UUID memberUUID : group.getMembers()) {
                OxygenHelperServer.removeObservedPlayer(playerUUID, memberUUID);
                OxygenHelperServer.removeObservedPlayer(memberUUID, playerUUID);
            }

            if (OxygenHelperServer.isPlayerOnline(playerUUID)) {
                EntityPlayerMP playerMP = CommonReference.playerByUUID(playerUUID);
                OxygenMain.network().sendTo(new CPLeaveGroup(), playerMP);
                this.manager.sendStatusMessage(playerMP, EnumGroupsStatusMessage.LEFT_GROUP);
            }

            for (UUID memberUUID : group.getMembers())
                if (OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenMain.network().sendTo(new CPRemovePlayerFromGroup(playerUUID), CommonReference.playerByUUID(memberUUID));

            this.manager.getGroupsDataContainer().setChanged(true);

            if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Groups] Player {} left or kicked from group.",
                        playerUUID);
        }
    }

    public void disbandGroup(Group group) {
        for (UUID memberUUID : group.getMembers()) {
            this.manager.getGroupsDataContainer().playerLeftGroup(memberUUID);

            for (UUID uuid : group.getMembers())
                OxygenHelperServer.removeObservedPlayer(memberUUID, uuid);

            if (OxygenHelperServer.isPlayerOnline(memberUUID)) {
                EntityPlayerMP playerMP = CommonReference.playerByUUID(memberUUID);
                OxygenMain.network().sendTo(new CPLeaveGroup(), playerMP);
                OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.LEFT_GROUP.ordinal());
            }
        }
        this.manager.getGroupsDataContainer().removeGroup(group.getId());

        this.manager.getGroupsDataContainer().setChanged(true);

        if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
            OxygenMain.LOGGER.info("[Groups] Player {} disbanded group.",
                    group.getLeader());
    }

    public void kickPlayer(EntityPlayerMP playerMP, UUID toKickUUID) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);
        if (group != null
                &&group.isLeader(leaderUUID)
                && group.isMember(toKickUUID)) {
            this.leaveGroup(toKickUUID);

            OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.PLAYER_KICKED.ordinal());

            this.manager.getGroupsDataContainer().setChanged(true);

            if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Groups] Player {}/{} kicked player {} from group.",
                        CommonReference.getName(playerMP),
                        CommonReference.getPersistentUUID(playerMP),
                        toKickUUID);
        }
    }

    public void promoteToLeader(EntityPlayerMP playerMP, UUID newLeaderUUID) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);
        if (group != null
                && group.isLeader(leaderUUID)
                && group.isMember(newLeaderUUID)) {
            if (OxygenHelperServer.isPlayerOnline(newLeaderUUID)) {
                group.setLeader(newLeaderUUID);
                int newLeaderIndex = OxygenHelperServer.getPlayerSharedData(newLeaderUUID).getIndex();
                for (UUID memberUUID : group.getMembers())
                    if (OxygenHelperServer.isPlayerOnline(memberUUID))
                        OxygenMain.network().sendTo(new CPUpdateGroupLeader(newLeaderIndex), CommonReference.playerByUUID(memberUUID));

                OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.NEW_LEADER_SET.ordinal());

                this.manager.getGroupsDataContainer().setChanged(true);

                if (GroupsConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Groups] Player {}/{} promoted player {} to group leader.",
                            CommonReference.getName(playerMP),
                            CommonReference.getPersistentUUID(playerMP),
                            newLeaderUUID);
            }
        }
    }

    private boolean canInvite(UUID playerUUID) {
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        return group == null || (group.isLeader(playerUUID) && group.getSize() < GroupsConfig.PLAYERS_PER_PARTY.asInt());
    }

    private boolean canBeInvited(UUID playerUUID) {
        return this.manager.getGroupsDataContainer().getGroup(playerUUID) == null;
    }
}
