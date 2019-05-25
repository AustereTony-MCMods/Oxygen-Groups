package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPGroupsCommand extends ProxyPacket {

    private EnumCommand command;

    public CPGroupsCommand() {}

    public CPGroupsCommand(EnumCommand command) {
        this.command = command;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.command.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.command = EnumCommand.values()[buffer.readByte()];
        switch (this.command) {
        case OPEN_GROUP_MENU:
            GroupsManagerClient.instance().openGroupMenuDelegated();
            break;
        case LEAVE_GROUP:
            GroupsManagerClient.instance().leaveGroup();
            break;
        }
    }

    public enum EnumCommand {

        OPEN_GROUP_MENU,
        LEAVE_GROUP
    }
}
