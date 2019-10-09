package austeretony.oxygen_groups.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsManagerServer {

    private static GroupsManagerServer instance;

    private final GroupsDataContainer dataContainer = new GroupsDataContainer();

    private final GroupsDataManager dataManager;

    private GroupsManagerServer() {
        this.dataManager = new GroupsDataManager(this);
        OxygenHelperServer.registerPersistentData(this.dataContainer);
    }

    private void scheduleRepeatableProcesses() {
        OxygenManagerServer.instance().getExecutionManager().getExecutors().getSchedulerExecutorService().scheduleAtFixedRate(
                ()->this.dataManager.runGroupsSync(), 1L, 1L, TimeUnit.SECONDS);
    }

    public static void create() {
        if (instance == null) {
            instance = new GroupsManagerServer();
            instance.scheduleRepeatableProcesses();
        }
    }

    public static GroupsManagerServer instance() {
        return instance;
    }

    public GroupsDataContainer getGroupsDataContainer() {
        return this.dataContainer;
    }

    public GroupsDataManager getGroupsDataManager() {
        return this.dataManager;
    }

    public void worldLoaded() {
        OxygenHelperServer.loadPersistentDataAsync(this.dataContainer);
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {   
        this.dataManager.onPlayerLoaded(playerMP);
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.dataManager.onPlayerUnloaded(playerMP);
    }

    public void onPlayerChangedStatusActivity(EntityPlayerMP playerMP, EnumActivityStatus newStatus) {
        this.dataManager.onPlayerChangedStatusActivity(playerMP, newStatus);
    }
}
