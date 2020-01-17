package austeretony.oxygen_groups.client.gui.menu;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_groups.client.gui.group.GroupMenuScreen;
import austeretony.oxygen_groups.client.settings.EnumGroupsClientSetting;

public class GroupMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return 20;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_groups.gui.groupMenu.title");
    }

    @Override
    public int getKeyCode() {
        return Keyboard.KEY_P;
    }

    @Override
    public boolean isValid() {
        return EnumGroupsClientSetting.ADD_GROUP_MENU.get().asBoolean();
    }

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new GroupMenuScreen());
    }
}
