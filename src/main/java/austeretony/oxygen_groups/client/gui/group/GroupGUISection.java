package austeretony.oxygen_groups.client.gui.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import austeretony.alternateui.screen.panel.GUIButtonPanel.GUIEnumOrientation;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen.client.OxygenManagerClient;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.StatusGUIDropDownElement;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.callback.DownloadDataGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.InvitePlayerGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.KickPlayerGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.LeaveGroupGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.PromoteToLeaderGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.ReadinessCheckGUICallback;
import austeretony.oxygen_groups.client.gui.group.callback.SettingsGUICallback;
import austeretony.oxygen_groups.client.gui.group.context.KickPlayerContextAction;
import austeretony.oxygen_groups.client.gui.group.context.PromoteToLeaderContextAction;
import austeretony.oxygen_groups.client.input.GroupsKeyHandler;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupGUISection extends AbstractGUISection {

    private GroupMenuGUIScreen screen;

    private GUIButton downloadButton, settingsButton, refreshButton, inviteButton, leaveButton, checkButton, sortDownStatusButton, sortUpStatusButton, 
    sortDownUsernameButton, sortUpUsernameButton;

    private GUITextLabel playersOnlineTextLabel, playerNameTextLabel;

    private GUIButtonPanel playersPanel;

    private GUIDropDownList statusDropDownList;

    private OxygenPlayerData.EnumActivityStatus currentStatus;  

    private GUIImageLabel statusImageLabel;

    private AbstractGUICallback downloadDataCallback, settingsCallback, invitePlayerCallback, leaveGroupCallback, readinessCheckCallback, kickPlayerCallback, 
    promoteToLeaderCallback;

    private GroupEntryGUIButton currentEntry;

    private final Set<SharedPlayerData> players = new HashSet<SharedPlayerData>();

    public GroupGUISection(GroupMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new GroupMenuBackgroungGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        String title = ClientReference.localize("groups.gui.groupMenu");
        this.addElement(new GUITextLabel(2, 4).setDisplayText(title, false, GUISettings.instance().getTitleScale()));
        this.addElement(this.downloadButton = new GUIButton(this.textWidth(title, GUISettings.instance().getTitleScale()) + 4, 4, 8, 8).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.DOWNLOAD_ICONS, 8, 8).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.download"), GUISettings.instance().getTooltipScale()));
        this.addElement(this.settingsButton = new GUIButton(this.getWidth() - 12, 1, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SETTINGS_ICONS, 10, 10).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.settings"), GUISettings.instance().getTooltipScale()));

        this.addElement(this.refreshButton = new GUIButton(2, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));         
        this.addElement(this.playerNameTextLabel = new GUITextLabel(14, 15).setDisplayText(OxygenHelperClient.getSharedClientPlayerData().getUsername(), false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.playersOnlineTextLabel = new GUITextLabel(0, 15).setTextScale(GUISettings.instance().getSubTextScale()).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.online"), GUISettings.instance().getTooltipScale())); 

        this.addElement(this.sortDownStatusButton = new GUIButton(7, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpStatusButton = new GUIButton(7, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortDownUsernameButton = new GUIButton(19, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpUsernameButton = new GUIButton(19, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(24, 25).setDisplayText(ClientReference.localize("oxygen.gui.username")).setTextScale(GUISettings.instance().getTextScale())); 
        this.addElement(new GUITextLabel(100, 25).setDisplayText(ClientReference.localize("oxygen.gui.dimension")).setTextScale(GUISettings.instance().getTextScale())); 

        this.playersPanel = new GUIButtonPanel(GUIEnumOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getPanelTextScale());
        this.addElement(this.playersPanel);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.getIntValue(), 14, 50), 14);
        this.playersPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, this.getHeight() - 49);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);    

        //Context Menu
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

        this.addElement(this.inviteButton = new GUIButton(4, this.getHeight() - 11,  40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("groups.gui.inviteButton"), true, GUISettings.instance().getButtonTextScale()));     
        this.lockInviteButton();

        this.addElement(this.leaveButton = new GUIButton(54, this.getHeight() - 11,  40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("groups.gui.leaveButton"), true, GUISettings.instance().getButtonTextScale())); 
        if (!GroupsManagerClient.instance().haveGroup())
            this.leaveButton.disable();

        this.addElement(this.checkButton = new GUIButton(104, this.getHeight() - 11,  40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("groups.gui.checkButton"), true, GUISettings.instance().getButtonTextScale()));   
        if (!GroupsManagerClient.instance().haveGroup() || !GroupsManagerClient.instance().getGroupData().isClientLeader())
            this.checkButton.disable();

        //Protection
        if (!OxygenGUIHelper.isNeedSync(GroupsMain.GROUP_MENU_SCREEN_ID) || OxygenGUIHelper.isDataRecieved(GroupsMain.GROUP_MENU_SCREEN_ID))
            this.sortPlayers(0);

        this.currentStatus = OxygenHelperClient.getClientPlayerStatus();
        int statusOffset = this.playerNameTextLabel.getX() + this.textWidth(this.playerNameTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale());
        this.addElement(this.statusImageLabel = new GUIImageLabel(statusOffset + 4, 17).setTexture(OxygenGUITextures.STATUS_ICONS, 3, 3, this.currentStatus.ordinal() * 3, 0, 12, 3));   
        this.statusDropDownList = new GUIDropDownList(statusOffset + 10, 16, GUISettings.instance().getDropDownListWidth(), 10).setScale(GUISettings.instance().getDropDownListScale()).setDisplayText(this.currentStatus.localizedName()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 1);
        this.statusDropDownList.setOpenSound(OxygenSoundEffects.DROP_DOWN_LIST_OPEN.soundEvent);
        this.statusDropDownList.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        StatusGUIDropDownElement profileElement;
        for (OxygenPlayerData.EnumActivityStatus status : OxygenPlayerData.EnumActivityStatus.values()) {
            profileElement = new StatusGUIDropDownElement(status);
            profileElement.setDisplayText(status.localizedName());
            profileElement.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
            profileElement.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.statusDropDownList.addElement(profileElement);
        }
        this.addElement(this.statusDropDownList);   

        this.downloadDataCallback = new DownloadDataGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.settingsCallback = new SettingsGUICallback(this.screen, this, 140, 48).enableDefaultBackground();

        this.invitePlayerCallback = new InvitePlayerGUICallback(this.screen, this, 140, 68).enableDefaultBackground();
        this.leaveGroupCallback = new LeaveGroupGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.readinessCheckCallback = new ReadinessCheckGUICallback(this.screen, this, 140, 40).enableDefaultBackground();

        this.kickPlayerCallback = new KickPlayerGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.promoteToLeaderCallback = new PromoteToLeaderGUICallback(this.screen, this, 140, 48).enableDefaultBackground();

        OxygenGUIHelper.screenInitialized(GroupsMain.GROUP_MENU_SCREEN_ID);
    }

    public void sortPlayers(int mode) {
        this.players.clear();
        for (UUID playerUUID : GroupsManagerClient.instance().getGroupData().getPlayersUUIDs())
            this.players.add(OxygenHelperClient.getObservedSharedData(playerUUID));

        List<SharedPlayerData> players = new ArrayList<SharedPlayerData>(this.players);
        if (mode == 0 || mode == 1) {//by status: 0 - online -> offline; 1 - vice versa.
            Collections.sort(players, new Comparator<SharedPlayerData>() {

                @Override
                public int compare(SharedPlayerData entry1, SharedPlayerData entry2) {
                    OxygenPlayerData.EnumActivityStatus 
                    entry1Status = OxygenPlayerData.EnumActivityStatus.OFFLINE,
                    entry2Status = OxygenPlayerData.EnumActivityStatus.OFFLINE;
                    if (OxygenHelperClient.isOnline(entry1.getPlayerUUID()))
                        entry1Status = OxygenHelperClient.getPlayerStatus(entry1);
                    if (OxygenHelperClient.isOnline(entry2.getPlayerUUID()))
                        entry2Status = OxygenHelperClient.getPlayerStatus(entry2);
                    if (mode == 0)
                        return entry1Status.ordinal() - entry2Status.ordinal();
                    else
                        return entry2Status.ordinal() - entry1Status.ordinal();
                }
            });
        } else {//by username: 2 - A -> z; 3 - vice versa.
            Collections.sort(players, new Comparator<SharedPlayerData>() {

                @Override
                public int compare(SharedPlayerData entry1, SharedPlayerData entry2) {
                    if (mode == 2)
                        return entry1.getUsername().compareTo(entry2.getUsername());
                    else
                        return entry2.getUsername().compareTo(entry1.getUsername());
                }
            });
        }

        this.playersPanel.reset();
        GroupEntryGUIButton button;
        int onlinePlayers = 0;
        OxygenPlayerData.EnumActivityStatus status;
        for (SharedPlayerData data : players) {           
            status = OxygenPlayerData.EnumActivityStatus.OFFLINE;
            if (OxygenHelperClient.isOnline(data.getPlayerUUID())) {
                status = OxygenHelperClient.getPlayerStatus(data);
                if (status != OxygenPlayerData.EnumActivityStatus.OFFLINE)
                    onlinePlayers++;
            }   
            button = new GroupEntryGUIButton(data, status);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getDisabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.playersPanel.addButton(button);
        }

        this.playersPanel.getScroller().resetPosition();
        this.playersPanel.getScroller().getSlider().reset();

        this.playersOnlineTextLabel.setDisplayText(onlinePlayers + " / " + this.players.size());
        this.playersOnlineTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.playersOnlineTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));

        this.sortUpStatusButton.toggle();
        this.sortDownStatusButton.setToggled(false);
        this.sortDownUsernameButton.setToggled(false);
        this.sortUpUsernameButton.setToggled(false);
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.downloadButton)
                this.downloadDataCallback.open();
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
                this.invitePlayerCallback.open();
            else if (element == this.leaveButton)
                this.leaveGroupCallback.open();
            else if (element == this.checkButton)
                this.readinessCheckCallback.open();
            else if (element instanceof StatusGUIDropDownElement) {
                StatusGUIDropDownElement profileButton = (StatusGUIDropDownElement) element;
                if (profileButton.status != this.currentStatus) {
                    OxygenManagerClient.instance().changeActivityStatusSynced(profileButton.status);
                    this.currentStatus = profileButton.status;
                    this.statusImageLabel.setTextureUV(this.currentStatus.ordinal() * 3, 0);
                    OxygenHelperClient.getSharedClientPlayerData().setByte(OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID, profileButton.status.ordinal());
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
