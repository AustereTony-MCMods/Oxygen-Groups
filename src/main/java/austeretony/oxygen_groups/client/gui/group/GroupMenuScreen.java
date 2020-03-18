package austeretony.oxygen_groups.client.gui.group;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_groups.client.gui.menu.GroupMenuEntry;
import austeretony.oxygen_groups.client.settings.gui.EnumGroupsGUISetting;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupMenuScreen extends AbstractGUIScreen {

    public static final OxygenMenuEntry GROUP_MENU_ENTRY = new GroupMenuEntry();

    protected GroupSection groupSection;

    public GroupMenuScreen() {
        OxygenHelperClient.syncSharedData(GroupsMain.GROUP_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumGroupsGUISetting.GROUP_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 195, 188).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.groupSection = new GroupSection(this));        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.groupSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void sharedDataSynchronized() {
        this.groupSection.sharedDataSynchronized();
    }
}
