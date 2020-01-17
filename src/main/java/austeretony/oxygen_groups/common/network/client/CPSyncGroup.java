package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.common.Group;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncGroup extends Packet {

    private Group group;

    public CPSyncGroup() {}

    public CPSyncGroup(Group group) {
        this.group = group;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        this.group.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final Group group = new Group();
        group.read(buffer);
        OxygenHelperClient.addRoutineTask(()->GroupsManagerClient.instance().getGroupDataManager().scheduleGroupUpdate(group));
    }
}
