package austeretony.oxygen_groups.server;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.EnumGroupsChatMessage;
import austeretony.oxygen_groups.common.main.Group;
import austeretony.oxygen_groups.common.main.GroupInviteRequest;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.main.KickPlayerRequest;
import austeretony.oxygen_groups.common.main.ReadinessCheckRequest;
import austeretony.oxygen_groups.common.network.client.CPAddPlayerToGroup;
import austeretony.oxygen_groups.common.network.client.CPAddSharedData;
import austeretony.oxygen_groups.common.network.client.CPLeaveGroup;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPRemoveSharedData;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroupData;
import austeretony.oxygen_groups.common.network.client.CPUpdateLeader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsDataManager {

    private final GroupsManagerServer manager;

    protected GroupsDataManager(GroupsManagerServer manager) {
        this.manager = manager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {    
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerUUID);
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);     

            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenMain.network().sendTo(new CPAddSharedData(sharedData), CommonReference.playerByUUID(uuid));

            OxygenMain.network().sendTo(new CPSyncGroup(group), playerMP);
        }
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);

            for (UUID uuid : group.getPlayers())
                if (!uuid.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenMain.network().sendTo(new CPRemoveSharedData(playerUUID), CommonReference.playerByUUID(uuid));
        }
    }

    public void onPlayerChangedStatusActivity(EntityPlayerMP playerMP, EnumActivityStatus newStatus) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
            PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerUUID);

            for (UUID uuid : group.getPlayers())
                if (!uuid.equals(playerUUID) && OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenMain.network().sendTo(new CPAddSharedData(sharedData), CommonReference.playerByUUID(uuid));
        }
    }

    public void inviteToGroup(EntityPlayerMP playerMP, int targetIndex) {
        UUID 
        senderUUID = CommonReference.getPersistentUUID(playerMP),
        targetUUID;
        if (OxygenHelperServer.isPlayerOnline(targetIndex)) {
            targetUUID = OxygenHelperServer.getPlayerSharedData(targetIndex).getPlayerUUID();
            if (!senderUUID.equals(targetUUID) 
                    && this.canInvite(senderUUID) 
                    && this.canBeInvited(targetUUID)) {
                OxygenHelperServer.sendRequest(playerMP, CommonReference.playerByUUID(targetUUID), 
                        new GroupInviteRequest(GroupsMain.GROUP_REQUEST_ID, senderUUID, CommonReference.getName(playerMP)));
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.REQUEST_RESET.ordinal());
        }
    }

    public void processGroupCreation(EntityPlayer player, UUID leaderUUID) {
        if (this.canBeInvited(CommonReference.getPersistentUUID(player))) {
            if (this.manager.getGroupsDataContainer().haveGroup(leaderUUID))
                this.addToGroup(player, leaderUUID);
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
        group.addPlayer(leaderUUID);
        group.addPlayer(invitedUUID);
        this.manager.getGroupsDataContainer().addGroup(group);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), leaderUUID);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), invitedUUID);

        OxygenHelperServer.addObservedPlayer(leaderUUID, invitedUUID);
        OxygenHelperServer.addObservedPlayer(invitedUUID, leaderUUID);

        OxygenMain.network().sendTo(new CPSyncGroup(group), CommonReference.playerByUUID(leaderUUID));
        OxygenManagerServer.instance().getSharedDataManager().syncObservedPlayersData((EntityPlayerMP) player);
        OxygenMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);
    }   

    private void addToGroup(EntityPlayer player, UUID leaderUUID) {   
        UUID invitedUUID = CommonReference.getPersistentUUID(player);
        Group group = this.manager.getGroupsDataContainer().getGroup(leaderUUID);

        for (UUID uuid : group.getPlayers()) {
            OxygenHelperServer.addObservedPlayer(invitedUUID, uuid);
            OxygenHelperServer.addObservedPlayer(uuid, invitedUUID);
        }

        group.addPlayer(invitedUUID);
        this.manager.getGroupsDataContainer().addGroupAccess(group.getId(), invitedUUID);

        OxygenManagerServer.instance().getSharedDataManager().syncObservedPlayersData((EntityPlayerMP) player);
        OxygenMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);

        for (UUID playerUUID : group.getPlayers())
            if (!playerUUID.equals(invitedUUID) 
                    && OxygenHelperServer.isPlayerOnline(playerUUID))
                OxygenMain.network().sendTo(new CPAddPlayerToGroup(OxygenHelperServer.getPlayerSharedData(invitedUUID)), CommonReference.playerByUUID(playerUUID));
    }

    public void leaveGroup(UUID playerUUID) {
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);

            if (group.getSize() == 2) {
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

            group.removePlayer(playerUUID);
            this.manager.getGroupsDataContainer().removeGroupAccess(playerUUID);

            for (UUID uuid : group.getPlayers()) {
                OxygenHelperServer.removeObservedPlayer(playerUUID, uuid);
                OxygenHelperServer.removeObservedPlayer(uuid, playerUUID);
            }

            if (OxygenHelperServer.isPlayerOnline(playerUUID)) 
                OxygenMain.network().sendTo(new CPLeaveGroup(), CommonReference.playerByUUID(playerUUID));

            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenMain.network().sendTo(new CPRemovePlayerFromGroup(playerUUID), CommonReference.playerByUUID(uuid));

            this.manager.getGroupsDataContainer().setChanged(true);
        }
    }

    public void disbandGroup(Group group) {
        for (UUID playerUUID : group.getPlayers()) {
            this.manager.getGroupsDataContainer().removeGroupAccess(playerUUID);

            for (UUID uuid : group.getPlayers())
                OxygenHelperServer.removeObservedPlayer(playerUUID, uuid);

            if (OxygenHelperServer.isPlayerOnline(playerUUID))
                OxygenMain.network().sendTo(new CPLeaveGroup(), CommonReference.playerByUUID(playerUUID));
        }
        this.manager.getGroupsDataContainer().removeGroup(group.getId());

        this.manager.getGroupsDataContainer().setChanged(true);
    }

    public void startReadinessCheck(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID) 
                && (group = this.manager.getGroupsDataContainer().getGroup(playerUUID)).isLeader(playerUUID)) {
            if (!group.isVoting()) {
                group.startVote();

                EntityPlayerMP player;
                for (UUID uuid : group.getPlayers()) {
                    if (OxygenHelperServer.isPlayerOnline(uuid)) {
                        player = CommonReference.playerByUUID(uuid);
                        OxygenHelperServer.sendChatMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_READINESS_CHECK_STARTED.ordinal());
                        OxygenHelperServer.addNotification(player, new ReadinessCheckRequest(GroupsMain.READINESS_CHECK_REQUEST_ID));
                    }
                }

                OxygenHelperServer.scheduleTask(()->this.stopReadinessCheck(group.getId()), GroupsConfig.READINESS_CHECK_REQUEST_EXPIRE_TIME_SECONDS.getIntValue(), TimeUnit.SECONDS);
            }
        }
    }

    public void processGroupVote(EntityPlayer player) {
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID))
            this.manager.getGroupsDataContainer().getGroup(playerUUID).vote();
    }

    public void stopReadinessCheck(long groupId) {
        if (this.manager.getGroupsDataContainer().groupExist(groupId)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(groupId);
            group.stopVote();
            EnumGroupsChatMessage msg = group.getVoteResult() ? EnumGroupsChatMessage.GROUP_READY : EnumGroupsChatMessage.GROUP_NOT_READY;
            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenHelperServer.sendChatMessage(CommonReference.playerByUUID(uuid), GroupsMain.GROUPS_MOD_INDEX, msg.ordinal());
        }
    }

    public void promoteToLeader(EntityPlayerMP playerMP, int index) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.manager.getGroupsDataContainer().haveGroup(leaderUUID) 
                && (group = this.manager.getGroupsDataContainer().getGroup(leaderUUID)).isLeader(leaderUUID)) {
            if (OxygenHelperServer.isPlayerOnline(index)) {
                UUID playerUUID = OxygenHelperServer.getPlayerSharedData(index).getPlayerUUID();
                group.setLeader(playerUUID);

                for (UUID uuid : group.getPlayers())
                    if (OxygenHelperServer.isPlayerOnline(uuid))
                        OxygenMain.network().sendTo(new CPUpdateLeader(index), CommonReference.playerByUUID(uuid));

                this.manager.getGroupsDataContainer().setChanged(true);
            }
        }
    }

    public void startKickPlayerVoting(EntityPlayerMP playerMP, UUID playerToKickUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.manager.getGroupsDataContainer().haveGroup(playerUUID)) {
            group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
            if (!group.isVoting()) {
                group.startVote();
                PlayerSharedData sharedData = OxygenHelperServer.getPlayerSharedData(playerToKickUUID);
                EntityPlayerMP player;
                for (UUID uuid : group.getPlayers()) {
                    if (OxygenHelperServer.isPlayerOnline(uuid) && !uuid.equals(playerToKickUUID)) {
                        player = CommonReference.playerByUUID(uuid);
                        OxygenHelperServer.sendChatMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.KICK_PLAYER_VOTING_STARTED.ordinal(), sharedData.getUsername());
                        OxygenHelperServer.addNotification(player, new KickPlayerRequest(GroupsMain.READINESS_CHECK_REQUEST_ID, sharedData.getUsername()));
                    }
                }       

                OxygenHelperServer.scheduleTask(()->this.stopKickPlayerVoting(group.getId(), playerToKickUUID), GroupsConfig.READINESS_CHECK_REQUEST_EXPIRE_TIME_SECONDS.getIntValue(), TimeUnit.SECONDS);
            }
        }
    }

    public void stopKickPlayerVoting(long groupId, UUID playerUUID) {
        if (this.manager.getGroupsDataContainer().groupExist(groupId)) {
            Group group = this.manager.getGroupsDataContainer().getGroup(groupId);
            group.stopVote();
            EnumGroupsChatMessage msg = EnumGroupsChatMessage.PLAYER_NOT_KICKED;
            if (group.getVoteResult()) {
                this.leaveGroup(playerUUID);
                msg = EnumGroupsChatMessage.PLAYER_KICKED;
            }
            String username = OxygenHelperServer.getPlayerSharedData(playerUUID).getUsername();
            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isPlayerOnline(uuid))
                    OxygenHelperServer.sendChatMessage(CommonReference.playerByUUID(uuid), GroupsMain.GROUPS_MOD_INDEX, msg.ordinal(), username);
        }
    }

    private boolean canInvite(UUID playerUUID) {
        if (!this.manager.getGroupsDataContainer().haveGroup(playerUUID))
            return true;
        Group group = this.manager.getGroupsDataContainer().getGroup(playerUUID);
        if (group.isLeader(playerUUID) 
                && group.getSize() < GroupsConfig.PLAYERS_PER_PARTY.getIntValue())
            return true;
        return false;
    }

    private boolean canBeInvited(UUID playerUUID) {
        return !this.manager.getGroupsDataContainer().haveGroup(playerUUID);
    }

    public void runGroupsSync() {
        OxygenHelperServer.addRoutineTask(()->{
            UUID[] online;
            EntityPlayerMP[] players;
            EntityPlayerMP playerMP;
            int[] indexes;
            float[] currHealth, maxHealth;
            int count;
            for (Group group : this.manager.getGroupsDataContainer().getGroups()) {
                count = 0;
                online = new UUID[group.getSize()];
                for (UUID uuid : group.getPlayers())
                    if (OxygenHelperServer.isPlayerOnline(uuid))
                        online[count++] = uuid;

                indexes = new int[count];
                currHealth = new float[count];
                maxHealth = new float[count];
                players = new EntityPlayerMP[count];
                count = 0;

                for (UUID uuid : online) {
                    if (uuid == null) break;
                    playerMP = CommonReference.playerByUUID(uuid);
                    if (playerMP == null) return;
                    players[count] = playerMP;
                    indexes[count] = OxygenHelperServer.getPlayerIndex(uuid);
                    currHealth[count] = playerMP.getHealth();
                    maxHealth[count] = playerMP.getMaxHealth();   
                    count++;
                }

                for (EntityPlayerMP player : players)
                    OxygenMain.network().sendTo(new CPSyncGroupData(indexes, currHealth, maxHealth), player);
            }
        });
    }
}
