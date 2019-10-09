package austeretony.oxygen_groups.client.gui.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenuElement.ContextMenuAction;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupContextAction implements ContextMenuAction {

    @Override
    public String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_groups.context.inviteToGroup");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID playerUUID = ((IndexedGUIButton<UUID>) currElement).index;   
        return OxygenHelperClient.isPlayerAvailable(playerUUID)
                && (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() || GroupsManagerClient.instance().getGroupDataManager().getGroupData().isClientLeader());
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        UUID playerUUID = ((IndexedGUIButton<UUID>) currElement).index;   
        GroupsManagerClient.instance().getGroupDataManager().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(playerUUID));
    }
}
