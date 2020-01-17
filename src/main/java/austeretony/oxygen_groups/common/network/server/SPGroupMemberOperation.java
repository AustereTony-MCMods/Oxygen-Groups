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

public class SPGroupMemberOperation extends Packet {

    private UUID playerUUID;

    private int ordinal;

    public SPGroupMemberOperation() {}

    public SPGroupMemberOperation(UUID playerUUID, EnumOperation operation) {
        this.playerUUID = playerUUID;
        this.ordinal = operation.ordinal();
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
        buffer.writeByte(this.ordinal);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), GroupsMain.GROUP_MANAGEMENT_REQUEST_ID)) {
            final UUID playerUUID = ByteBufUtils.readUUID(buffer);
            final int ordinal = buffer.readByte();
            if (ordinal >= 0 && ordinal < EnumOperation.values().length) {
                final EnumOperation operation = EnumOperation.values()[ordinal];
                switch (operation) {
                case KICK:
                    OxygenHelperServer.addRoutineTask(()->GroupsManagerServer.instance().getGroupsDataManager().kickPlayer(playerMP, playerUUID));
                    break;
                case PROMOTE_TO_LEADER:
                    OxygenHelperServer.addRoutineTask(()->GroupsManagerServer.instance().getGroupsDataManager().promoteToLeader(playerMP, playerUUID));
                    break;           
                }
            }
        }
    }

    public enum EnumOperation {

        KICK,
        PROMOTE_TO_LEADER
    }
}
