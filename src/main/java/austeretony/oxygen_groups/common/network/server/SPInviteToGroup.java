package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.common.util.PacketBufferUtils;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPInviteToGroup extends ProxyPacket {

    private UUID targetUUID;

    public SPInviteToGroup() {}

    public SPInviteToGroup(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        PacketBufferUtils.writeUUID(this.targetUUID, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerServer.instance().inviteToGroup(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
    }
}
