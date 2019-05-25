package austeretony.oxygen_groups.client.gui;

import java.util.UUID;

import austeretony.oxygen.client.IInteractionExecutor;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivileges;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.util.ResourceLocation;

public class InviteToGroupInteractionExecutor implements IInteractionExecutor {

    @Override
    public String getName() {
        return "groups.gui.interaction.inviteToGroup";
    }

    @Override
    public ResourceLocation getIcon() { 
        return GroupsGUITextures.INVITE_TO_GROUP_ICONS;
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return (!OxygenHelperClient.isOfflineStatus(playerUUID) || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivileges.EXPOSE_PLAYERS_OFFLINE.toString(), false))
                && (!OxygenHelperClient.getPlayerData().haveFriendListEntryForUUID(playerUUID) || !OxygenHelperClient.getPlayerData().getFriendListEntryByUUID(playerUUID).ignored);
    }

    @Override
    public void execute(UUID playerUUID) {
        GroupsManagerClient.instance().inviteToGroupSynced(playerUUID);
    }
}