package austeretony.oxygen_groups.client.gui.group;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.AbstractMenuEntry;

public class GroupMenuEntry extends AbstractMenuEntry {

    @Override
    public String getName() {
        return "oxygen_groups.gui.groupMenu.title";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new GroupMenuGUIScreen());
    }
}
