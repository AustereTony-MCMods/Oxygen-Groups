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

public class SPInviteToGroup extends Packet {

    private int index;

    public SPInviteToGroup() {}

    public SPInviteToGroup(int index) {
        this.index = index;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), GroupsMain.INVITE_TO_GROUP_REQUEST_ID)) {
            final int index = buffer.readInt();
            OxygenHelperServer.addRoutineTask(()->GroupsManagerServer.instance().getGroupsDataManager().inviteToGroup(playerMP, index));
        }
    }
}
