package austeretony.groups.common.event;

import austeretony.groups.common.GroupsManagerServer;
import austeretony.oxygen.common.event.OxygenPlayerLoadedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class GroupsEventsServer {

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {        
        GroupsManagerServer.instance().onPlayerLoaded(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogOut(PlayerLoggedOutEvent event) {
        GroupsManagerServer.instance().onPlayerLoggedOut(event.player);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            GroupsManagerServer.instance().runGroupDataSynchronization();
    }
}
