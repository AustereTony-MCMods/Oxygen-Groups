package austeretony.groups.client;

import java.util.UUID;

public class GroupEntryClient implements Comparable<GroupEntryClient> {

    public final UUID playerUUID;

    public final String username;

    private float currHealth, maxHealth;

    public GroupEntryClient(UUID playerUUID, String username) {
        this.playerUUID = playerUUID;
        this.username = username;
    }

    @Override
    public int compareTo(GroupEntryClient other) {
        return this.username.compareTo(other.username);
    }

    public float getHealth() {
        return this.currHealth;
    }

    public void setHealth(float value) {
        this.currHealth = value;
    }

    public float getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(float value) {
        this.maxHealth = value;
    }
}
