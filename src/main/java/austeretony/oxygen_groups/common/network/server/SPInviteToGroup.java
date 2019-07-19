package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPInviteToGroup extends ProxyPacket {

    private int targetIndex;

    public SPInviteToGroup() {}

    public SPInviteToGroup(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeInt(this.targetIndex);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerServer.instance().inviteToGroup(getEntityPlayerMP(netHandler), buffer.readInt());
    }
}
