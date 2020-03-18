package austeretony.oxygen_groups.server;

import austeretony.oxygen_core.common.EnumActivityStatus;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
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

    public void playerLoaded(EntityPlayerMP playerMP) {   
        this.dataManager.playerLoaded(playerMP);
    }

    public void playerUnloaded(EntityPlayerMP playerMP) {
        this.dataManager.playerUnloaded(playerMP);
    }

    public void playerChangedStatusActivity(EntityPlayerMP playerMP, EnumActivityStatus newStatus) {
        this.dataManager.playerChangedStatusActivity(playerMP, newStatus);
    }

    public void sendStatusMessage(EntityPlayerMP playerMP, EnumGroupsStatusMessage message) {
        OxygenHelperServer.sendStatusMessage(playerMP, GroupsMain.GROUPS_MOD_INDEX, message.ordinal());
    }
}
