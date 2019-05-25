package austeretony.oxygen_groups.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.api.IOxygenTask;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIScreen;
import austeretony.oxygen_groups.common.Group;
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

    public void readGroupOnLoad(Group group) {
        OxygenHelperClient.addRoutineTask(new IOxygenTask() {

            @Override
            public void execute() {
                try {
                    Thread.sleep(5000);//wait for 5 seconds, affects oxygen routine client thread 
                } catch (InterruptedException exception) {      
                    exception.printStackTrace();
                }       
                readGroup(group);
            }});
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
