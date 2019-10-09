package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPUpdateLeader extends Packet {

    private int index;

    public CPUpdateLeader() {}

    public CPUpdateLeader(int index) {
        this.index = index;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int index = buffer.readInt();
        OxygenHelperClient.addRoutineTask(()->GroupsManagerClient.instance().getGroupDataManager().updateLeader(index));
    }
}
