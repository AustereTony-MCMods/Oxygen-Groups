package austeretony.groups.client.gui;

import java.util.UUID;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.groups.client.GroupsManagerClient;
import austeretony.oxygen.client.gui.PlayerGUIButton;
import austeretony.oxygen.common.api.OxygenHelperClient;
import austeretony.oxygen.common.main.EnumOxygenPrivileges;
import austeretony.oxygen.common.privilege.api.PrivilegeProviderClient;
import net.minecraft.client.resources.I18n;

public class InviteToGroupContextAction extends AbstractContextAction {

    @Override
    protected String getName(GUIBaseElement currElement) {
        return I18n.format("contextaction.inviteToGroup");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((PlayerGUIButton) currElement).playerUUID;
        return !targetUUID.equals(OxygenHelperClient.getPlayerUUID()) 
                && OxygenHelperClient.isOnline(targetUUID)
                && (!OxygenHelperClient.isOfflineStatus(targetUUID) || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivileges.EXPOSE_PLAYERS_OFFLINE.toString(), false)) 
                && (!OxygenHelperClient.getPlayerData().haveFriendListEntryForUUID(targetUUID) || !OxygenHelperClient.getPlayerData().getFriendListEntryByUUID(targetUUID).ignored);
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((PlayerGUIButton) currElement).playerUUID;
        GroupsManagerClient.instance().inviteToGroupSynced(targetUUID);
    }
}
