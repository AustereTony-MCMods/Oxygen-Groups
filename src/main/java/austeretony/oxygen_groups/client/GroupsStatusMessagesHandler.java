package austeretony.oxygen_groups.client;

import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupsStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return GroupsMain.GROUPS_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        return EnumGroupsStatusMessage.values()[messageIndex].localizedName();
    }
}
