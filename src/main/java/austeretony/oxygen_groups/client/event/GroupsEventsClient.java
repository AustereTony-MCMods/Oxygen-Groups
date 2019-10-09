package austeretony.oxygen_groups.client.event;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.event.OxygenChatMessageEvent;
import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_core.client.api.event.OxygenNotificationRecievedEvent;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.common.main.EnumGroupsChatMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupsEventsClient {

    @SubscribeEvent     
    public void onClientInit(OxygenClientInitEvent event) {
        GroupsManagerClient.instance().init();
    }

    @SubscribeEvent
    public void onChatMessage(OxygenChatMessageEvent event) {
        if (event.modIndex == GroupsMain.GROUPS_MOD_INDEX)
            EnumGroupsChatMessage.values()[event.messageIndex].show(event.args);
    }

    @SubscribeEvent
    public void onNotificationRecieved(OxygenNotificationRecievedEvent event) {
        if (event.notification.getIndex() == GroupsMain.GROUP_REQUEST_ID 
                && OxygenHelperClient.getClientSettingBoolean(GroupsMain.AUTO_ACCEPT_GROUP_INVITE_SETTING_ID))
            event.notification.accepted(null);
    }
}
