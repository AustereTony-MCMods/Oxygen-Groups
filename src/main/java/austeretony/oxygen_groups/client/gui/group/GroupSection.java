package austeretony.oxygen_groups.client.gui.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenActivityStatusSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_groups.client.GroupDataClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.callback.InvitePlayerCallback;
import austeretony.oxygen_groups.client.gui.group.callback.KickPlayerCallback;
import austeretony.oxygen_groups.client.gui.group.callback.LeaveGroupCallback;
import austeretony.oxygen_groups.client.gui.group.callback.PromoteToLeaderCallback;
import austeretony.oxygen_groups.client.gui.group.context.KickPlayerContextAction;
import austeretony.oxygen_groups.client.gui.group.context.PromoteToLeaderContextAction;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupSection extends AbstractGUISection {

    private GroupMenuScreen screen;

    private OxygenButton inviteButton, leaveButton;

    private OxygenSorter statusSorter, usernameSorter;

    private OxygenTextLabel membersOnlineAmountLabel;

    private OxygenScrollablePanel membersPanel;

    private OxygenActivityStatusSwitcher activityStatusSwitcher;

    private AbstractGUICallback invitePlayerCallback, leaveGroupCallback, kickPlayerCallback, promoteToLeaderCallback;

    //cache

    private GroupMemberPanelEntry currentEntry;

    public GroupSection(GroupMenuScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new GroupMenuBackgroungFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_groups.gui.groupMenu.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.membersOnlineAmountLabel = new OxygenTextLabel(0, 23, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.statusSorter = new OxygenSorter(13, 27, EnumSorting.DOWN, ClientReference.localize("oxygen_core.gui.status")));   

        this.statusSorter.setClickListener((sorting)->{
            this.usernameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(0);
            else
                this.sortPlayers(1);
        });

        this.addElement(this.usernameSorter = new OxygenSorter(19, 27, EnumSorting.INACTIVE, ClientReference.localize("oxygen_core.gui.username")));  

        this.usernameSorter.setClickListener((sorting)->{
            this.statusSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(2);
            else
                this.sortPlayers(3);
        });      

        this.addElement(this.membersPanel = new OxygenScrollablePanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.asInt(), 12, GroupsConfig.PLAYERS_PER_PARTY.asInt()), 12, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));      

        this.membersPanel.<GroupMemberPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntry = clicked);

        List<OxygenContextMenuAction> actions = new ArrayList<>(OxygenManagerClient.instance().getGUIManager().getContextActions(GroupsMain.GROUP_MENU_SCREEN_ID));
        actions.add(new KickPlayerContextAction(this));
        actions.add(new PromoteToLeaderContextAction(this));
        OxygenContextMenuAction[] array = new OxygenContextMenuAction[actions.size()];
        actions.toArray(array);
        this.membersPanel.initContextMenu(new OxygenContextMenu(array));

        this.addElement(this.inviteButton = new OxygenButton(6, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_groups.gui.inviteButton")));  
        this.inviteButton.setKeyPressListener(Keyboard.KEY_F, ()->this.invitePlayerCallback.open());
        this.lockInviteButton();
        this.addElement(this.leaveButton = new OxygenButton(52, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_groups.gui.leaveButton")));    
        this.leaveButton.setKeyPressListener(Keyboard.KEY_X, ()->this.leaveGroupCallback.open());
        if (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive())
            this.leaveButton.disable();

        this.addElement(this.activityStatusSwitcher = new OxygenActivityStatusSwitcher(7, 16));

        this.activityStatusSwitcher.setActivityStatusChangeListener((status)->{
            this.statusSorter.setSorting(EnumSorting.DOWN);
            this.usernameSorter.reset();
            this.sortPlayers(0);
        });

        this.invitePlayerCallback = new InvitePlayerCallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.leaveGroupCallback = new LeaveGroupCallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.kickPlayerCallback = new KickPlayerCallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.promoteToLeaderCallback = new PromoteToLeaderCallback(this.screen, this, 140, 38).enableDefaultBackground();
    }

    private void sortPlayers(int mode) {
        int maxGroupSize = this.getMaxGroupSize();
        List<PlayerSharedData> members = new ArrayList<>(maxGroupSize);

        for (UUID playerUUID : GroupsManagerClient.instance().getGroupDataManager().getGroupData().getMembers())
            members.add(OxygenHelperClient.getPlayerSharedData(playerUUID));

        if (mode == 0)
            Collections.sort(members, (s1, s2)->OxygenHelperClient.getPlayerActivityStatus(s1).ordinal() - OxygenHelperClient.getPlayerActivityStatus(s2).ordinal());
        else if (mode == 1)
            Collections.sort(members, (s1, s2)->OxygenHelperClient.getPlayerActivityStatus(s2).ordinal() - OxygenHelperClient.getPlayerActivityStatus(s1).ordinal());
        else if (mode == 2)
            Collections.sort(members, (s1, s2)->s1.getUsername().compareTo(s2.getUsername()));
        else if (mode == 3)
            Collections.sort(members, (s1, s2)->s2.getUsername().compareTo(s1.getUsername()));

        this.membersPanel.reset();
        for (PlayerSharedData sharedData : members)           
            this.membersPanel.addEntry(new GroupMemberPanelEntry(sharedData));

        this.membersOnlineAmountLabel.setDisplayText(String.valueOf(members.size()) + "/" + String.valueOf(maxGroupSize));
        this.membersOnlineAmountLabel.setX(this.getWidth() - 6 - this.textWidth(this.membersOnlineAmountLabel.getDisplayText(), this.membersOnlineAmountLabel.getTextScale()));

        this.membersPanel.getScroller().reset();
        this.membersPanel.getScroller().updateRowsAmount(MathUtils.clamp(members.size(), 12, GroupsConfig.PLAYERS_PER_PARTY.asInt()));
    }

    private int getMaxGroupSize() {
        switch (GroupsManagerClient.instance().getGroupDataManager().getGroupData().getMode()) {
        case SQUAD:
            return GroupsConfig.PLAYERS_PER_SQUAD.asInt();
        case RAID:
            return GroupsConfig.PLAYERS_PER_RAID.asInt();
        case PARTY:
            return GroupsConfig.PLAYERS_PER_PARTY.asInt();
        }
        return GroupsConfig.PLAYERS_PER_SQUAD.asInt();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.inviteButton)
                this.invitePlayerCallback.open();
            else if (element == this.leaveButton)
                this.leaveGroupCallback.open();
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (!this.hasCurrentCallback())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == GroupMenuScreen.GROUP_MENU_ENTRY.getKeyCode())
                    this.screen.close();
            } else if (GroupsConfig.ENABLE_GROUP_MENU_KEY.asBoolean() 
                    && keyCode == GroupsManagerClient.instance().getKeyHandler().getGroupMenuKeybinding().getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void lockInviteButton() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        if (groupData.isActive() && !groupData.isLeader(OxygenHelperClient.getPlayerUUID()) 
                || groupData.isActive() && groupData.getSize() == GroupsConfig.PLAYERS_PER_PARTY.asInt())
            this.inviteButton.disable();
    }

    public void unlockInviteButton() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        if (!groupData.isActive() 
                || (groupData.isLeader(OxygenHelperClient.getPlayerUUID()) && groupData.getSize() < GroupsConfig.PLAYERS_PER_PARTY.asInt()))
            this.inviteButton.enable();
    }

    public void sharedDataSynchronized() {
        this.activityStatusSwitcher.updateActivityStatus();
        this.unlockInviteButton();
        this.sortPlayers(0);
    }

    public GroupMemberPanelEntry getCurrentEntry() {
        return this.currentEntry;
    }

    public void openKickPlayerCallback() {
        this.kickPlayerCallback.open();
    }

    public void openPromoteToLeaderCallback() {
        this.promoteToLeaderCallback.open();
    }
}
