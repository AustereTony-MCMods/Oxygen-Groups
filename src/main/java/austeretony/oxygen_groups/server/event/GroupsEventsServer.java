package austeretony.oxygen_groups.server.event;

import austeretony.oxygen_core.server.api.event.OxygenActivityStatusChangedEvent;
import austeretony.oxygen_core.server.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenWorldLoadedEvent;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupsEventsServer {

    @SubscribeEvent
    public void onWorldLoaded(OxygenWorldLoadedEvent event) {        
        GroupsManagerServer.instance().worldLoaded();
    }

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {        
        GroupsManagerServer.instance().playerLoaded(event.playerMP);
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {        
        GroupsManagerServer.instance().playerUnloaded(event.playerMP);
    }

    @SubscribeEvent
    public void onPlayerChangedStatusActivity(OxygenActivityStatusChangedEvent event) {    
        GroupsManagerServer.instance().playerChangedStatusActivity(event.playerMP, event.newStatus);
    }
}
