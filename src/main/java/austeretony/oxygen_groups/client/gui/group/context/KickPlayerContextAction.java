package austeretony.oxygen_groups.client.gui.group.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.GroupSection;

public class KickPlayerContextAction implements OxygenContextMenuAction {

    public final GroupSection section;

    public KickPlayerContextAction(GroupSection section) {
        this.section = section; 
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_groups.gui.action.kick");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID playerUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();   
        return !playerUUID.equals(OxygenHelperClient.getPlayerUUID())
                && GroupsManagerClient.instance().getGroupDataManager().getGroupData().isLeader(OxygenHelperClient.getPlayerUUID())
                && GroupsManagerClient.instance().getGroupDataManager().getGroupData().getSize() > 2;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openKickPlayerCallback();
    }
}
