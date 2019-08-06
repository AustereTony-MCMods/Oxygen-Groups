package austeretony.oxygen_groups.client.gui.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.contextmenu.GUIContextMenu;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.list.GUIDropDownList;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUIOrientation;
import austeretony.oxygen.client.OxygenManagerClient;
import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIDropDownElement;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.callback.InviteGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.KickGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.LeaveGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.PromoteGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.ReadinessCheckGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.SettingsGUICallback;
import austeretony.oxygen_groups.client.gui.group.context.KickPlayerContextAction;
import austeretony.oxygen_groups.client.gui.group.context.PromoteToLeaderContextAction;
import austeretony.oxygen_groups.client.input.GroupsKeyHandler;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupGUISection extends AbstractGUISection {

    private GroupMenuGUIScreen screen;

    private GUIButton settingsButton, refreshButton, inviteButton, leaveButton, checkButton, sortDownStatusButton, sortUpStatusButton, 
    sortDownUsernameButton, sortUpUsernameButton;

    private GUITextLabel playersOnlineTextLabel, playerNameTextLabel;

    private GUIButtonPanel playersPanel;

    private GUIDropDownList statusDropDownList;

    private EnumActivityStatus clientStatus;  

    private GUIImageLabel statusImageLabel;

    private AbstractGUICallback settingsCallback, inviteCallback, leaveGroupCallback, readinessCheckCallback, kickPlayerCallback, 
    promoteToLeaderCallback;

    private GroupEntryGUIButton currentEntry;

    public GroupGUISection(GroupMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new GroupMenuBackgroungGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        String title = ClientReference.localize("oxygen_groups.gui.groupMenu");
        this.addElement(new GUITextLabel(2, 4).setDisplayText(title, false, GUISettings.instance().getTitleScale()));
        this.addElement(this.settingsButton = new GUIButton(this.getWidth() - 5, 0, 5, 5).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.TRIANGLE_TOP_RIGHT_CORNER_ICONS, 5, 5).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.settings"), GUISettings.instance().getTooltipScale()));

        this.addElement(this.refreshButton = new GUIButton(2, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));         
        this.addElement(this.playerNameTextLabel = new GUITextLabel(14, 15).setDisplayText(OxygenHelperClient.getSharedClientPlayerData().getUsername(), false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.playersOnlineTextLabel = new GUITextLabel(0, 15).setTextScale(GUISettings.instance().getSubTextScale()).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.online"), GUISettings.instance().getTooltipScale())); 

        this.addElement(this.sortDownStatusButton = new GUIButton(7, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpStatusButton = new GUIButton(7, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortDownUsernameButton = new GUIButton(19, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpUsernameButton = new GUIButton(19, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(24, 25).setDisplayText(ClientReference.localize("oxygen.gui.username")).setTextScale(GUISettings.instance().getSubTextScale())); 
        this.addElement(new GUITextLabel(100, 25).setDisplayText(ClientReference.localize("oxygen.gui.dimension")).setTextScale(GUISettings.instance().getSubTextScale())); 

        this.playersPanel = new GUIButtonPanel(EnumGUIOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getPanelTextScale());
        this.addElement(this.playersPanel);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.getIntValue(), 14, 50), 14);
        this.playersPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, this.getHeight() - 49);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);    

        GUIContextMenu menu = new GUIContextMenu(GUISettings.instance().getContextMenuWidth(), 10).setScale(GUISettings.instance().getContextMenuScale()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 2);
        menu.setOpenSound(OxygenSoundEffects.CONTEXT_OPEN.soundEvent);
        menu.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        this.playersPanel.initContextMenu(menu);
        menu.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
        menu.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
        menu.addElement(new KickPlayerContextAction(this));
        menu.addElement(new PromoteToLeaderContextAction(this));

        //Support
        for (AbstractContextAction action : OxygenGUIHelper.getContextActions(GroupsMain.GROUP_MENU_SCREEN_ID))
            menu.addElement(action);

        this.addElement(this.inviteButton = new GUIButton(4, this.getHeight() - 11, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen_groups.gui.inviteButton"), true, GUISettings.instance().getButtonTextScale()));     
        this.lockInviteButton();

        this.addElement(this.leaveButton = new GUIButton(54, this.getHeight() - 11, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen_groups.gui.leaveButton"), true, GUISettings.instance().getButtonTextScale())); 
        if (!GroupsManagerClient.instance().haveGroup())
            this.leaveButton.disable();

        this.addElement(this.checkButton = new GUIButton(104, this.getHeight() - 11, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen_groups.gui.checkButton"), true, GUISettings.instance().getButtonTextScale()));   
        if (!GroupsManagerClient.instance().haveGroup() || !GroupsManagerClient.instance().getGroupData().isClientLeader())
            this.checkButton.disable();

        this.clientStatus = OxygenHelperClient.getClientPlayerStatus();
        int statusOffset = this.playerNameTextLabel.getX() + this.textWidth(this.playerNameTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale());
        this.addElement(this.statusImageLabel = new GUIImageLabel(statusOffset + 4, 17).setTexture(OxygenGUITextures.STATUS_ICONS, 3, 3, this.clientStatus.ordinal() * 3, 0, 12, 3));   
        this.statusDropDownList = new GUIDropDownList(statusOffset + 10, 16, GUISettings.instance().getDropDownListWidth(), 10).setScale(GUISettings.instance().getDropDownListScale()).setDisplayText(this.clientStatus.localizedName()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 1);
        this.statusDropDownList.setOpenSound(OxygenSoundEffects.DROP_DOWN_LIST_OPEN.soundEvent);
        this.statusDropDownList.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        IndexedGUIDropDownElement<EnumActivityStatus> statusElement;
        for (EnumActivityStatus status : EnumActivityStatus.values()) {
            statusElement = new IndexedGUIDropDownElement(status);
            statusElement.setDisplayText(status.localizedName());
            statusElement.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
            statusElement.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.statusDropDownList.addElement(statusElement);
        }
        this.addElement(this.statusDropDownList);   

        this.settingsCallback = new SettingsGUICallback(this.screen, this, 140, 48).enableDefaultBackground();

        this.inviteCallback = new InviteGUICallback(this.screen, this, 140, 68).enableDefaultBackground();
        this.leaveGroupCallback = new LeaveGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.readinessCheckCallback = new ReadinessCheckGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.kickPlayerCallback = new KickGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.promoteToLeaderCallback = new PromoteGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
    }

    public void sortPlayers(int mode) {
        List<SharedPlayerData> members = new ArrayList<SharedPlayerData>();

        for (UUID playerUUID : GroupsManagerClient.instance().getGroupData().getPlayersUUIDs())
            members.add(OxygenHelperClient.getObservedSharedData(playerUUID));

        if (mode == 0)
            Collections.sort(members, (s1, s2)->GroupsManagerClient.getActivityStatus(s1).ordinal() - GroupsManagerClient.getActivityStatus(s2).ordinal());
        else if (mode == 1)
            Collections.sort(members, (s1, s2)->GroupsManagerClient.getActivityStatus(s2).ordinal() - GroupsManagerClient.getActivityStatus(s1).ordinal());
        else if (mode == 2)
            Collections.sort(members, (s1, s2)->s1.getUsername().compareTo(s2.getUsername()));
        else if (mode == 3)
            Collections.sort(members, (s1, s2)->s2.getUsername().compareTo(s1.getUsername()));

        this.playersPanel.reset();
        GroupEntryGUIButton button;
        int onlinePlayers = 0;
        EnumActivityStatus status;
        for (SharedPlayerData data : members) {           
            status = EnumActivityStatus.OFFLINE;
            if (OxygenHelperClient.isOnline(data.getPlayerUUID())) {
                status = OxygenHelperClient.getPlayerStatus(data);
                if (status != EnumActivityStatus.OFFLINE)
                    onlinePlayers++;
            }   
            button = new GroupEntryGUIButton(data, status);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getDisabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.playersPanel.addButton(button);
        }

        this.playersPanel.getScroller().resetPosition();
        this.playersPanel.getScroller().getSlider().reset();

        this.playersOnlineTextLabel.setDisplayText(onlinePlayers + " / " + members.size());
        this.playersOnlineTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.playersOnlineTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));

        this.sortUpStatusButton.toggle();
        this.sortDownStatusButton.setToggled(false);
        this.sortDownUsernameButton.setToggled(false);
        this.sortUpUsernameButton.setToggled(false);
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.settingsButton)
                this.settingsCallback.open();
            else if (element == this.refreshButton)
                this.sortPlayers(0);
            else if (element == this.sortDownStatusButton) {
                if (!this.sortDownStatusButton.isToggled()) {
                    this.sortPlayers(1);
                    this.sortUpStatusButton.setToggled(false);
                    this.sortDownStatusButton.toggle(); 

                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.setToggled(false);
                }
            } else if (element == this.sortUpStatusButton) {
                if (!this.sortUpStatusButton.isToggled()) {
                    this.sortPlayers(0);
                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.toggle();

                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.setToggled(false);
                }
            } else if (element == this.sortDownUsernameButton) {
                if (!this.sortDownUsernameButton.isToggled()) {
                    this.sortPlayers(3);
                    this.sortUpUsernameButton.setToggled(false);
                    this.sortDownUsernameButton.toggle(); 

                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.setToggled(false);
                }
            } else if (element == this.sortUpUsernameButton) {
                if (!this.sortUpUsernameButton.isToggled()) {
                    this.sortPlayers(2);
                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.toggle();

                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.setToggled(false);
                }
            } else if (element == this.inviteButton)
                this.inviteCallback.open();
            else if (element == this.leaveButton)
                this.leaveGroupCallback.open();
            else if (element == this.checkButton)
                this.readinessCheckCallback.open();
            else if (element instanceof IndexedGUIDropDownElement) {
                IndexedGUIDropDownElement<EnumActivityStatus> profileButton = (IndexedGUIDropDownElement) element;
                if (profileButton.index != this.clientStatus) {
                    OxygenManagerClient.instance().changeActivityStatusSynced(profileButton.index);
                    this.clientStatus = profileButton.index;
                    this.statusImageLabel.setTextureUV(this.clientStatus.ordinal() * 3, 0);
                    OxygenHelperClient.getSharedClientPlayerData().setByte(OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID, profileButton.index.ordinal());
                    this.sortPlayers(0);
                }
            }
        }
        if (element instanceof GroupEntryGUIButton) {
            GroupEntryGUIButton entry = (GroupEntryGUIButton) element;
            if (entry != this.currentEntry)
                this.currentEntry = entry;
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (keyCode == GroupsKeyHandler.GROUP_MENU.getKeyCode() && !this.hasCurrentCallback())
            this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void lockInviteButton() {
        if (GroupsManagerClient.instance().haveGroup() && !GroupsManagerClient.instance().getGroupData().isClientLeader() 
                || GroupsManagerClient.instance().haveGroup() && GroupsManagerClient.instance().getGroupData().getSize() == GroupsConfig.PLAYERS_PER_PARTY.getIntValue())
            this.inviteButton.disable();
    }

    public void unlockInviteButton() {
        if (!GroupsManagerClient.instance().haveGroup() 
                || (GroupsManagerClient.instance().getGroupData().isClientLeader() && GroupsManagerClient.instance().getGroupData().getSize() < GroupsConfig.PLAYERS_PER_PARTY.getIntValue()))
            this.inviteButton.enable();
    }

    public GroupEntryGUIButton getCurrentEntry() {
        return this.currentEntry;
    }

    public void openKickPlayerCallback() {
        this.kickPlayerCallback.open();
    }

    public void openPromoteToLeaderCallback() {
        this.promoteToLeaderCallback.open();
    }
}
