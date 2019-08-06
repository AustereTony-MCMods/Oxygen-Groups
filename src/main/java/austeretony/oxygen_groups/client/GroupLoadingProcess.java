package austeretony.oxygen_groups.client;

import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;
import austeretony.oxygen_groups.common.main.Group;

public class GroupLoadingProcess extends AbstractTemporaryProcess {

    private final Group group;

    public GroupLoadingProcess(Group group) {
        this.group = group;
    }

    @Override
    public int getExpireTime() {
        return 5;//five seconds
    }

    @Override
    public void process() {}

    @Override
    public void expired() {
        GroupsManagerClient.instance().readGroup(this.group);
    }
}
