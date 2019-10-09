package austeretony.oxygen_groups.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPStartReadinessCheck extends Packet {

    public SPStartReadinessCheck() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), GroupsMain.START_READINESS_CHECK_REQUEST_ID))
            OxygenHelperServer.addRoutineTask(()->GroupsManagerServer.instance().getGroupsDataManager().startReadinessCheck(playerMP));
    }
}
