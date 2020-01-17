package austeretony.oxygen_groups.client;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_groups.common.Group.EnumGroupMode;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import io.netty.util.internal.ConcurrentSet;

public class GroupDataClient {

    private UUID leaderUUID;

    private Set<UUID> members = new ConcurrentSet<>();

    private volatile boolean active;

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean flag) {
        this.active = flag;
    }

    public int getSize() {
        return this.members.size();
    }

    public Set<UUID> getMembers() {
        return this.members;
    }

    public void addMember(UUID playerUUID) {
        this.members.add(playerUUID);
    }

    public void removeMember(UUID playerUUID) {
        this.members.remove(playerUUID);
    }

    public boolean isMember(UUID playerUUID) {
        return this.members.contains(playerUUID);
    }

    public UUID getLeaderUUID() {
        return this.leaderUUID;
    }

    public void setLeader(UUID playerUUID) {
        this.leaderUUID = playerUUID;
    }

    public boolean isLeader(UUID playerUUID) {
        return this.leaderUUID.equals(playerUUID);
    }

    public EnumGroupMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.asInt())
            return EnumGroupMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.asInt())
            return EnumGroupMode.RAID;
        return EnumGroupMode.SQUAD;
    }

    public void clear() {
        this.members.clear();
    }
}
