package austeretony.oxygen_groups.client.gui.group.callback;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackGUIFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_groups.client.gui.group.GroupGUISection;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIScreen;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class SettingsGUICallback extends AbstractGUICallback {

    private final GroupMenuGUIScreen screen;

    private final GroupGUISection section;

    private OxygenCheckBoxGUIButton autoAcceptButton, hideOverlayButton;

    private OxygenGUIButton closeButton;

    public SettingsGUICallback(GroupMenuGUIScreen screen, GroupGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OxygenCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen.gui.callback.settings"), GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.autoAcceptButton = new OxygenCheckBoxGUIButton(6, 18));
        this.addElement(new OxygenGUIText(16, 19, ClientReference.localize("oxygen_groups.gui.group.setting.acceptInvitations"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColorDark()));

        this.addElement(this.hideOverlayButton = new OxygenCheckBoxGUIButton(6, 28));
        this.addElement(new OxygenGUIText(16, 29, ClientReference.localize("oxygen_groups.gui.group.setting.hideOverlay"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColorDark()));

        this.addElement(this.closeButton = new OxygenGUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.closeButton"))); 
    }

    @Override
    public void onOpen() {
        this.hideOverlayButton.setToggled(OxygenHelperClient.getClientSettingBoolean(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID));
        this.autoAcceptButton.setToggled(OxygenHelperClient.getClientSettingBoolean(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING_ID));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.closeButton)
                this.close();
            else if (element == this.autoAcceptButton)
                OxygenHelperClient.setClientSetting(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING_ID, this.autoAcceptButton.isToggled());
            else if (element == this.hideOverlayButton)
                OxygenHelperClient.setClientSetting(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID, this.hideOverlayButton.isToggled());
        }
    }
}
