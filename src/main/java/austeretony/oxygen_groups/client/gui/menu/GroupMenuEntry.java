package austeretony.oxygen_groups.client.gui.menu;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_groups.client.GroupMenuManager;
import austeretony.oxygen_groups.client.settings.EnumGroupsClientSetting;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return GroupsMain.GROUP_MENU_SCREEN_ID;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_groups.gui.groupMenu.title");
    }

    @Override
    public int getKeyCode() {
        return GroupsConfig.GROUP_MENU_KEY.asInt();
    }

    @Override
    public boolean isValid() {
        return EnumGroupsClientSetting.ADD_GROUP_MENU.get().asBoolean();
    }

    @Override
    public void open() {
        GroupMenuManager.openGroupMenu();
    }
}
