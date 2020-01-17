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
import austeretony.oxygen_groups.common.GroupInviteRequest;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.EnumGroupsPrivilege;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.client.CPAddNewGroupMember;
import austeretony.oxygen_groups.common.network.client.CPLeaveGroup;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPUpdateGroupLeader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsDataManagerServer {

    private final GroupsManagerServer manager;

    protected GroupsDataManagerServer(GroupsManagerServer manager) {
        this.manager = manager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {    
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);     
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

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
            for (UUID memberUUID : group.getMembers())
                if (!memberUUID.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenHelperServer.removePlayerSharedData(playerUUID, CommonReference.playerByUUID(memberUUID));
        }
    }

    public void onPlayerChangedStatusActivity(EntityPlayerMP playerMP, EnumActivityStatus newStatus) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
            PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerUUID);

            for (UUID memberUUID : group.getMembers())
                if (!memberUUID.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenHelperServer.sendPlayerSharedData(sharedData, CommonReference.playerByUUID(memberUUID));
        }
    }

    public void inviteToGroup(EntityPlayerMP playerMP, int targetIndex) {
        UUID 
        senderUUID = CommonReference.getPersistentUUID(playerMP),
        targetUUID;
        if (PrivilegesProviderServer.getAsBoolean(senderUUID, EnumGroupsPrivilege.ALLOW_GROUP_CREATION.id(), true)) {
            if (OxygenHelperServer.isPlayerOnline(targetIndex)) {
                targetUUID = OxygenHelperServer.getPlayerSharedData(targetIndex).getPlayerUUID();
                if (!senderUUID.equals(targetUUID) 
                        && this.canInvite(senderUUID) 
                        && this.canBeInvited(targetUUID)) {
                    OxygenHelperServer.sendRequest(playerMP, CommonReference.playerByUUID(targetUUID), 
                            new GroupInviteRequest(GroupsMain.GROUP_INVITATION_REQUEST_ID, senderUUID, CommonReference.getName(playerMP)));
                } else
                    OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.REQUEST_RESET.ordinal());
            }
        }
    }

    public void processGroupCreation(EntityPlayer player, UUID leaderUUID) {
        if (this.canBeInvited(CommonReference.getPersistentUUID(player))) {
            if (this.manager.getGroupsDataContainer().haveGroup(leaderUUID))
                this.addNewGroupMember(player, leaderUUID);
            else
                this.createGroup(player, leaderUUID);

            this.manager.getGroupsDataContainer().setChanged(true);
        }
    }

    private void createGroup(EntityPlayer player, UUID leaderUUID) {
        UUID invitedUUID = CommonReference.getPersistentUUID(player);
        Group group = new Group();
        group.setId(this.manager.getGroupsDataContainer().getNewGroupId());
        group.setLeader(leaderUUID);
        group.addMember(leaderUUID);
        group.addMember(invitedUUID);
        this.manager.getGroupsDataContainer().addGroup(group);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), leaderUUID);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), invitedUUID);

        OxygenHelperServer.addTrackedEntity(leaderUUID, invitedUUID, true);
        OxygenHelperServer.addTrackedEntity(invitedUUID, leaderUUID, true);

        OxygenHelperServer.addObservedPlayer(leaderUUID, invitedUUID);
        OxygenHelperServer.addObservedPlayer(invitedUUID, leaderUUID);

        OxygenHelperServer.sendPlayerSharedData(invitedUUID, (EntityPlayerMP) player);
        OxygenHelperServer.sendPlayerSharedData(leaderUUID, (EntityPlayerMP) player);

        OxygenMain.network().sendTo(new CPSyncGroup(group), CommonReference.playerByUUID(leaderUUID));
        OxygenMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);

        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.JOINED_GROUP.ordinal());
    }   

    private void addNewGroupMember(EntityPlayer player, UUID leaderUUID) {   
        UUID invitedUUID = CommonReference.getPersistentUUID(player);
        Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);

        for (UUID memberUUID : group.getMembers()) {
            if (OxygenHelperServer.isPlayerOnline(memberUUID)) {
                OxygenHelperServer.addTrackedEntity(invitedUUID, memberUUID, true);
                OxygenHelperServer.addTrackedEntity(memberUUID, invitedUUID, true);
            }

            OxygenHelperServer.addObservedPlayer(invitedUUID, memberUUID);
            OxygenHelperServer.addObservedPlayer(memberUUID, invitedUUID);
        }

        group.addMember(invitedUUID);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), invitedUUID);

        PlayerSharedData invitedSharedData = OxygenHelperServer.getPlayerSharedData(invitedUUID);
        OxygenManagerServer.instance().getSharedDataManager().syncObservedPlayersData((EntityPlayerMP) player);
        for (UUID memberUUID : group.getMembers()) {
            if (!memberUUID.equals(invitedUUID) && OxygenHelperServer.isPlayerOnline(memberUUID)) {
                OxygenMain.network().sendTo(new CPAddNewGroupMember(invitedSharedData), CommonReference.playerByUUID(memberUUID));
                OxygenHelperServer.sendPlayerSharedData(OxygenHelperServer.getPlayerSharedData(memberUUID), (EntityPlayerMP) player);
            }
        }
        OxygenMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);

        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.JOINED_GROUP.ordinal());
    }

    public void leaveGroup(UUID playerUUID) {
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
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
            this.manager.getGroupsDataContainer().removeGroupAccess(playerUUID);

            for (UUID memberUUID : group.getMembers()) {
                OxygenHelperServer.removeObservedPlayer(playerUUID, memberUUID);
                OxygenHelperServer.removeObservedPlayer(memberUUID, playerUUID);
            }

            if (OxygenHelperServer.isPlayerOnline(playerUUID)) {
                EntityPlayerMP playerMP = CommonReference.playerByUUID(playerUUID);
                OxygenMain.network().sendTo(new CPLeaveGroup(), playerMP);
                OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.LEFT_GROUP.ordinal());
            }

            for (UUID memberUUID : group.getMembers())
                if (OxygenHelperServer.isPlayerOnline(memberUUID))
                    OxygenMain.network().sendTo(new CPRemovePlayerFromGroup(playerUUID), CommonReference.playerByUUID(memberUUID));

            this.manager.getGroupsDataContainer().setChanged(true);
        }
    }

    public void disbandGroup(Group group) {
        for (UUID memberUUID : group.getMembers()) {
            this.manager.getGroupsDataContainer().removeGroupAccess(memberUUID);

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
    }

    public void kickPlayer(EntityPlayerMP playerMP, UUID toKickUUID) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(leaderUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);
            if (group.isLeader(leaderUUID)
                    && group.isMember(toKickUUID)) {
                this.leaveGroup(toKickUUID);

                OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.PLAYER_KICKED.ordinal());

                this.manager.getGroupsDataContainer().setChanged(true);
            }
        }
    }

    public void promoteToLeader(EntityPlayerMP playerMP, UUID newLeaderUUID) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(leaderUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);
            if (group.isLeader(leaderUUID)
                    && group.isMember(newLeaderUUID)) {
                if (OxygenHelperServer.isPlayerOnline(newLeaderUUID)) {
                    group.setLeader(newLeaderUUID);
                    int newLeaderIndex = OxygenHelperServer.getPlayerSharedData(newLeaderUUID).getIndex();
                    for (UUID memberUUID : group.getMembers())
                        if (OxygenHelperServer.isPlayerOnline(memberUUID))
                            OxygenMain.network().sendTo(new CPUpdateGroupLeader(newLeaderIndex), CommonReference.playerByUUID(memberUUID));

                    OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.NEW_LEADER_SET.ordinal());

                    this.manager.getGroupsDataContainer().setChanged(true);
                }
            }
        }
    }

    private boolean canInvite(UUID playerUUID) {
        if (!this.manager.getGroupsDataContainer().haveGroup(playerUUID))
            return true;
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        return group.isLeader(playerUUID) && group.getSize() < GroupsConfig.PLAYERS_PER_PARTY.asInt();
    }

    private boolean canBeInvited(UUID playerUUID) {
        return !this.manager.getGroupsDataContainer().haveGroup(playerUUID);
    }
}
