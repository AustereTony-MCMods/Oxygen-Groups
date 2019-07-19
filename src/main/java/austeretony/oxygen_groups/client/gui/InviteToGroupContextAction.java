package austeretony.oxygen_groups.client.gui;

import java.util.UUID;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.PlayerGUIButton;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivileges;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupContextAction extends AbstractContextAction {

    @Override
    protected String getName(GUIBaseElement currElement) {
        return ClientReference.localize("contextaction.inviteToGroup");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((PlayerGUIButton) currElement).playerUUID;   
        return !targetUUID.equals(OxygenHelperClient.getPlayerUUID()) 
                && OxygenHelperClient.isOnline(targetUUID)
                && (!OxygenHelperClient.isOfflineStatus(targetUUID) || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivileges.EXPOSE_PLAYERS_OFFLINE.toString(), false));
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((PlayerGUIButton) currElement).playerUUID;
        GroupsManagerClient.instance().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(targetUUID));
    }
}
