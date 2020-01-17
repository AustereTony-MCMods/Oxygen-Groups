package austeretony.oxygen_groups.client;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_groups.client.input.GroupsKeyHandler;

public class GroupsManagerClient {

    private static GroupsManagerClient instance;

    private final GroupDataManagerClient dataManager = new GroupDataManagerClient();

    private final GroupMenuManager menuManager = new GroupMenuManager();

    private final GroupsKeyHandler keyHandler = new GroupsKeyHandler();

    private GroupsManagerClient() {
        CommonReference.registerEvent(this.keyHandler);
    }

    public static void create() {
        if (instance == null) 
            instance = new GroupsManagerClient();
    }

    public static GroupsManagerClient instance() {
        return instance;
    }

    public GroupDataManagerClient getGroupDataManager() {
        return this.dataManager;
    }

    public GroupMenuManager getGroupMenuManager() {
        return this.menuManager;
    }

    public GroupsKeyHandler getKeyHandler() {
        return this.keyHandler;
    }

    public void init() {
        this.dataManager.reset();
    }
}
