package austeretony.oxygen_groups.client.event;

import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupsEventsClient {

    @SubscribeEvent     
    public void onClientInit(OxygenClientInitEvent event) {
        GroupsManagerClient.instance().worldLoaded();
    }
}
