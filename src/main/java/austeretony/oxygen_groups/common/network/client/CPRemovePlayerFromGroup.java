package austeretony.oxygen_groups.common.network.client;

import java.util.UUID;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPRemovePlayerFromGroup extends Packet {

    private UUID playerUUID;

    public CPRemovePlayerFromGroup() {}

    public CPRemovePlayerFromGroup(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final UUID playerUUID = ByteBufUtils.readUUID(buffer);
        OxygenHelperClient.addRoutineTask(()->GroupsManagerClient.instance().getGroupDataManager().removeGroupMember(playerUUID));
    }
}
