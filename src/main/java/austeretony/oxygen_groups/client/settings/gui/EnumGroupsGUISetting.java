package austeretony.oxygen_groups.client.settings.gui;

import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.settings.SettingValue;
import austeretony.oxygen_core.common.settings.SettingValueUtils;

public enum EnumGroupsGUISetting {

    //Alignment

    GROUP_MENU_ALIGNMENT("alignment_group_menu", EnumValueType.INT, String.valueOf(0)),

    //Offset

    GROUP_OVERLAY_OFFSET_X("offset_group_overlay_x", EnumValueType.INT, String.valueOf(10)),
    GROUP_OVERLAY_OFFSET_Y("offset_group_overlay_y", EnumValueType.INT, String.valueOf(80)),

    //Color

    OVERAY_BAR_BACKGROUND_COLOR("color_group_overlay_bar_background", EnumValueType.HEX, Integer.toHexString(0xff303030)),
    OVERAY_OFFLINE_BAR_COLOR("color_group_overlay_bar_offline", EnumValueType.HEX, Integer.toHexString(0xff606060)),
    OVERAY_HEALTH_BAR_COLOR("color_group_overlay_health_bar", EnumValueType.HEX, Integer.toHexString(0xff990000)),
    OVERAY_ABSORPTION_BAR_COLOR("color_group_overlay_absorption_bar", EnumValueType.HEX, Integer.toHexString(0xffd4a200)),
    OVERAY_ARMOR_BAR_COLOR("color_group_overlay_armor_bar", EnumValueType.HEX, Integer.toHexString(0xffb8b9c4)),

    //Scale

    OVERLAY_SQUAD_SCALE_FACTOR("offset_group_overlay_squad_scale_factor", EnumValueType.FLOAT, String.valueOf(1.0F)),
    OVERLAY_RAID_SCALE_FACTOR("offset_group_overlay_raid_scale_factor", EnumValueType.FLOAT, String.valueOf(0.85F)),
    OVERLAY_PARTY_SCALE_FACTOR("offset_group_overlay_party_scale_factor", EnumValueType.FLOAT, String.valueOf(0.7F)),

    //Misc

    OVERLAY_STATUS_BAR_TEXT("alignment_group_overlay_status_bar_text", EnumValueType.INT, String.valueOf(3));

    private final String key, baseValue;

    private final EnumValueType type;

    private SettingValue value;

    EnumGroupsGUISetting(String key, EnumValueType type, String baseValue) {
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
        for (EnumGroupsGUISetting setting : EnumGroupsGUISetting.values())
            OxygenManagerClient.instance().getClientSettingManager().register(SettingValueUtils.getValue(setting.type, setting.key, setting.baseValue));
    }
}
