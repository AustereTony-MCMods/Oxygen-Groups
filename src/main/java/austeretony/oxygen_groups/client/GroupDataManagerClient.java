package austeretony.oxygen_groups.client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_groups.common.Group;
import austeretony.oxygen_groups.common.network.server.SPGroupMemberOperation;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPLeaveGroup;

public class GroupDataManagerClient {

    private final GroupDataClient groupData = new GroupDataClient();

    protected GroupDataManagerClient() {}

    public void scheduleGroupUpdate(Group group) {
        OxygenHelperClient.scheduleTask(()->this.updateGroup(group), 5L, TimeUnit.SECONDS);
    }

    public void updateGroup(Group group) {   
        this.reset();

        this.groupData.setLeader(group.getLeader());
        PlayerSharedData sharedData;
        for (UUID memberUUID : group.getMembers()) {
            sharedData = OxygenHelperClient.getPlayerSharedData(memberUUID);
            if (sharedData != null)
                this.groupData.addMember(memberUUID);
        }
        this.groupData.setActive(true);
    }

    public void inviteToGroupSynced(int index) {
        OxygenMain.network().sendToServer(new SPInviteToGroup(index));
    }

    public void addNewGroupMember(PlayerSharedData sharedData) {
        OxygenManagerClient.instance().getSharedDataManager().addSharedData(sharedData);
        this.groupData.addMember(sharedData.getPlayerUUID());
    }

    public void removeGroupMember(UUID playerUUID) {
        this.groupData.removeMember(playerUUID);
    }

    public void leaveGroupSynced() {
        OxygenMain.network().sendToServer(new SPLeaveGroup());
    }

    public void kickPlayerSynced(UUID playerUUID) {    
        OxygenMain.network().sendToServer(new SPGroupMemberOperation(playerUUID, SPGroupMemberOperation.EnumOperation.KICK));
    }

    public void promoteToLeaderSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPGroupMemberOperation(playerUUID, SPGroupMemberOperation.EnumOperation.PROMOTE_TO_LEADER));
    }

    public GroupDataClient getGroupData() {
        return this.groupData;
    }

    public void leaveGroup() {
        this.reset();
    }

    public void updateLeader(int index) {
        PlayerSharedData sharedData = OxygenHelperClient.getPlayerSharedData(index);
        if (sharedData != null)
            this.groupData.setLeader(sharedData.getPlayerUUID());
    }

    public void reset() {
        this.groupData.setActive(false);
        this.groupData.clear();
    }
}
