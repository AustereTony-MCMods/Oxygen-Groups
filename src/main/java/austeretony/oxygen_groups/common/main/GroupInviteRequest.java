package austeretony.oxygen_groups.common.main;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.notification.AbstractNotification;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.notification.EnumNotification;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.entity.player.EntityPlayer;

public class GroupInviteRequest extends AbstractNotification {

    public final int index;

    public final UUID senderUUID;

    public final String senderUsername;

    public GroupInviteRequest(int index, UUID senderUUID, String senderUsername) {
        this.index = index;
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
    }

    @Override
    public EnumNotification getType() {
        return EnumNotification.REQUEST;
    }

    @Override
    public String getDescription() {
        return "oxygen_groups.request.groupInvite";
    }

    @Override
    public String[] getArguments() {
        return new String[] {this.senderUsername};
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getExpireTime() {
        return GroupsConfig.GROUP_INVITE_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void accepted(EntityPlayer player) {
        GroupsManagerServer.instance().processAcceptedGroupRequest(player, this.senderUUID);

        if (OxygenHelperServer.isOnline(this.senderUUID))
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_REQUEST_ACCEPTED_SENDER.ordinal());
        OxygenHelperServer.sendMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_REQUEST_ACCEPTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isOnline(this.senderUUID))
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendMessage(player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsChatMessage.GROUP_REQUEST_REJECTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void expired() {
        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }
}
