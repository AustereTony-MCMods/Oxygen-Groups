package austeretony.oxygen_groups.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.client.CPGroupsCommand;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroupOnLoad;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPGroupsRequest extends ProxyPacket {

    private EnumRequest request;

    public SPGroupsRequest() {}

    public SPGroupsRequest(EnumRequest request) {
        this.request = request;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.request.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        this.request = EnumRequest.values()[buffer.readByte()];
        switch (this.request) {
        case OPEN_GROUP_MENU:
            if (!OxygenHelperServer.isSyncing(playerUUID)) {
                OxygenHelperServer.syncSharedPlayersData(playerMP, OxygenHelperServer.getSharedDataIdentifiersForScreen(GroupsMain.GROUP_MENU_SCREEN_ID));
                GroupsMain.network().sendTo(new CPGroupsCommand(CPGroupsCommand.EnumCommand.OPEN_GROUP_MENU), playerMP);
            }
            break;
        case DOWNLOAD_GROUP_DATA_OPEN:
            if (!OxygenHelperServer.isSyncing(playerUUID)) {
                OxygenHelperServer.syncSharedPlayersData(playerMP, OxygenHelperServer.getSharedDataIdentifiersForScreen(GroupsMain.GROUP_MENU_SCREEN_ID));
                if (GroupsManagerServer.instance().haveGroup(playerUUID))
                    GroupsMain.network().sendTo(new CPSyncGroup(GroupsManagerServer.instance().getGroup(playerUUID)), playerMP);
                GroupsMain.network().sendTo(new CPGroupsCommand(CPGroupsCommand.EnumCommand.OPEN_GROUP_MENU), playerMP);
            }
            break;      
        case DOWNLOAD_GROUP_DATA:
            if (!OxygenHelperServer.isSyncing(playerUUID)) {
                OxygenHelperServer.syncSharedPlayersData(playerMP, OxygenHelperServer.getSharedDataIdentifiersForScreen(GroupsMain.GROUP_MENU_SCREEN_ID));
                if (GroupsManagerServer.instance().haveGroup(playerUUID))
                    GroupsMain.network().sendTo(new CPSyncGroupOnLoad(GroupsManagerServer.instance().getGroup(playerUUID)), playerMP);
            }
            break;
        case LEAVE_GROUP:
            GroupsManagerServer.instance().leaveGroup(playerUUID);
            break;
        case START_READINESS_CHECK:
            GroupsManagerServer.instance().startReadinessCheck(playerMP);
            break;
        }
    }

    public enum EnumRequest {

        OPEN_GROUP_MENU,
        DOWNLOAD_GROUP_DATA_OPEN,
        DOWNLOAD_GROUP_DATA,
        LEAVE_GROUP,
        START_READINESS_CHECK
    }
}
