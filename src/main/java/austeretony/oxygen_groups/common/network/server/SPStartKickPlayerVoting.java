package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPStartKickPlayerVoting extends Packet {

    private UUID playerUUID;

    public SPStartKickPlayerVoting() {}

    public SPStartKickPlayerVoting(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), GroupsMain.START_KICK_PLAYER_VOTING_REQUEST_ID)) {
            final UUID playerUUID = ByteBufUtils.readUUID(buffer);
            GroupsManagerServer.instance().getGroupsDataManager().startKickPlayerVoting(playerMP, playerUUID);
        }
    }
}
