package austeretony.oxygen_groups.client.gui.group.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.GroupSection;

public class PromoteToLeaderContextAction implements OxygenContextMenuAction {

    public final GroupSection section;

    public PromoteToLeaderContextAction(GroupSection section) {
        this.section = section; 
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_groups.gui.action.promoteToLeader");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID playerUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();   
        return OxygenHelperClient.isPlayerAvailable(playerUUID)
                && GroupsManagerClient.instance().getGroupDataManager().getGroupData().isLeader(OxygenHelperClient.getPlayerUUID());
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openPromoteToLeaderCallback();
    }
}
