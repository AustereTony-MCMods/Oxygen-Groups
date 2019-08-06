package austeretony.oxygen_groups.client.gui.group;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;

public class GroupMenuBackgroungGUIFiller extends BackgroundGUIFiller {

    public GroupMenuBackgroungGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, GroupMenuGUIScreen.GROUP_MENU_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 14, this.getWidth(), 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//status background
        drawRect(0, 24, this.getWidth(), 34, GUISettings.instance().getAdditionalGUIBackgroundColor());//sorters background
        drawRect(0, 35, this.getWidth() - 3, this.getHeight() - 14, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
        drawRect(this.getWidth() - 2, 35, this.getWidth(), this.getHeight() - 14, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
        drawRect(0, this.getHeight() - 13, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//add button background
    }
}
