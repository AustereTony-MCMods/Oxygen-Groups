package austeretony.groups.common.network.client;

import austeretony.groups.client.GroupsManagerClient;
import austeretony.oxygen.common.network.ProxyPacket;
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
        buffer.writeShort(this.index);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerClient.instance().updateLeader(buffer.readShort());
    }
}
