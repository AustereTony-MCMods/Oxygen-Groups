package austeretony.oxygen_groups.client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_groups.common.main.Group;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPLeaveGroup;
import austeretony.oxygen_groups.common.network.server.SPPromoteToLeader;
import austeretony.oxygen_groups.common.network.server.SPStartKickPlayerVoting;
import austeretony.oxygen_groups.common.network.server.SPStartReadinessCheck;

public class GroupDataManagerClient {

    private final GroupDataClient groupData = new GroupDataClient();

    protected GroupDataManagerClient() {}

    public void scheduleGroupUpdate(Group group) {
        OxygenHelperClient.scheduleTask(()->this.updateGroup(group), 3L, TimeUnit.SECONDS);
    }

    public void updateGroup(Group group) {   
        this.init();
        this.groupData.setLeader(group.getLeader());
        PlayerSharedData sharedData;
        for (UUID playerUUID : group.getPlayers()) {
            sharedData = OxygenHelperClient.getPlayerSharedData(playerUUID);
            if (sharedData != null)
                this.groupData.addPlayerData(new GroupEntryClient(playerUUID, sharedData.getUsername()));
        }
        this.groupData.setActive(true);
        
        OxygenHelperClient.syncSharedData(GroupsMain.GROUP_MENU_SCREEN_ID);
    }

    public void inviteToGroupSynced(int index) {
        OxygenMain.network().sendToServer(new SPInviteToGroup(index));
    }

    public void addToGroup(PlayerSharedData sharedData) {
        OxygenManagerClient.instance().getSharedDataManager().addSharedData(sharedData);
        this.groupData.addPlayerData(new GroupEntryClient(
                sharedData.getPlayerUUID(),
                sharedData.getUsername()));
    }

    public void removeFromGroup(UUID playerUUID) {
        this.groupData.removePlayerData(playerUUID);
    }

    public void leaveGroupSynced() {
        OxygenMain.network().sendToServer(new SPLeaveGroup());
    }

    public void startReadinessCheckSynced() {
        OxygenMain.network().sendToServer(new SPStartReadinessCheck());
    }

    public void startKickPlayerVotingSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPStartKickPlayerVoting(playerUUID));
    }

    public void promoteToLeaderSynced(UUID playerUUID) {
        this.groupData.setLeader(playerUUID);
        OxygenMain.network().sendToServer(new SPPromoteToLeader(OxygenHelperClient.getPlayerIndex(playerUUID)));
    }

    public GroupDataClient getGroupData() {
        return this.groupData;
    }

    public void leaveGroup() {
        this.init();
    }

    public void updateLeader(int index) {
        PlayerSharedData sharedData = OxygenHelperClient.getPlayerSharedData(index);
        if (sharedData != null)
            this.groupData.setLeader(sharedData.getPlayerUUID());
    }

    public void init() {
        this.groupData.setActive(false);
        this.groupData.clear();
    }
}
