package austeretony.oxygen_groups.client.gui.group;

import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen.client.gui.SynchronizedGUIScreen;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraft.util.ResourceLocation;

public class GroupMenuGUIScreen extends SynchronizedGUIScreen {

    public static final ResourceLocation 
    GROUP_MENU_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/group_menu.png"),
    SETTINGS_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/settings_callback.png"),
    INVITE_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/invite_callback.png"),
    LEAVE_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/leave_callback.png"),
    KICK_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/kick_callback.png"),
    PROMOTE_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/promote_callback.png"),
    READINESS_CHECK_CALLBACK_BACKGROUND = new ResourceLocation(GroupsMain.MODID, "textures/gui/group/readiness_check_callback.png");

    protected GroupGUISection mainSection;

    public GroupMenuGUIScreen() {
        super(GroupsMain.GROUP_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 195, 202).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
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

    @Override
    public void loadData() {
        this.mainSection.sortPlayers(0);
    }
}
