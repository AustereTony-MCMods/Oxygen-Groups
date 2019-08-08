package austeretony.oxygen_groups.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivilege;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen_groups.common.main.Group;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.server.SPGroupsRequest;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPPromoteToLeader;
import austeretony.oxygen_groups.common.network.server.SPStartKickPlayerVoting;

public class GroupsManagerClient {

    private static GroupsManagerClient instance;

    private final GroupDataClient groupData;

    private volatile boolean haveGroup;

    private GroupsManagerClient() {
        this.groupData = new GroupDataClient();
    }   

    public static void create() {
        if (instance == null) 
            instance = new GroupsManagerClient();
    }

    public static GroupsManagerClient instance() {
        return instance;
    }

    public void readGroupDelayed(Group group) {
        OxygenHelperClient.addTemporaryProcess(new GroupLoadingProcess(group));
    }

    public void readGroup(Group group) {   
        this.haveGroup = true;  
        this.groupData.clear();
        this.groupData.setLeader(group.getLeader());
        SharedPlayerData sharedData;
        for (UUID playerUUID : group.getPlayers()) {
            sharedData = OxygenHelperClient.getObservedSharedData(playerUUID);
            if (sharedData != null)
                this.groupData.addPlayerData(new GroupEntryClient(playerUUID, sharedData.getUsername()));
        }
    }

    public void downloadGroupDataSynced() {
        GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.DOWNLOAD_GROUP_DATA));
    }

    public void inviteToGroupSynced(int index) {
        GroupsMain.network().sendToServer(new SPInviteToGroup(index));
    }

    public void addToGroup(int index) {
        SharedPlayerData sharedData = OxygenHelperClient.getSharedPlayerData(index);
        this.groupData.addPlayerData(new GroupEntryClient(
                sharedData.getPlayerUUID(),
                sharedData.getUsername()));
    }

    public void removeFromGroup(UUID playerUUID) {
        this.groupData.removePlayerData(playerUUID);
    }

    public void leaveGroupSynced() {
        GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.LEAVE_GROUP));
    }

    public void leaveGroup() {
        if (this.haveGroup())
            this.reset();
    }

    public void startReadinessCheckSynced() {
        GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.START_READINESS_CHECK));
    }

    public void startKickPlayerVotingSynced(UUID playerUUID) {
        GroupsMain.network().sendToServer(new SPStartKickPlayerVoting(playerUUID));
    }

    public void promoteToLeaderSynced(UUID playerUUID) {
        this.groupData.setLeader(playerUUID);
        GroupsMain.network().sendToServer(new SPPromoteToLeader(OxygenHelperClient.getPlayerIndex(playerUUID)));
    }

    public void updateLeader(int index) {
        SharedPlayerData sharedData = OxygenHelperClient.getSharedPlayerData(index);
        this.groupData.setLeader(sharedData.getPlayerUUID());
    }

    public boolean haveGroup() {
        return this.haveGroup;
    }

    public void setHaveGroup(boolean flag) {
        this.haveGroup = flag;
    }

    public GroupDataClient getGroupData() {
        return this.groupData;
    }

    public static EnumActivityStatus getActivityStatus(SharedPlayerData sharedData) {
        EnumActivityStatus activityStatus = EnumActivityStatus.OFFLINE;
        if (OxygenHelperClient.isOnline(sharedData.getPlayerUUID()))
            activityStatus = OxygenHelperClient.getPlayerStatus(sharedData.getPlayerUUID());
        return activityStatus;
    }

    public static boolean isPlayerAvailable(String username) {
        if (username.equals(OxygenHelperClient.getSharedClientPlayerData().getUsername()))
            return false;
        SharedPlayerData sharedData = OxygenHelperClient.getSharedPlayerData(username);
        if (sharedData != null) {
            if (OxygenHelperClient.getPlayerStatus(sharedData) != OxygenPlayerData.EnumActivityStatus.OFFLINE || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivilege.EXPOSE_PLAYERS_OFFLINE.toString(), false))
                return true;
        }
        return false;
    }

    public void reset() {
        this.groupData.clear();
        this.haveGroup = false;
    }
}
