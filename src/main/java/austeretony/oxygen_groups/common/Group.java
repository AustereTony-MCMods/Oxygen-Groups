package austeretony.oxygen_groups.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.network.PacketBuffer;

public class Group {

    private long groupId;

    private UUID groupLeader;

    private final Set<UUID> players = new ConcurrentSet<UUID>();

    private volatile int voteCounter;

    private volatile boolean voting;

    public long getId() {
        return this.groupId;
    }

    public void setId(long id) {
        this.groupId = id;
    }

    public void createId() {
        this.groupId = OxygenUtils.createDataStampedId();
    }   

    public UUID getLeader() {
        return this.groupLeader;
    }

    public void setLeader(UUID playerUUID) {
        this.groupLeader = playerUUID;
    }

    public boolean isLeader(UUID playerUUID) {
        return this.groupLeader.equals(playerUUID);
    }

    public UUID getRandomOnlinePlayer() {
        for (UUID uuid : this.players)
            if (OxygenHelperServer.isOnline(uuid) && !uuid.equals(this.groupLeader))
                return uuid;
        return null;
    }

    public int getSize() {
        return this.players.size();
    }

    public EnumGroupMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.getIntValue())
            return EnumGroupMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.getIntValue())
            return EnumGroupMode.RAID;
        return EnumGroupMode.SQUAD;
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public void addPlayer(UUID playerUUID) {
        this.players.add(playerUUID);
    }

    public void removePlayer(UUID playerUUID) {
        this.players.remove(playerUUID);
    }

    public boolean isVoting() {
        return this.voting;
    }

    public void startVote() {
        this.voting = true;
        this.voteCounter = 0;
    }

    public void stopVote() {
        this.voting = false;
    }

    public void voteFor() {
        this.voteCounter++;
    }

    public boolean getVoteResult() {
        return this.voteCounter > this.getSize() / 2;
    }

    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.groupId, bos);
        StreamUtils.write(this.groupLeader, bos);
        StreamUtils.write((byte) this.players.size(), bos);
        for (UUID playerUUID : this.players)
            StreamUtils.write(playerUUID, bos);
    }

    public static Group read(BufferedInputStream bis) throws IOException {
        Group group = new Group();
        group.groupId = StreamUtils.readLong(bis);
        group.groupLeader = StreamUtils.readUUID(bis);
        int amount = StreamUtils.readByte(bis);
        for (int i = 0; i < amount; i++)
            group.addPlayer(StreamUtils.readUUID(bis));
        return group;
    }

    public void write(PacketBuffer buffer) {
        PacketBufferUtils.writeUUID(this.groupLeader, buffer);
        buffer.writeByte(this.players.size());
        for (UUID playerUUID : this.players)
            PacketBufferUtils.writeUUID(playerUUID, buffer);
    }

    public static Group read(PacketBuffer buffer) {
        Group group = new Group();
        group.groupLeader = PacketBufferUtils.readUUID(buffer);
        int amount = buffer.readByte();
        for (int i = 0; i < amount; i++)
            group.players.add(PacketBufferUtils.readUUID(buffer));
        return group;
    }

    private void resetData() {
        this.players.clear();
    }

    public enum EnumGroupMode {

        SQUAD,
        RAID,
        PARTY;

        public String localizedName() {
            return ClientReference.localize("groups.groupMode." + this.toString().toLowerCase());
        }  
    }
}