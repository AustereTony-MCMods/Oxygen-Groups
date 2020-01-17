package austeretony.oxygen_groups.common;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.notification.AbstractNotification;
import austeretony.oxygen_core.common.notification.EnumNotification;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
    public int getExpireTimeSeconds() {
        return GroupsConfig.GROUP_INVITE_REQUEST_EXPIRE_TIME_SECONDS.asInt();
    }

    @Override
    public void process() {}

    @Override
    public void accepted(EntityPlayer player) {
        if (OxygenHelperServer.isPlayerOnline(this.senderUUID)) {
            GroupsManagerServer.instance().getGroupsDataManager().processGroupCreation(player, this.senderUUID);
            OxygenHelperServer.sendStatusMessage(CommonReference.playerByUUID(this.senderUUID), GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.GROUP_REQUEST_ACCEPTED_SENDER.ordinal());
        }
        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.GROUP_REQUEST_ACCEPTED_TARGET.ordinal());
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isPlayerOnline(this.senderUUID))
            OxygenHelperServer.sendStatusMessage(CommonReference.playerByUUID(this.senderUUID), GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.GROUP_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.GROUP_REQUEST_REJECTED_TARGET.ordinal());
    }

    @Override
    public void expired() {}
}
