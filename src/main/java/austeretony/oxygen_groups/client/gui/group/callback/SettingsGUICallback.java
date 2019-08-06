package austeretony.oxygen_groups.client.gui.group.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUICheckBoxButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_groups.client.gui.group.GroupGUISection;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIScreen;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class SettingsGUICallback extends AbstractGUICallback {

    private final GroupMenuGUIScreen screen;

    private final GroupGUISection section;

    private GUICheckBoxButton autoAcceptButton, hideOverlayButton;

    private GUIButton closeButton;

    public SettingsGUICallback(GroupMenuGUIScreen screen, GroupGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new SettingsCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen.gui.callback.settings"), true, GUISettings.instance().getTitleScale()));

        this.addElement(this.hideOverlayButton = new GUICheckBoxButton(2, 16, 6).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor()));
        this.addElement(new GUITextLabel(10, 15).setDisplayText(ClientReference.localize("oxygen_groups.gui.group.setting.hideOverlay"), false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.autoAcceptButton = new GUICheckBoxButton(2, 26, 6).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor()));
        this.addElement(new GUITextLabel(10, 25).setDisplayText(ClientReference.localize("oxygen_groups.gui.group.setting.acceptInvitations"), false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.closeButton = new GUIButton(this.getWidth() - 42, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.closeButton"), true, GUISettings.instance().getButtonTextScale()));
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
            else if (element == this.autoAcceptButton) {
                if (this.autoAcceptButton.isToggled())
                    OxygenHelperClient.setClientSetting(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING_ID, true);
                else
                    OxygenHelperClient.setClientSetting(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING_ID, false);
                OxygenHelperClient.saveClientSettings();
            } else if (element == this.hideOverlayButton) {
                if (this.hideOverlayButton.isToggled())
                    OxygenHelperClient.setClientSetting(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID, true);
                else
                    OxygenHelperClient.setClientSetting(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID, false);
                OxygenHelperClient.saveClientSettings();
            }
        }
    }
}
