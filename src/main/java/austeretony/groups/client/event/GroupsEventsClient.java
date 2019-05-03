package austeretony.groups.client.event;

import austeretony.groups.client.GroupsManagerClient;
import austeretony.groups.common.main.EnumGroupsChatMessages;
import austeretony.groups.common.main.GroupsMain;
import austeretony.oxygen.client.event.OxygenChatMessageEvent;
import austeretony.oxygen.client.event.OxygenClientInitEvent;
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
}
