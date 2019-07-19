package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPUpdateLeader extends ProxyPacket {

    private int index;

    public CPUpdateLeader() {}

    public CPUpdateLeader(int index) {
        this.index = index;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeInt(this.index);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerClient.instance().updateLeader(buffer.readInt());
    }
}
