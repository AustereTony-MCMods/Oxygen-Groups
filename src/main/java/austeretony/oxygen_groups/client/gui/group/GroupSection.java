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
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenDefaultBackgroundWithButtonsFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_groups.client.GroupDataClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.callback.InvitePlayerCallback;
import austeretony.oxygen_groups.client.gui.group.callback.KickGroupMemberCallback;
import austeretony.oxygen_groups.client.gui.group.callback.LeaveGroupCallback;
import austeretony.oxygen_groups.client.gui.group.callback.PromoteToLeaderCallback;
import austeretony.oxygen_groups.client.gui.group.context.KickPlayerContextAction;
import austeretony.oxygen_groups.client.gui.group.context.PromoteToLeaderContextAction;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraft.client.gui.ScaledResolution;

public class GroupSection extends AbstractGUISection {

    private GroupMenuScreen screen;

    private OxygenKeyButton invitePlayerButton, leaveGroupButton;

    private OxygenSorter statusSorter, usernameSorter;

    private OxygenTextLabel membersOnlineAmountLabel;

    private OxygenScrollablePanel membersPanel;

    private OxygenActivityStatusSwitcher activityStatusSwitcher;

    private AbstractGUICallback invitePlayerCallback, leaveGroupCallback, kickGroupMemberCallback, promoteToLeaderCallback;

    //cache

    private GroupMemberPanelEntry currMemberEntry;

    public GroupSection(GroupMenuScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.invitePlayerCallback = new InvitePlayerCallback(this.screen, this, 140, 46).enableDefaultBackground();
        this.leaveGroupCallback = new LeaveGroupCallback(this.screen, this, 140, 36).enableDefaultBackground();
        this.kickGroupMemberCallback = new KickGroupMemberCallback(this.screen, this, 140, 36).enableDefaultBackground();
        this.promoteToLeaderCallback = new PromoteToLeaderCallback(this.screen, this, 140, 36).enableDefaultBackground();

        this.addElement(new OxygenDefaultBackgroundWithButtonsFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_groups.gui.groupMenu.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.membersOnlineAmountLabel = new OxygenTextLabel(0, 23, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.statusSorter = new OxygenSorter(13, 27, EnumSorting.DOWN, ClientReference.localize("oxygen_core.gui.status")));   

        this.statusSorter.setSortingListener((sorting)->{
            this.usernameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(0);
            else
                this.sortPlayers(1);
        });

        this.addElement(this.usernameSorter = new OxygenSorter(19, 27, EnumSorting.INACTIVE, ClientReference.localize("oxygen_core.gui.username")));  

        this.usernameSorter.setSortingListener((sorting)->{
            this.statusSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(2);
            else
                this.sortPlayers(3);
        });      

        this.addElement(this.membersPanel = new OxygenScrollablePanel(this.screen, 6, 33, this.getWidth() - 15, 10, 1, 
                MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.asInt(), 14, GroupsConfig.PLAYERS_PER_PARTY.asInt()), 14, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));      

        this.membersPanel.<GroupMemberPanelEntry>setElementClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currMemberEntry = clicked);
        List<OxygenContextMenuAction> actions = new ArrayList<>(OxygenManagerClient.instance().getGUIManager().getContextActions(GroupsMain.GROUP_MENU_SCREEN_ID));
        actions.add(new KickPlayerContextAction(this));
        actions.add(new PromoteToLeaderContextAction(this));
        OxygenContextMenuAction[] array = new OxygenContextMenuAction[actions.size()];
        actions.toArray(array);
        this.membersPanel.initContextMenu(new OxygenContextMenu(array));

        this.addElement(this.invitePlayerButton = new OxygenKeyButton(0, this.getY() + this.getHeight() + this.screen.guiTop - 8, ClientReference.localize("oxygen_groups.gui.group.button.invitePlayer"), Keyboard.KEY_E, this.invitePlayerCallback::open).disableFull());  
        this.addElement(this.leaveGroupButton = new OxygenKeyButton(0, this.getY() + this.getHeight() + this.screen.guiTop - 8, ClientReference.localize("oxygen_groups.gui.group.button.leaveGroup"), Keyboard.KEY_X, this.leaveGroupCallback::open).disableFull());    
        if (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive())
            this.leaveGroupButton.disable();

        this.addElement(this.activityStatusSwitcher = new OxygenActivityStatusSwitcher(7, 16));

        this.activityStatusSwitcher.setActivityStatusChangeListener((status)->{
            this.statusSorter.setSorting(EnumSorting.DOWN);
            this.usernameSorter.reset();
            this.sortPlayers(0);
        });
    }

    private void calculateButtonsHorizontalPosition() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.invitePlayerButton.setX((sr.getScaledWidth() - (12 + this.textWidth(this.invitePlayerButton.getDisplayText(), this.invitePlayerButton.getTextScale()))) / 2 - this.screen.guiLeft);

        if (this.invitePlayerButton.isEnabled())
            this.leaveGroupButton.setX(sr.getScaledWidth() / 2 + 50 - this.screen.guiLeft);
        else
            this.leaveGroupButton.setX((sr.getScaledWidth() - (12 + this.textWidth(this.leaveGroupButton.getDisplayText(), this.leaveGroupButton.getTextScale()))) / 2 - this.screen.guiLeft);
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
        this.membersPanel.getScroller().updateRowsAmount(MathUtils.clamp(members.size(), 14, MathUtils.clamp(GroupsConfig.PLAYERS_PER_PARTY.asInt(), 14, GroupsConfig.PLAYERS_PER_PARTY.asInt())));
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
            if (element == this.invitePlayerButton)
                this.invitePlayerCallback.open();
            else if (element == this.leaveGroupButton)
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

    public void updateButtonsState() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();

        this.invitePlayerButton.disableFull();
        if (!groupData.isActive() || (groupData.isLeader(OxygenHelperClient.getPlayerUUID()) && groupData.getSize() < GroupsConfig.PLAYERS_PER_PARTY.asInt()))
            this.invitePlayerButton.enableFull();            

        if (groupData.isActive())
            this.leaveGroupButton.enableFull();
        else
            this.leaveGroupButton.disableFull();
    }

    public void unlockInviteButton() {
        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        if (!groupData.isActive() 
                || (groupData.isLeader(OxygenHelperClient.getPlayerUUID()) && groupData.getSize() < GroupsConfig.PLAYERS_PER_PARTY.asInt()))
            this.invitePlayerButton.enable();
    }

    public void sharedDataSynchronized() {
        this.activityStatusSwitcher.updateActivityStatus();
        this.sortPlayers(0);

        this.updateButtonsState();
        this.calculateButtonsHorizontalPosition();
    }

    public GroupMemberPanelEntry getCurrentMemberEntry() {
        return this.currMemberEntry;
    }

    public void openKickPlayerCallback() {
        this.kickGroupMemberCallback.open();
    }

    public void openPromoteToLeaderCallback() {
        this.promoteToLeaderCallback.open();
    }
}
