package austeretony.oxygen_groups.common.main;

import austeretony.oxygen.common.api.notification.AbstractNotification;
import austeretony.oxygen.common.notification.EnumNotifications;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.entity.player.EntityPlayer;

public class KickPlayerRequest extends AbstractNotification {

    public final int index;

    public final String username;

    public KickPlayerRequest(int index, String username) {
        this.index = index;
        this.username = username;
    }

    @Override
    public EnumNotifications getType() {
        return EnumNotifications.REQUEST;
    }

    @Override
    public String getDescription() {
        return "groups.request.kickPlayer";
    }

    @Override
    public String[] getArguments() {
        return new String[] {this.username};
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getExpireTime() {
        return GroupsConfig.VOTE_KICK_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void accepted(EntityPlayer player) {
        GroupsManagerServer.instance().processVoteFor(player);
    }

    @Override
    public void rejected(EntityPlayer player) {
        GroupsManagerServer.instance().processVoteAgainst(player);
    }

    @Override
    public void expired() {}
}
