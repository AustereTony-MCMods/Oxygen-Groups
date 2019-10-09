package austeretony.oxygen_groups.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPPromoteToLeader extends Packet {

    private int index;

    public SPPromoteToLeader() {}

    public SPPromoteToLeader(int index) {
        this.index = index;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), GroupsMain.PROMOTE_TO_LEADER_REQUEST_ID)) {
            final int index = buffer.readInt();
            GroupsManagerServer.instance().getGroupsDataManager().promoteToLeader(playerMP, index);
        }
    }
}