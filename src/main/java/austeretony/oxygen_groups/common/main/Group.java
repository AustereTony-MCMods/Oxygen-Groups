package austeretony.oxygen_groups.common.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronizedData;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ConcurrentSet;

public class Group implements PersistentEntry, SynchronizedData {

    private long groupId;

    private UUID groupLeader;

    private final Set<UUID> players = new ConcurrentSet<>();

    private volatile boolean vote;

    private volatile int voteCounter;

    @Override
    public long getId() {
        return this.groupId;
    }

    public void setId(long id) {
        this.groupId = id;
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
        for (UUID playerUUID : this.players)
            if (OxygenHelperServer.isPlayerOnline(playerUUID) && !playerUUID.equals(this.groupLeader))
                return playerUUID;
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
        return this.vote;
    }

    public void startVote() {
        this.vote = true;
        this.voteCounter = 0;
    }

    public void stopVote() {
        this.vote = false;
    }

    public void vote() {
        this.voteCounter++;
    }

    public boolean getVoteResult() {
        return this.voteCounter > this.getSize() / 2;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.groupId, bos);
        StreamUtils.write(this.groupLeader, bos);
        StreamUtils.write((byte) this.players.size(), bos);
        for (UUID playerUUID : this.players)
            StreamUtils.write(playerUUID, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.groupId = StreamUtils.readLong(bis);
        this.groupLeader = StreamUtils.readUUID(bis);
        int amount = StreamUtils.readByte(bis);
        for (int i = 0; i < amount; i++)
            this.addPlayer(StreamUtils.readUUID(bis));
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufUtils.writeUUID(this.groupLeader, buffer);
        buffer.writeByte(this.players.size());
        for (UUID playerUUID : this.players)
            ByteBufUtils.writeUUID(playerUUID, buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.groupLeader = ByteBufUtils.readUUID(buffer);
        int amount = buffer.readByte();
        for (int i = 0; i < amount; i++)
            this.players.add(ByteBufUtils.readUUID(buffer));
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