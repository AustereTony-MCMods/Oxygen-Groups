package austeretony.oxygen_groups.client.gui.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.elements.ActivityStatusGUIDDList;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenuElement.ContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTexturedGUIButton;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_groups.client.GroupDataClient;
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

    private OxygenGUIButton inviteButton, leaveButton, checkButton;

    private OxygenTexturedGUIButton settingsButton;

    private OxygenGUIText playersOnlineTextLabel;

    private OxygenGUIButtonPanel playersPanel;

    private AbstractGUICallback settingsCallback, inviteCallback, leaveGroupCallback, readinessCheckCallback, kickPlayerCallback, 
    promoteToLeaderCallback;

    private ActivityStatusGUIDDList activityStatusDDList;

    private OxygenSorterGUIElement statusSorter, usernameSorter;

    //cache

    private GroupEntryGUIButton currentEntry;

    public GroupGUISection(GroupMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new GroupMenuBackgroungGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_groups.gui.groupMenu.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.playersOnlineTextLabel = new OxygenGUIText(0, this.getHeight() - 9, "", GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColor()));

        this.addElement(this.statusSorter = new OxygenSorterGUIElement(13, 27, EnumSorting.DOWN, ClientReference.localize("oxygen.sorting.status")));   

        this.statusSorter.setClickListener((sorting)->{
            this.usernameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(0);
            else
                this.sortPlayers(1);
        });

        this.addElement(this.usernameSorter = new OxygenSorterGUIElement(19, 27, EnumSorting.INACTIVE, ClientReference.localize("oxygen.sorting.username")));  

        this.usernameSorter.setClickListener((sorting)->{
            this.statusSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(2);
            else
                this.sortPlayers(3);
        });      

        this.addElement(this.playersPanel = new OxygenGUIButtonPanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.getIntValue(), 12, 24), 12, GUISettings.get().getPanelTextScale(), true));      

        this.playersPanel.<GroupEntryGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntry = clicked);

        List<ContextMenuAction> actions = new ArrayList<>(OxygenManagerClient.instance().getGUIManager().getContextActions(GroupsMain.GROUP_MENU_SCREEN_ID));
        actions.add(new KickPlayerContextAction(this));
        actions.add(new PromoteToLeaderContextAction(this));
        ContextMenuAction[] array = new ContextMenuAction[actions.size()];
        actions.toArray(array);
        this.playersPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, array));

        this.addElement(this.inviteButton = new OxygenGUIButton(4, 167, 40, 10, ClientReference.localize("oxygen_groups.gui.inviteButton")));     
        this.lockInviteButton();
        this.addElement(this.leaveButton = new OxygenGUIButton(54, 167, 40, 10, ClientReference.localize("oxygen_groups.gui.leaveButton")));    
        if (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive())
            this.leaveButton.disable();
        this.addElement(this.checkButton = new OxygenGUIButton(104, 167, 40, 10, ClientReference.localize("oxygen_groups.gui.checkButton")));     
        if (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() || !GroupsManagerClient.instance().getGroupDataManager().getGroupData().isClientLeader())
            this.checkButton.disable();
        this.addElement(this.settingsButton = new OxygenTexturedGUIButton(this.getWidth() - 5, 0, 5, 5, OxygenGUITextures.TRIANGLE_TOP_RIGHT_CORNER_ICONS, 5, 5, ClientReference.localize("oxygen.tooltip.settings")));

        this.addElement(this.activityStatusDDList = new ActivityStatusGUIDDList(7, 16));

        this.activityStatusDDList.setActivityStatusChangeListener((status)->{
            this.statusSorter.setSorting(EnumSorting.DOWN);
            this.usernameSorter.reset();
            this.sortPlayers(0);
        });

        this.settingsCallback = new SettingsGUICallback(this.screen, this, 140, 52).enableDefaultBackground();

        this.inviteCallback = new InviteGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.leaveGroupCallback = new LeaveGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.readinessCheckCallback = new ReadinessCheckGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.kickPlayerCallback = new KickGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.promoteToLeaderCallback = new PromoteGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
    }

    private void sortPlayers(int mode) {
        List<PlayerSharedData> members = new ArrayList<>();

        for (UUID playerUUID : GroupsManagerClient.instance().getGroupDataManager().getGroupData().getPlayersUUIDs())
            members.add(OxygenHelperClient.getPlayerSharedData(playerUUID));

        if (mode == 0)
            Collections.sort(members, (s1, s2)->OxygenHelperClient.getPlayerActivityStatus(s1).ordinal() - OxygenHelperClient.getPlayerActivityStatus(s2).ordinal());
        else if (mode == 1)
            Collections.sort(members, (s1, s2)->OxygenHelperClient.getPlayerActivityStatus(s2).ordinal() - OxygenHelperClient.getPlayerActivityStatus(s1).ordinal());
        else if (mode == 2)
            Collections.sort(members, (s1, s2)->s1.getUsername().compareTo(s2.getUsername()));
        else if (mode == 3)
            Collections.sort(members, (s1, s2)->s2.getUsername().compareTo(s1.getUsername()));

        this.playersPanel.reset();
        for (PlayerSharedData sharedData : members)           
            this.playersPanel.addButton(new GroupEntryGUIButton(sharedData));

        this.playersPanel.getScroller().resetPosition();
        this.playersPanel.getScroller().getSlider().reset();

        this.playersOnlineTextLabel.setDisplayText(members.size() + "/" + this.getMaxGroupSize());
        this.playersOnlineTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.playersOnlineTextLabel.getDisplayText(), GUISettings.get().getSubTextScale() - 0.05F));
    }

    private int getMaxGroupSize() {
        switch (GroupsManagerClient.instance().getGroupDataManager().getGroupData().getMode()) {
        case SQUAD:
            return GroupsConfig.PLAYERS_PER_SQUAD.getIntValue();
        case RAID:
            return GroupsConfig.PLAYERS_PER_RAID.getIntValue();
        case PARTY:
            return GroupsConfig.PLAYERS_PER_PARTY.getIntValue();
        }
        return GroupsConfig.PLAYERS_PER_SQUAD.getIntValue();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.settingsButton)
                this.settingsCallback.open();
            else if (element == this.inviteButton)
                this.inviteCallback.open();
            else if (element == this.leaveButton)
                this.leaveGroupCallback.open();
            else if (element == this.checkButton)
                this.readinessCheckCallback.open();
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (!this.hasCurrentCallback())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == GroupMenuGUIScreen.GROUP_MENU_ENTRY.getIndex() + 2)
                    this.screen.close();
            } else if (keyCode == GroupsKeyHandler.GROUP_MENU.getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void lockInviteButton() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        if (groupData.isActive() && !groupData.isClientLeader() 
                || groupData.isActive() && groupData.getSize() == GroupsConfig.PLAYERS_PER_PARTY.getIntValue())
            this.inviteButton.disable();
    }

    public void unlockInviteButton() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        if (!groupData.isActive() 
                || (groupData.isClientLeader() && groupData.getSize() < GroupsConfig.PLAYERS_PER_PARTY.getIntValue()))
            this.inviteButton.enable();
    }

    public void sharedDataSynchronized() {
        this.activityStatusDDList.updateActivityStatus();
        this.unlockInviteButton();
        this.sortPlayers(0);
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
