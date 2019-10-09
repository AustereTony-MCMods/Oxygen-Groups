package austeretony.oxygen_groups.client.gui.interaction;

import java.util.UUID;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegeProviderClient;
import austeretony.oxygen_core.client.interaction.InteractionMenuEntry;
import austeretony.oxygen_core.common.main.EnumOxygenPrivilege;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupInteractionExecutor implements InteractionMenuEntry {

    @Override
    public String getName() {
        return "oxygen_groups.gui.interaction.inviteToGroup";
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return OxygenHelperClient.isPlayerAvailable(playerUUID) 
                && (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() || GroupsManagerClient.instance().getGroupDataManager().getGroupData().isClientLeader());
    }

    @Override
    public void execute(UUID playerUUID) {
        GroupsManagerClient.instance().getGroupDataManager().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(playerUUID));
    }
}
