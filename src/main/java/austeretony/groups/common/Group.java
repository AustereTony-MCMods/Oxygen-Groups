package austeretony.groups.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import austeretony.groups.common.config.GroupsConfig;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.util.PacketBufferUtils;
import austeretony.oxygen.common.util.StreamUtils;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;

public class Group {

    private long groupId;

    private UUID groupLeader;

    private final Set<UUID> players = new ConcurrentSet<UUID>();

    private volatile int voteCounter;

    private volatile boolean voting;

    public Group() {}

    public long getId() {
        return this.groupId;
    }

    public void setId(long id) {
        this.groupId = id;
    }

    public void createId() {
        this.groupId = Long.parseLong(OxygenMain.SIMPLE_ID_DATE_FORMAT.format(new Date()));
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
            if (OxygenHelperServer.isOnline(uuid))
                return uuid;
        return null;
    }

    public int getSize() {
        return this.players.size();
    }

    public EnumMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.getIntValue())
            return EnumMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.getIntValue())
            return EnumMode.RAID;
        return EnumMode.SQUAD;
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

    public enum EnumMode {

        SQUAD,
        RAID,
        PARTY;

        public String localizedName() {
            return I18n.format("groups.groupMode." + this.toString().toLowerCase());
        }  
    }
}