package austeretony.oxygen_groups.client.gui.interaction;

import java.util.UUID;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.interaction.PlayerInteractionMenuEntry;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupInteractionExecutor implements PlayerInteractionMenuEntry {

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_groups.gui.interaction.inviteToGroup");
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return OxygenHelperClient.isPlayerAvailable(playerUUID) 
                && (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() || GroupsManagerClient.instance().getGroupDataManager().getGroupData().isLeader(OxygenHelperClient.getPlayerUUID()));
    }

    @Override
    public void execute(UUID playerUUID) {
        GroupsManagerClient.instance().getGroupDataManager().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(playerUUID));
    }
}
