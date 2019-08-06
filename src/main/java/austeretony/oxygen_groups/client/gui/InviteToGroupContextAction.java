package austeretony.oxygen_groups.client.gui;

import java.util.UUID;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivilege;
import austeretony.oxygen_groups.client.GroupsManagerClient;

public class InviteToGroupContextAction extends AbstractContextAction {

    @Override
    protected String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_groups.context.inviteToGroup");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;   
        return !targetUUID.equals(OxygenHelperClient.getPlayerUUID()) 
                && OxygenHelperClient.isOnline(targetUUID)
                && (!OxygenHelperClient.isOfflineStatus(targetUUID) || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivilege.EXPOSE_PLAYERS_OFFLINE.toString(), false));
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;   
        GroupsManagerClient.instance().inviteToGroupSynced(OxygenHelperClient.getPlayerIndex(targetUUID));
    }
}
