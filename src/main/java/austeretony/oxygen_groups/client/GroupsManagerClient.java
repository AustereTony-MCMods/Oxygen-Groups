package austeretony.oxygen_groups.client;

public class GroupsManagerClient {

    private static GroupsManagerClient instance;

    private final GroupDataManagerClient dataManager = new GroupDataManagerClient();

    private final GroupMenuManager menuManager = new GroupMenuManager();

    private GroupsManagerClient() {}

    public static void create() {
        if (instance == null) 
            instance = new GroupsManagerClient();
    }

    public GroupDataManagerClient getGroupDataManager() {
        return this.dataManager;
    }

    public GroupMenuManager getGroupMenuManager() {
        return this.menuManager;
    }

    public static GroupsManagerClient instance() {
        return instance;
    }

    public void init() {
        this.dataManager.init();
    }
}
