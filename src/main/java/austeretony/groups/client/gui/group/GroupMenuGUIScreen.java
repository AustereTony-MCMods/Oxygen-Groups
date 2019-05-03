package austeretony.groups.client.gui.group;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.groups.common.main.GroupsMain;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import net.minecraft.util.ResourceLocation;

public class GroupMenuGUIScreen extends AbstractGUIScreen {

    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(GroupsMain.MODID, "textures/gui/players/background.png");

    protected GroupGUISection mainSection;

    private boolean initialized;

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 217, 202).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.mainSection = new GroupGUISection(this);
        this.getWorkspace().initSection(this.mainSection);        
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

    @Override
    public void updateScreen() {    
        super.updateScreen();
        if (!this.initialized//reduce map calls
                && OxygenGUIHelper.isNeedSync(GroupsMain.GROUP_MENU_SCREEN_ID)
                && OxygenGUIHelper.isScreenInitialized(GroupsMain.GROUP_MENU_SCREEN_ID)
                && OxygenGUIHelper.isDataRecieved(GroupsMain.GROUP_MENU_SCREEN_ID)) {
            this.initialized = true;
            OxygenGUIHelper.resetNeedSync(GroupsMain.GROUP_MENU_SCREEN_ID);
            this.mainSection.sortPlayers(0);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        OxygenGUIHelper.resetNeedSync(GroupsMain.GROUP_MENU_SCREEN_ID);
        OxygenGUIHelper.resetScreenInitialized(GroupsMain.GROUP_MENU_SCREEN_ID);
        OxygenGUIHelper.resetDataRecieved(GroupsMain.GROUP_MENU_SCREEN_ID);
    }
}
