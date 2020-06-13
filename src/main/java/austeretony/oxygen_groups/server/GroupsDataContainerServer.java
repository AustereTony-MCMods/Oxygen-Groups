package austeretony.oxygen_groups.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.Group;

public class GroupsDataContainerServer extends AbstractPersistentData {

    private final Map<Long, Group> groups = new ConcurrentHashMap<>();

    private final Map<UUID, Long> access = new ConcurrentHashMap<>();

    public Collection<Group> getGroups() {
        return this.groups.values();
    }

    @Nullable
    public Group getGroup(long groupId) {
        return this.groups.get(groupId);
    }

    @Nullable
    public Group getGroup(UUID playerUUID) {
        Long groupId = this.access.get(playerUUID);
        return groupId != null ? this.groups.get(groupId) : null;
    }

    public void addGroup(Group group) {
        this.groups.put(group.getId(), group);
    }

    public void removeGroup(long groupId) {
        this.groups.remove(groupId);
    }

    public void playerJoinedGroup(UUID playerUUID, long groupId) {
        this.access.put(playerUUID, groupId);
    }

    public void playerLeftGroup(UUID playerUUID) {
        this.access.remove(playerUUID);
    }

    public long createId(long seed) {
        long id = ++seed;
        while (this.groups.containsKey(id))
            id++;
        return id;
    }

    @Override
    public String getDisplayName() {   
        return "groups:groups_data_server";
    }

    @Override
    public String getPath() {
        return OxygenHelperServer.getDataFolder() + "/server/world/groups/groups.dat";
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
            for (UUID playerUUID : group.getMembers())
                this.access.put(playerUUID, group.getId());
        }
    }

    @Override
    public void reset() {
        this.groups.clear();
        this.access.clear();
    }
}
