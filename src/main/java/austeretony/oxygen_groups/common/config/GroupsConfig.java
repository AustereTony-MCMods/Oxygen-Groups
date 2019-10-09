package austeretony.oxygen_groups.common.config;

import java.util.List;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfigHolder;
import austeretony.oxygen_core.common.api.config.ConfigValueImpl;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupsConfig extends AbstractConfigHolder {

    public static final ConfigValue
    GROUPS_SAVE_DELAY_MINUTES = new ConfigValueImpl(EnumValueType.INT, "setup", "groups_save_delay_minutes"),

    GROUP_INVITE_REQUEST_EXPIRE_TIME_SECONDS = new ConfigValueImpl(EnumValueType.INT, "main", "group_invite_request_expire_time_seconds"),
    READINESS_CHECK_REQUEST_EXPIRE_TIME_SECONDS = new ConfigValueImpl(EnumValueType.INT, "main", "readiness_check_request_expire_time_seconds"),
    VOTE_KICK_REQUEST_EXPIRE_TIME_SECONDS = new ConfigValueImpl(EnumValueType.INT, "main", "vote_kick_request_expire_time_seconds"),
    PLAYERS_PER_SQUAD = new ConfigValueImpl(EnumValueType.INT, "main", "max_players_per_squad"),
    PLAYERS_PER_RAID = new ConfigValueImpl(EnumValueType.INT, "main", "max_players_per_raid"),
    PLAYERS_PER_PARTY = new ConfigValueImpl(EnumValueType.INT, "main", "max_players_per_party");

    @Override
    public String getDomain() {
        return GroupsMain.MODID;
    }

    @Override
    public String getVersion() {
        return GroupsMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/groups.json";
    }

    @Override
    public String getInternalPath() {
        return "assets/oxygen_groups/groups.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(GROUPS_SAVE_DELAY_MINUTES);

        values.add(GROUP_INVITE_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(READINESS_CHECK_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(VOTE_KICK_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(PLAYERS_PER_SQUAD);
        values.add(PLAYERS_PER_RAID);
        values.add(PLAYERS_PER_PARTY);
    }

    @Override
    public boolean sync() {
        return true;
    }
}
