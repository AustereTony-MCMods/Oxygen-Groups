package austeretony.groups.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.groups.common.Group;
import austeretony.groups.common.config.GroupsConfig;
import austeretony.oxygen.common.api.OxygenHelperClient;

public class GroupDataClient {

    private UUID leaderUUID;

    private Map<UUID, GroupEntryClient> playersData = new ConcurrentHashMap<UUID, GroupEntryClient>();

    public GroupEntryClient getLeaderData() {
        return this.playersData.get(this.leaderUUID);
    }

    public void setLeader(UUID playerUUID) {
        this.leaderUUID = playerUUID;
    }

    public boolean isLeader(UUID playerUUID) {
        return this.leaderUUID.equals(playerUUID);
    }

    public boolean isClientLeader() {
        return this.leaderUUID.equals(OxygenHelperClient.getPlayerUUID());
    }

    public Set<UUID> getPlayersUUIDs() {
        return this.playersData.keySet();
    }

    public Collection<GroupEntryClient> getPlayersData() {
        return this.playersData.values();
    }

    public boolean exist(UUID playerUUID) {
        return this.playersData.containsKey(playerUUID);
    }

    public GroupEntryClient getPlayerData(UUID playerUUID) {
        return this.playersData.get(playerUUID);
    }

    public int getSize() {
        return this.playersData.size();
    }

    public Group.EnumMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.getIntValue())
            return Group.EnumMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.getIntValue())
            return Group.EnumMode.RAID;
        return Group.EnumMode.SQUAD;
    }

    public void addPlayerData(GroupEntryClient data) {
        this.playersData.put(data.playerUUID, data);
    }

    public void removePlayerData(UUID playerUUID) {
        this.playersData.remove(playerUUID);
    }

    public void clear() {
        this.playersData.clear();
    }
}
