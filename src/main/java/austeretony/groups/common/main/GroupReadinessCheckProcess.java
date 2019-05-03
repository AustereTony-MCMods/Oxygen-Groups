package austeretony.groups.common.main;

import austeretony.groups.common.GroupsManagerServer;
import austeretony.groups.common.config.GroupsConfig;
import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;

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
