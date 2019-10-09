package austeretony.oxygen_groups.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.Group;

public class GroupDataClient {

    private UUID leaderUUID;

    private Map<UUID, GroupEntryClient> playersData = new ConcurrentHashMap<>();

    private volatile boolean active;

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean flag) {
        this.active = flag;
    }

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

    public void updateGroupData(int[] indexes, float[] currHealth, float[] maxHealth) {
        UUID playerUUID;
        GroupEntryClient data;
        for (int i = 0; i < indexes.length; i++) {
            if (OxygenHelperClient.isPlayerOnline(indexes[i])) {
                playerUUID = OxygenHelperClient.getPlayerSharedData(indexes[i]).getPlayerUUID();
                if (this.exist(playerUUID)) {
                    data = this.getPlayerData(playerUUID);
                    data.setHealth(currHealth[i]);
                    data.setMaxHealth(maxHealth[i]);
                }
            }
        }
    }

    public int getSize() {
        return this.playersData.size();
    }

    public Group.EnumGroupMode getMode() {
        if (this.getSize() > GroupsConfig.PLAYERS_PER_RAID.getIntValue())
            return Group.EnumGroupMode.PARTY;
        if (this.getSize() > GroupsConfig.PLAYERS_PER_SQUAD.getIntValue())
            return Group.EnumGroupMode.RAID;
        return Group.EnumGroupMode.SQUAD;
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
