package austeretony.oxygen_groups.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.EnumOxygenChatMessage;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.EnumGroupsChatMessage;
import austeretony.oxygen_groups.common.main.Group;
import austeretony.oxygen_groups.common.main.GroupInviteRequest;
import austeretony.oxygen_groups.common.main.GroupReadinessCheckProcess;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.main.KickPlayerRequest;
import austeretony.oxygen_groups.common.main.KickPlayerVotingProcess;
import austeretony.oxygen_groups.common.main.ReadinessCheckRequest;
import austeretony.oxygen_groups.common.network.client.CPAddPlayerToGroup;
import austeretony.oxygen_groups.common.network.client.CPGroupsCommand;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroupOnLoad;
import austeretony.oxygen_groups.common.network.client.CPSyncPlayersHealth;
import austeretony.oxygen_groups.common.network.client.CPUpdateLeader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsManagerServer implements IPersistentData {

    private static GroupsManagerServer instance;

    private final Map<Long, Group> groups = new ConcurrentHashMap<Long, Group>();

    private final Map<UUID, Long> groupAccess = new ConcurrentHashMap<UUID, Long>();

    public static void create() {
        if (instance == null) 
            instance = new GroupsManagerServer();
    }

    public static GroupsManagerServer instance() {
        return instance;
    }

    public boolean groupExist(long groupId) {
        return this.groups.containsKey(groupId);
    }

    public Group getGroup(long groupId) {
        return this.groups.get(groupId);
    }

    public Group getGroup(UUID playerUUID) {
        return this.groups.get(this.groupAccess.get(playerUUID));
    }

    public boolean haveGroup(UUID playerUUID) {
        return this.groupAccess.containsKey(playerUUID);
    }

    public void inviteToGroup(EntityPlayerMP playerMP, int targetIndex) {
        UUID 
        senderUUID = CommonReference.getPersistentUUID(playerMP),
        targetUUID;
        if (OxygenHelperServer.isOnline(targetIndex)) {
            targetUUID = OxygenHelperServer.getSharedPlayerData(targetIndex).getPlayerUUID();
            if (!senderUUID.equals(targetUUID) 
                    && this.canInvite(senderUUID) 
                    && this.canBeInvited(targetUUID)) {
                OxygenHelperServer.sendRequest(playerMP, CommonReference.playerByUUID(targetUUID), 
                        new GroupInviteRequest(GroupsMain.GROUP_REQUEST_ID, senderUUID, CommonReference.getName(playerMP)), true);
            } else
                OxygenHelperServer.sendMessage(playerMP, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessage.REQUEST_RESET.ordinal());
        }
    }

    public void processAcceptedGroupRequest(EntityPlayer player, UUID leaderUUID) {
        if (this.canBeInvited(CommonReference.getPersistentUUID(player))) {
            if (this.haveGroup(leaderUUID))
                this.addToGroup(player, leaderUUID);
            else
                this.createGroup(player, leaderUUID);
            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    private void createGroup(EntityPlayer player, UUID leaderUUID) {
        UUID invitedUUID = CommonReference.getPersistentUUID(player);
        Group group = new Group();
        group.createId();
        group.setLeader(leaderUUID);
        group.addPlayer(leaderUUID);
        group.addPlayer(invitedUUID);
        this.groups.put(group.getId(), group);
        this.groupAccess.put(leaderUUID, group.getId());
        this.groupAccess.put(invitedUUID, group.getId());

        OxygenHelperServer.addObservedPlayer(leaderUUID, invitedUUID, false);
        OxygenHelperServer.addObservedPlayer(invitedUUID, leaderUUID, true);

        GroupsMain.network().sendTo(new CPSyncGroup(group), CommonReference.playerByUUID(leaderUUID));
        GroupsMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);
    }   

    private void addToGroup(EntityPlayer player, UUID leaderUUID) {   
        UUID invitedUUID = CommonReference.getPersistentUUID(player);
        Group group = this.getGroup(leaderUUID);

        for (UUID uuid : group.getPlayers()) {
            OxygenHelperServer.addObservedPlayer(invitedUUID, uuid, false);
            OxygenHelperServer.addObservedPlayer(uuid, invitedUUID, false);
        }
        OxygenHelperServer.saveObservedPlayersData();

        group.addPlayer(invitedUUID);
        this.groupAccess.put(invitedUUID, group.getId());

        OxygenHelperServer.syncObservedPlayersData((EntityPlayerMP) player);       
        GroupsMain.network().sendTo(new CPSyncGroup(group), (EntityPlayerMP) player);

        for (UUID playerUUID : group.getPlayers())
            if (!playerUUID.equals(invitedUUID) 
                    && OxygenHelperServer.isOnline(playerUUID))
                GroupsMain.network().sendTo(new CPAddPlayerToGroup(OxygenHelperServer.getPlayerIndex(invitedUUID)), CommonReference.playerByUUID(playerUUID));
    }

    public void leaveGroup(UUID playerUUID) {
        if (this.haveGroup(playerUUID)) {
            Group group = this.getGroup(playerUUID);

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
            this.groupAccess.remove(playerUUID);

            for (UUID uuid : group.getPlayers()) {
                OxygenHelperServer.removeObservedPlayer(playerUUID, uuid, false);
                OxygenHelperServer.removeObservedPlayer(uuid, playerUUID, false);
            }
            OxygenHelperServer.saveObservedPlayersData();

            if (OxygenHelperServer.isOnline(playerUUID)) 
                GroupsMain.network().sendTo(new CPGroupsCommand(CPGroupsCommand.EnumCommand.LEAVE_GROUP), CommonReference.playerByUUID(playerUUID));

            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isOnline(uuid))
                    GroupsMain.network().sendTo(new CPRemovePlayerFromGroup(playerUUID), CommonReference.playerByUUID(uuid));

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void disbandGroup(Group group) {
        for (UUID playerUUID : group.getPlayers()) {
            this.groupAccess.remove(playerUUID);

            for (UUID uuid : group.getPlayers())
                OxygenHelperServer.removeObservedPlayer(playerUUID, uuid, false);

            if (OxygenHelperServer.isOnline(playerUUID))
                GroupsMain.network().sendTo(new CPGroupsCommand(CPGroupsCommand.EnumCommand.LEAVE_GROUP), CommonReference.playerByUUID(playerUUID));
        }
        OxygenHelperServer.saveObservedPlayersData();
        this.groups.remove(group.getId());

        OxygenHelperServer.savePersistentDataDelegated(this);
    }

    public void startReadinessCheck(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.haveGroup(playerUUID) && (group = this.getGroup(playerUUID)).isLeader(playerUUID)) {
            if (!group.isVoting()) {
                group.startVote();

                EntityPlayerMP player;
                for (UUID uuid : group.getPlayers()) {
                    if (OxygenHelperServer.isOnline(uuid)) {
                        player = CommonReference.playerByUUID(uuid);
                        OxygenHelperServer.sendMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_READINESS_CHECK_STARTED.ordinal());
                        OxygenHelperServer.addNotification(player, new ReadinessCheckRequest(GroupsMain.READINESS_CHECK_REQUEST_ID));
                    }
                }

                OxygenHelperServer.addGlobalTemporaryProcess(new GroupReadinessCheckProcess(group.getId()));
            }
        }
    }

    public void processVoteFor(EntityPlayer player) {
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        if (this.haveGroup(playerUUID))
            this.getGroup(playerUUID).voteFor();
    }

    public void stopReadinessCheck(long groupId) {
        if (this.groupExist(groupId)) {
            Group group = GroupsManagerServer.instance().getGroup(groupId);
            group.stopVote();
            EnumGroupsChatMessage msg = group.getVoteResult() ? EnumGroupsChatMessage.GROUP_READY : EnumGroupsChatMessage.GROUP_NOT_READY;
            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isOnline(uuid))
                    OxygenHelperServer.sendMessage(CommonReference.playerByUUID(uuid), GroupsMain.GROUPS_MOD_INDEX, msg.ordinal());
        }
    }

    public void promoteToLeader(EntityPlayerMP playerMP, int index) {
        UUID leaderUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.haveGroup(leaderUUID) && (group = this.getGroup(leaderUUID)).isLeader(leaderUUID)) {
            if (OxygenHelperServer.isOnline(index)) {
                UUID playerUUID = OxygenHelperServer.getSharedPlayerData(index).getPlayerUUID();
                group.setLeader(playerUUID);

                for (UUID uuid : group.getPlayers())
                    if (OxygenHelperServer.isOnline(uuid))
                        GroupsMain.network().sendTo(new CPUpdateLeader(index), CommonReference.playerByUUID(uuid));

                OxygenHelperServer.savePersistentDataDelegated(this);
            }
        }
    }

    public void startKickPlayerVoting(EntityPlayerMP playerMP, UUID kickUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        Group group;
        if (this.haveGroup(playerUUID)) {
            group = this.getGroup(playerUUID);
            if (!group.isVoting()) {
                group.startVote();

                SharedPlayerData kickData = OxygenHelperServer.getPersistentSharedData(kickUUID);

                EntityPlayerMP player;
                for (UUID uuid : group.getPlayers()) {
                    if (OxygenHelperServer.isOnline(uuid) && !uuid.equals(kickUUID)) {
                        player = CommonReference.playerByUUID(uuid);
                        OxygenHelperServer.sendMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.KICK_PLAYER_VOTING_STARTED.ordinal(), kickData.getUsername());
                        OxygenHelperServer.addNotification(player, new KickPlayerRequest(GroupsMain.READINESS_CHECK_REQUEST_ID, kickData.getUsername()));
                    }
                }       

                OxygenHelperServer.addGlobalTemporaryProcess(new KickPlayerVotingProcess(group.getId(), kickUUID));
            }
        }
    }

    public void stopKickPlayerVoting(long groupId, UUID playerUUID) {
        if (this.groupExist(groupId)) {
            Group group = GroupsManagerServer.instance().getGroup(groupId);
            group.stopVote();
            EnumGroupsChatMessage msg = EnumGroupsChatMessage.PLAYER_NOT_KICKED;
            if (group.getVoteResult()) {
                this.leaveGroup(playerUUID);
                msg = EnumGroupsChatMessage.PLAYER_KICKED;
            }
            String username = OxygenHelperServer.getPersistentSharedData(playerUUID).getUsername();
            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isOnline(uuid))
                    OxygenHelperServer.sendMessage(CommonReference.playerByUUID(uuid), GroupsMain.GROUPS_MOD_INDEX, msg.ordinal(), username);
        }
    }

    //TODO onPlayerLoggedIn()
    public void onPlayerLoaded(EntityPlayer player) {    
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        if (this.haveGroup(playerUUID))
            GroupsMain.network().sendTo(new CPSyncGroupOnLoad(this.getGroup(playerUUID)), (EntityPlayerMP) player);
    }

    private boolean canInvite(UUID playerUUID) {
        if (!this.haveGroup(playerUUID))
            return true;
        Group group = this.getGroup(playerUUID);
        if (group.isLeader(playerUUID) && group.getSize() < GroupsConfig.PLAYERS_PER_PARTY.getIntValue())
            return true;
        return false;
    }

    private boolean canBeInvited(UUID playerUUID) {
        return !this.haveGroup(playerUUID);
    }

    public void runGroupDataSynchronization() {
        UUID[] online;
        EntityPlayerMP[] players;
        EntityPlayerMP playerMP;
        int[] indexes;
        float[] currHealth, maxHealth;
        int count;
        for (Group group : this.groups.values()) {
            count = 0;
            online = new UUID[group.getSize()];
            for (UUID uuid : group.getPlayers())
                if (OxygenHelperServer.isOnline(uuid))
                    online[count++] = uuid;

            indexes = new int[count];
            currHealth = new float[count];
            maxHealth = new float[count];
            players = new EntityPlayerMP[count];
            count = 0;

            for (UUID uuid : online) {
                if (uuid == null) break;
                playerMP = CommonReference.playerByUUID(uuid);
                if (playerMP == null) return;//TODO Debug
                players[count] = playerMP;
                indexes[count] = OxygenHelperServer.getPlayerIndex(uuid);
                currHealth[count] = playerMP.getHealth();
                maxHealth[count] = playerMP.getMaxHealth();   
                count++;
            }

            for (EntityPlayerMP player : players)
                GroupsMain.routineNetwork().sendTo(new CPSyncPlayersHealth(indexes, currHealth, maxHealth), player);
        }
    }

    @Override
    public String getName() {   
        return "groups_data";
    }

    @Override
    public String getModId() {
        return GroupsMain.MODID;
    }

    @Override
    public String getPath() {
        return "world/groups/groups.dat";
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.groups.size(), bos);
        for (Group group : this.groups.values())
            group.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        Group group;
        for (int i = 0; i < amount; i++) {
            group = Group.read(bis);
            this.groups.put(group.getId(), group);
            for (UUID playerUUID : group.getPlayers())
                this.groupAccess.put(playerUUID, group.getId());
        }
    }

    public void reset() {
        this.groups.clear();
        this.groupAccess.clear();
    }
}
