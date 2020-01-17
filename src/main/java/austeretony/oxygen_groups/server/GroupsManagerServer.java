package austeretony.oxygen_groups.server;

import austeretony.oxygen_core.common.EnumActivityStatus;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class GroupsManagerServer {

    private static GroupsManagerServer instance;

    private final GroupsDataContainerServer dataContainer = new GroupsDataContainerServer();

    private final GroupsDataManagerServer dataManager;

    private GroupsManagerServer() {
        this.dataManager = new GroupsDataManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.dataContainer);
    }

    public static void create() {
        if (instance == null) {
            instance = new GroupsManagerServer();
            instance.registerPersistentData();
        }
    }

    public static GroupsManagerServer instance() {
        return instance;
    }

    public GroupsDataContainerServer getGroupsDataContainer() {
        return this.dataContainer;
    }

    public GroupsDataManagerServer getGroupsDataManager() {
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
