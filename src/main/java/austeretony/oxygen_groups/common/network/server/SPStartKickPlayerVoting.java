package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.common.util.PacketBufferUtils;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPStartKickPlayerVoting extends ProxyPacket {

    private UUID playerUUID;

    public SPStartKickPlayerVoting() {}

    public SPStartKickPlayerVoting(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        PacketBufferUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerServer.instance().startKickPlayerVoting(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
    }
}
