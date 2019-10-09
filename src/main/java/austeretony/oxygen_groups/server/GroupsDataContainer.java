package austeretony.oxygen_groups.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.Group;

public class GroupsDataContainer extends AbstractPersistentData {

    private final Map<Long, Group> groups = new ConcurrentHashMap<>();

    private final Map<UUID, Long> access = new ConcurrentHashMap<>();

    public Collection<Group> getGroups() {
        return this.groups.values();
    }

    public boolean groupExist(long groupId) {
        return this.groups.containsKey(groupId);
    }

    public Group getGroup(long groupId) {
        return this.groups.get(groupId);
    }

    public Group getGroup(UUID playerUUID) {
        return this.groups.get(this.access.get(playerUUID));
    }

    public boolean haveGroup(UUID playerUUID) {
        return this.access.containsKey(playerUUID);
    }

    public void addGroup(Group group) {
        this.groups.put(group.getId(), group);
    }

    public void removeGroup(long groupId) {
        this.groups.remove(groupId);
    }

    public void addGroupAccess(long groupId, UUID playerUUID) {
        this.access.put(playerUUID, groupId);
    }

    public void removeGroupAccess(UUID playerUUID) {
        this.access.remove(playerUUID);
    }

    public long getNewGroupId() {
        long id = System.currentTimeMillis();
        while (this.groups.containsKey(id))
            id++;
        return id;
    }

    @Override
    public String getDisplayName() {   
        return "groups_data";
    }

    @Override
    public String getPath() {
        return OxygenHelperServer.getDataFolder() + "/server/world/groups/groups.dat";
    }

    @Override
    public long getSaveDelayMinutes() {
        return GroupsConfig.GROUPS_SAVE_DELAY_MINUTES.getIntValue();
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.groups.size(), bos);
        for (Group group : this.groups.values())
            group.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        Group group;
        for (int i = 0; i < amount; i++) {
            group = new Group();
            group.read(bis);
            this.groups.put(group.getId(), group);
            for (UUID playerUUID : group.getPlayers())
                this.access.put(playerUUID, group.getId());
        }
    }

    @Override
    public void reset() {
        this.groups.clear();
        this.access.clear();
    }
}
