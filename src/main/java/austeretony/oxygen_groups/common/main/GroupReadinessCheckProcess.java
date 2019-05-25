package austeretony.oxygen_groups.common.main;

import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;

public class GroupReadinessCheckProcess extends AbstractTemporaryProcess {

    public final long groupId;

    public GroupReadinessCheckProcess(long groupId) {
        this.groupId = groupId;
    }

    @Override
    public int getExpireTime() {
        return GroupsConfig.READINESS_CHECK_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void expired() {
        GroupsManagerServer.instance().stopReadinessCheck(this.groupId);
    }
}
