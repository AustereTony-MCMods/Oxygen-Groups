package austeretony.oxygen_groups.common.event;

import austeretony.oxygen.common.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupsEventsServer {

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {        
        GroupsManagerServer.instance().onPlayerLoaded(event.player);
    }
}