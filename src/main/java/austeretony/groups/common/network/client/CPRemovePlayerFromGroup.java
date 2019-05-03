package austeretony.groups.common.network.client;

import java.util.UUID;

import austeretony.groups.client.GroupsManagerClient;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.common.util.PacketBufferUtils;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPRemovePlayerFromGroup extends ProxyPacket {

    private UUID playerUUID;

    public CPRemovePlayerFromGroup() {}

    public CPRemovePlayerFromGroup(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        PacketBufferUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerClient.instance().removeFromGroup(PacketBufferUtils.readUUID(buffer));
    }
}
