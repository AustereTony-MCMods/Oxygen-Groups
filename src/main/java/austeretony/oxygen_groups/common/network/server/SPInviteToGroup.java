package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPInviteToGroup extends Packet {

    private UUID playerUUID;

    public SPInviteToGroup() {}

    public SPInviteToGroup(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), GroupsMain.GROUP_MANAGEMENT_REQUEST_ID)) {
            final UUID playerUUID = ByteBufUtils.readUUID(buffer);
            OxygenHelperServer.addRoutineTask(()->GroupsManagerServer.instance().getGroupsDataManager().inviteToGroup(playerMP, playerUUID));
        }
    }
}
