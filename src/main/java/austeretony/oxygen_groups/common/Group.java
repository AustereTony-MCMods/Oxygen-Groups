package austeretony.oxygen_groups.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.persistent.PersistentEntry;
import austeretony.oxygen_core.common.sync.SynchronousEntry;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ConcurrentSet;

public class Group implements PersistentEntry, SynchronousEntry {

    private long id;

    private UUID leaderUUID;

    private final Set<UUID> members = new ConcurrentSet<>();

    public Group() {}

    public Group(long id, UUID leaderUUID) {
        this.id = id;
        this.leaderUUID = leaderUUID;
        this.addMember(leaderUUID);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getLeader() {
        return this.leaderUUID;
    }

    public void setLeader(UUID playerUUID) {
        this.leaderUUID = playerUUID;
    }

    public boolean isLeader(UUID playerUUID) {
        return this.leaderUUID.equals(playerUUID);
    }

    public int getSize() {
        return this.members.size();
    }

    public Set<UUID> getMembers() {
        return this.members;
    }

    public boolean isMember(UUID playerUUID) {
        return this.members.contains(playerUUID);
    }

    public void addMember(UUID playerUUID) {
        this.members.add(playerUUID);
    }

    public void removeMember(UUID playerUUID) {
        this.members.remove(playerUUID);
    }   

    @Nullable
    public UUID getRandomOnlinePlayer() {
        for (UUID playerUUID : this.members)
            if (OxygenHelperServer.isPlayerOnline(playerUUID) && !playerUUID.equals(this.leaderUUID))
                return playerUUID;
        return null;
    }

    public EnumGroupMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.asInt())
            return EnumGroupMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.asInt())
            return EnumGroupMode.RAID;
        return EnumGroupMode.SQUAD;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write(this.id, bos);
        StreamUtils.write(this.leaderUUID, bos);
        StreamUtils.write((byte) this.members.size(), bos);
        for (UUID playerUUID : this.members)
            StreamUtils.write(playerUUID, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.id = StreamUtils.readLong(bis);
        this.leaderUUID = StreamUtils.readUUID(bis);
        int amount = StreamUtils.readByte(bis);
        for (int i = 0; i < amount; i++)
            this.addMember(StreamUtils.readUUID(bis));
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufUtils.writeUUID(this.leaderUUID, buffer);
        buffer.writeByte(this.members.size());
        for (UUID playerUUID : this.members)
            ByteBufUtils.writeUUID(playerUUID, buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.leaderUUID = ByteBufUtils.readUUID(buffer);
        int amount = buffer.readByte();
        for (int i = 0; i < amount; i++)
            this.members.add(ByteBufUtils.readUUID(buffer));
    }

    public enum EnumGroupMode {

        SQUAD,
        RAID,
        PARTY;

        public String localizedName() {
            return ClientReference.localize("oxygen_groups.groupMode." + this.toString().toLowerCase());
        }  
    }
}