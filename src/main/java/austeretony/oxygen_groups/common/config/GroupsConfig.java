package austeretony.oxygen_groups.common.config;

import java.util.Queue;

import austeretony.oxygen.common.api.config.AbstractConfigHolder;
import austeretony.oxygen.common.api.config.ConfigValue;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupsConfig extends AbstractConfigHolder {

    public static final ConfigValue
    GROUP_INVITE_REQUEST_EXPIRE_TIME = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "group_invite_request_expire_time_seconds"),
    READINESS_CHECK_REQUEST_EXPIRE_TIME = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "readiness_check_request_expire_time_seconds"),
    VOTE_KICK_REQUEST_EXPIRE_TIME = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "vote_kick_request_expire_time_seconds"),
    PLAYERS_PER_SQUAD = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "max_players_per_squad"),
    PLAYERS_PER_RAID = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "max_players_per_raid"),
    PLAYERS_PER_PARTY = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "max_players_per_party");

    @Override
    public String getModId() {
        return GroupsMain.MODID;
    }

    @Override
    public String getVersion() {
        return GroupsMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/groups/groups.json";
    }

    @Override
    public String getInternalPath() {
        return "assets/oxygen_groups/groups.json";
    }

    @Override
    public void getValues(Queue<ConfigValue> values) {
        values.add(GROUP_INVITE_REQUEST_EXPIRE_TIME);
        values.add(READINESS_CHECK_REQUEST_EXPIRE_TIME);
        values.add(VOTE_KICK_REQUEST_EXPIRE_TIME);
        values.add(PLAYERS_PER_SQUAD);
        values.add(PLAYERS_PER_RAID);
        values.add(PLAYERS_PER_PARTY);
    }

    @Override
    public boolean sync() {
        return true;
    }
}
