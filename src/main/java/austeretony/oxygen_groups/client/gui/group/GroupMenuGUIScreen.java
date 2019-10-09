package austeretony.oxygen_groups.client.gui.group;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupMenuGUIScreen extends AbstractGUIScreen {

    public static final OxygenMenuEntry GROUP_MENU_ENTRY = new GroupMenuEntry();

    protected GroupGUISection mainSection;

    public GroupMenuGUIScreen() {
        OxygenHelperClient.syncSharedData(GroupsMain.GROUP_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 195, 179).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.mainSection = new GroupGUISection(this));        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.mainSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void sharedDataSynchronized() {
        this.mainSection.sharedDataSynchronized();
    }
}
