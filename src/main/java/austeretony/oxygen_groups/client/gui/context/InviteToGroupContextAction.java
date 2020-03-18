package austeretony.oxygen_groups.client.gui.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupContextAction implements OxygenContextMenuAction {

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_groups.context.inviteToGroup");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID playerUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();   
        return OxygenHelperClient.isPlayerAvailable(playerUUID)
                && (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() || GroupsManagerClient.instance().getGroupDataManager().getGroupData().isLeader(OxygenHelperClient.getPlayerUUID()));
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        UUID playerUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();   
        GroupsManagerClient.instance().getGroupDataManager().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(playerUUID));
    }
}
