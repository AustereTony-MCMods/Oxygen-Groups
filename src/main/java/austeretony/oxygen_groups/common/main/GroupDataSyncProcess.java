package austeretony.oxygen_groups.common.main;

import austeretony.oxygen.common.api.process.AbstractPersistentProcess;
import austeretony.oxygen_groups.common.GroupsManagerServer;

public class GroupDataSyncProcess extends AbstractPersistentProcess {

    @Override
    public boolean hasDelay() {
        return true;
    }

    @Override
    public int getExecutionDelay() {
        return 20;
    }

    @Override
    public void execute() {
        GroupsManagerServer.instance().runGroupDataSynchronization();
    }
}
