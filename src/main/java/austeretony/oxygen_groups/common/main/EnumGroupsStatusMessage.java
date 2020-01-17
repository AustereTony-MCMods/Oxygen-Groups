package austeretony.oxygen_groups.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumGroupsStatusMessage {

    GROUP_REQUEST_ACCEPTED_SENDER("acceptedSender"),
    GROUP_REQUEST_ACCEPTED_TARGET("acceptedTarget"),
    GROUP_REQUEST_REJECTED_SENDER("rejectedSender"),
    GROUP_REQUEST_REJECTED_TARGET("rejectedTarget"),

    JOINED_GROUP("joinedGroup"),
    LEFT_GROUP("leftGroup"),
    PLAYER_KICKED("playerKicked"),
    NEW_LEADER_SET("newLeaderSet"),

    GROUP_SYNCHRONIZED("groupSynchronized");

    private final String status;

    EnumGroupsStatusMessage(String status) {
        this.status = "oxygen_groups.status." + status;
    }

    public String localizedName() {
        return ClientReference.localize(this.status);
    }
}
