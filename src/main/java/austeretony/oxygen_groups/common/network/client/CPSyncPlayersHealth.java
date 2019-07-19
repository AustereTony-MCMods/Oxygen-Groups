package austeretony.oxygen_groups.common.network.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_groups.client.GroupEntryClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncPlayersHealth extends ProxyPacket {

    private int[] indexes;

    private float[] currHealth, maxHealth;

    public CPSyncPlayersHealth() {}

    public CPSyncPlayersHealth(int[] indexes, float[] currHealth, float[] maxHealth) {  
        this.indexes = indexes;
        this.currHealth = currHealth;
        this.maxHealth = maxHealth;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.indexes.length);
        for (int i = 0; i < this.indexes.length; i++) {
            buffer.writeInt(this.indexes[i]);
            buffer.writeFloat(this.currHealth[i]);
            buffer.writeFloat(this.maxHealth[i]);
        }
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        int 
        amount = buffer.readByte(),
        index;
        UUID playerUUID;
        GroupEntryClient data;
        for (int i = 0; i < amount; i++) {
            index = buffer.readInt();
            if (OxygenHelperClient.isOnline(index)) {
                playerUUID = OxygenHelperClient.getSharedPlayerData(index).getPlayerUUID();
                if (GroupsManagerClient.instance().getGroupData().exist(playerUUID)) {
                    data = GroupsManagerClient.instance().getGroupData().getPlayerData(playerUUID);
                    data.setHealth(buffer.readFloat());
                    data.setMaxHealth(buffer.readFloat());
                }
            }
        }
    }
}
