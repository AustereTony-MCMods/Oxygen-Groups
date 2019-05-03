package austeretony.groups.client.gui.group.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.groups.client.GroupsManagerClient;
import austeretony.groups.client.gui.group.GroupGUISection;
import austeretony.oxygen.common.api.OxygenHelperClient;
import net.minecraft.client.resources.I18n;

public class PromoteToLeaderContextAction extends AbstractContextAction {

    public final GroupGUISection section;

    public PromoteToLeaderContextAction(GroupGUISection section) {
        this.section = section;
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return I18n.format("groups.gui.action.promoteToLeader");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        return !this.section.getCurrentEntry().playerUUID.equals(OxygenHelperClient.getPlayerUUID()) 
                && GroupsManagerClient.instance().getGroupData().isClientLeader() 
                && (OxygenHelperClient.isOnline(this.section.getCurrentEntry().playerUUID) && !OxygenHelperClient.isOfflineStatus(this.section.getCurrentEntry().playerUUID));
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        this.section.openPromoteToLeaderCallback();
    }
}
