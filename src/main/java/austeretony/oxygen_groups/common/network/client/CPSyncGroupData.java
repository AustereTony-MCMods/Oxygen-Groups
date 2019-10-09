package austeretony.oxygen_groups.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncGroupData extends Packet {

    private int[] indexes;

    private float[] currHealth, maxHealth;

    public CPSyncGroupData() {}

    public CPSyncGroupData(int[] indexes, float[] currHealth, float[] maxHealth) {  
        this.indexes = indexes;
        this.currHealth = currHealth;
        this.maxHealth = maxHealth;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.indexes.length);
        for (int i = 0; i < this.indexes.length; i++) {
            buffer.writeInt(this.indexes[i]);
            buffer.writeFloat(this.currHealth[i]);
            buffer.writeFloat(this.maxHealth[i]);
        }
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        int amount = buffer.readByte();
        final int[] indexes = new int[amount];
        final float[] 
                currHealth = new float[amount], 
                maxHealth = new float[amount];
        for (int i = 0; i < amount; i++) {
            indexes[i] = buffer.readInt();
            currHealth[i] = buffer.readFloat();
            maxHealth[i] = buffer.readFloat();
        }
        OxygenHelperClient.addRoutineTask(()->GroupsManagerClient.instance().getGroupDataManager().getGroupData().updateGroupData(indexes, currHealth, maxHealth));
    }
}
