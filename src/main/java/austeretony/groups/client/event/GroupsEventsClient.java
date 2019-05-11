package austeretony.groups.client.event;

import austeretony.groups.client.GroupsManagerClient;
import austeretony.groups.common.main.EnumGroupsChatMessages;
import austeretony.groups.common.main.GroupsMain;
import austeretony.oxygen.client.event.OxygenChatMessageEvent;
import austeretony.oxygen.client.event.OxygenClientInitEvent;
import austeretony.oxygen.client.event.OxygenNotificationRecievedEvent;
import austeretony.oxygen.common.api.OxygenHelperClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupsEventsClient {

    @SubscribeEvent
    public void onClientInit(OxygenClientInitEvent event) {
        GroupsManagerClient.instance().reset();
    }

    @SubscribeEvent
    public void onChatMessage(OxygenChatMessageEvent event) {
        if (event.modIndex == GroupsMain.GROUPS_MOD_INDEX)
            EnumGroupsChatMessages.values()[event.messageIndex].show(event.args);
    }

    @SubscribeEvent
    public void onNotificationRecieved(OxygenNotificationRecievedEvent event) {
        if (event.notification.getIndex() == GroupsMain.GROUP_REQUEST_ID 
                && OxygenHelperClient.getClientSettingBoolean(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING))
            event.notification.accepted(null);
    }
}
