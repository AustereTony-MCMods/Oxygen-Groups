package austeretony.oxygen_groups.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_groups.common.main.GroupsMain;

public class GroupsConfig extends AbstractConfig {

    public static final ConfigValue
    ENABLE_GROUP_MENU_KEY = ConfigValueUtils.getValue("client", "enable_group_menu_key", true),  
    GROUP_MENU_KEY = ConfigValueUtils.getValue("client", "group_menu_key", 25),

    DISABLE_PVP_FOR_GROUP_MEMBERS = ConfigValueUtils.getValue("server", "disable_pvp_for_group_members", true),
    GROUP_INVITE_REQUEST_EXPIRE_TIME_SECONDS = ConfigValueUtils.getValue("server", "group_invite_request_expire_time_seconds", 20),
    PLAYERS_PER_SQUAD = ConfigValueUtils.getValue("server", "max_players_per_squad", 4, true),
    PLAYERS_PER_RAID = ConfigValueUtils.getValue("server", "max_players_per_raid", 12, true),
    PLAYERS_PER_PARTY = ConfigValueUtils.getValue("server", "max_players_per_party", 24, true),
    ENABLE_GROUP_CHAT = ConfigValueUtils.getValue("server", "enable_group_chat", true, true),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

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
    public void getValues(List<ConfigValue> values) {
        values.add(ENABLE_GROUP_MENU_KEY);
        values.add(GROUP_MENU_KEY);

        values.add(DISABLE_PVP_FOR_GROUP_MEMBERS);
        values.add(GROUP_INVITE_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(PLAYERS_PER_SQUAD);
        values.add(PLAYERS_PER_RAID);
        values.add(PLAYERS_PER_PARTY);
        values.add(ENABLE_GROUP_CHAT);
        values.add(ADVANCED_LOGGING);
    }
}
