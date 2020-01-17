package austeretony.oxygen_groups.client.settings;

import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.settings.SettingValue;
import austeretony.oxygen_core.common.settings.SettingValueUtils;

public enum EnumGroupsClientSetting {

    //Misc

    HIDE_GROUP_OVERLAY("misc_hide_group_overlay", EnumValueType.BOOLEAN, String.valueOf(false)),
    SHOW_ACTIVE_EFFECTS("misc_show_active_group_effects", EnumValueType.BOOLEAN, String.valueOf(true)),

    //Oxygen Menu

    ADD_GROUP_MENU("menu_add_group_menu", EnumValueType.BOOLEAN, String.valueOf(true));

    private final String key, baseValue;

    private final EnumValueType type;

    private SettingValue value;

    EnumGroupsClientSetting(String key, EnumValueType type, String baseValue) {
        this.key = key;
        this.type = type;
        this.baseValue = baseValue;
    }

    public SettingValue get() {
        if (this.value == null)
            this.value = OxygenManagerClient.instance().getClientSettingManager().getSettingValue(this.key);
        return this.value;
    }

    public static void register() {
        for (EnumGroupsClientSetting setting : EnumGroupsClientSetting.values())
            OxygenManagerClient.instance().getClientSettingManager().register(SettingValueUtils.getValue(setting.type, setting.key, setting.baseValue));
    }
}
