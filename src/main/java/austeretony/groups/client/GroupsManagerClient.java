package austeretony.groups.client;

import java.util.UUID;

import austeretony.groups.client.gui.group.GroupMenuGUIScreen;
import austeretony.groups.common.Group;
import austeretony.groups.common.main.GroupsMain;
import austeretony.groups.common.network.server.SPGroupsRequest;
import austeretony.groups.common.network.server.SPInviteToGroup;
import austeretony.groups.common.network.server.SPPromoteToLeader;
import austeretony.groups.common.network.server.SPStartKickPlayerVoting;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.api.OxygenHelperClient;
import austeretony.oxygen.common.core.api.ClientReference;
import austeretony.oxygen.common.main.SharedPlayerData;

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

    public void readGroup(Group group) {
        this.haveGroup = true;
        this.groupData.clear();
        this.groupData.setLeader(group.getLeader());
        SharedPlayerData sharedData;
        for (UUID playerUUID : group.getPlayers()) {
            sharedData = OxygenHelperClient.getObservedSharedData(playerUUID);
            if (sharedData != null)//TODO debug
                this.groupData.addPlayerData(new GroupEntryClient(playerUUID, sharedData.getUsername()));
            else
                GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.DOWNLOAD_GROUP_DATA));
        }
    }

    public void downloadGroupDataSynced() {
        this.reset();
        GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.DOWNLOAD_GROUP_DATA_OPEN));
    }

    public void inviteToGroupSynced(UUID targetUUID) {
        GroupsMain.network().sendToServer(new SPInviteToGroup(targetUUID));
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

    public void openGroupMenuSynced() {
        OxygenGUIHelper.needSync(GroupsMain.GROUP_MENU_SCREEN_ID);
        GroupsMain.network().sendToServer(new SPGroupsRequest(SPGroupsRequest.EnumRequest.OPEN_GROUP_MENU));
    }

    public void openGroupMenuDelegated() {
        ClientReference.getMinecraft().addScheduledTask(new Runnable() {

            @Override
            public void run() {
                openGroupMenu();
            }
        });    
    }

    private void openGroupMenu() {
        ClientReference.displayGuiScreen(new GroupMenuGUIScreen());
    }

    public void reset() {
        this.groupData.clear();
        this.haveGroup = false;
    }
}
