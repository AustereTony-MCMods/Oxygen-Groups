package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPLeaveGroup extends Packet {

    public CPLeaveGroup() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        OxygenHelperClient.addRoutineTask(()->GroupsManagerClient.instance().getGroupDataManager().leaveGroup());
    }
}
