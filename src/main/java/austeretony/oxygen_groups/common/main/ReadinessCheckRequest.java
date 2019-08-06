package austeretony.oxygen_groups.common.main;

import austeretony.oxygen.common.api.notification.AbstractNotification;
import austeretony.oxygen.common.notification.EnumNotification;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.entity.player.EntityPlayer;

public class ReadinessCheckRequest extends AbstractNotification {

    public final int index;

    public ReadinessCheckRequest(int index) {
        this.index = index;
    }

    @Override
    public EnumNotification getType() {
        return EnumNotification.REQUEST;
    }

    @Override
    public String getDescription() {
        return "oxygen_groups.request.readinessCheck";
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getExpireTime() {
        return GroupsConfig.READINESS_CHECK_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void accepted(EntityPlayer player) {
        GroupsManagerServer.instance().processVoteFor(player);
    }

    @Override
    public void rejected(EntityPlayer player) {}

    @Override
    public void expired() {}
}
