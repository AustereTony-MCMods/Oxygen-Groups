package austeretony.groups.client.gui.group.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.groups.client.GroupsManagerClient;
import austeretony.groups.client.gui.group.GroupGUISection;
import austeretony.oxygen.common.api.OxygenHelperClient;
import net.minecraft.client.resources.I18n;

public class KickPlayerContextAction extends AbstractContextAction {

    public final GroupGUISection section;

    public KickPlayerContextAction(GroupGUISection section) {
        this.section = section;
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return I18n.format("groups.gui.action.kick");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        return !this.section.getCurrentEntry().playerUUID.equals(OxygenHelperClient.getPlayerUUID())
                && GroupsManagerClient.instance().getGroupData().getSize() != 2;
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        this.section.openKickPlayerCallback();
    }
}
