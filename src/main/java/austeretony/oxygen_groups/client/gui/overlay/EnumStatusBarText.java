package austeretony.oxygen_groups.client.gui.overlay;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumStatusBarText {

    NONE("oxygen_groups.gui.overlay.barText.none"),
    HEALTH_AND_MAX_HEALTH("oxygen_groups.gui.overlay.barText.healthAndMaxHealth"),
    HEALTH_AND_MAX_HEALTH_PLUS_ABSORPTION("oxygen_groups.gui.overlay.barText.healthAndMaxHealthPlusAbsorption"),
    HEALTH_PERCENT("oxygen_groups.gui.overlay.barText.healthPercent"),
    HEALTH_PERCENT_PLUS_ABSORPTION("oxygen_groups.gui.overlay.barText.healthPercentPlusAbsorption");

    private final String description;

    EnumStatusBarText(String description) {
        this.description = description;
    }

    public String getLocalizedDescription() {
        return ClientReference.localize(this.description, "%");
    }
}
