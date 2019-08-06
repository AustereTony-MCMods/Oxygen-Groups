package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.common.main.Group;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncGroupOnLoad extends ProxyPacket {

    private Group group;

    public CPSyncGroupOnLoad() {}

    public CPSyncGroupOnLoad(Group group) {
        this.group = group;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        this.group.write(buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        GroupsManagerClient.instance().readGroupOnLoad(Group.read(buffer));
    }
}
