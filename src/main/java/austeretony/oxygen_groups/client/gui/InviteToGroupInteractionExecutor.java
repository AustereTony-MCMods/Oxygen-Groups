package austeretony.oxygen_groups.client.gui;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.interaction.IInteractionMenuExecutor;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivilege;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.util.ResourceLocation;

public class InviteToGroupInteractionExecutor implements IInteractionMenuExecutor {

    @Override
    public String getName() {
        return "oxygen_groups.gui.interaction.inviteToGroup";
    }

    @Override
    public ResourceLocation getIcon() { 
        return GroupsGUITextures.INVITE_TO_GROUP_ICONS;
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return !OxygenHelperClient.isOfflineStatus(playerUUID) || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivilege.EXPOSE_PLAYERS_OFFLINE.toString(), false);
    }

    @Override
    public void execute(UUID playerUUID) {
        GroupsManagerClient.instance().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(playerUUID));
    }
}
