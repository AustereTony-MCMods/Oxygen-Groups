package austeretony.oxygen_groups.client.gui.settings;

import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxButton;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListWrapperEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.settings.ElementsContainer;
import austeretony.oxygen_core.client.gui.settings.gui.ColorButton;
import austeretony.oxygen_core.client.gui.settings.gui.OffsetButton;
import austeretony.oxygen_core.client.gui.settings.gui.ScaleButton;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetColorCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetKeyCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetOffsetCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetScaleCallback;
import austeretony.oxygen_groups.client.gui.overlay.EnumStatusBarText;
import austeretony.oxygen_groups.client.settings.EnumGroupsClientSetting;
import austeretony.oxygen_groups.client.settings.gui.EnumGroupsGUISetting;

public class GroupsSettingsContainer implements ElementsContainer {

    //common

    private OxygenCheckBoxButton addGroupMenuButton, hideGroupOverlayButton, showActiveEffectsButton;

    //interface

    private OxygenDropDownList alignmentGroupMenu, alignmentGroupOverlay, statusBarText;

    private OffsetButton groupOverlayOffsetX, groupOverlayOffsetY;

    private ColorButton 
    barBackgroundColor, offlineBarColor, healthBarColor, absorptionBarColor, armorBarColor;

    private ScaleButton
    squadOverlayScaleFactor,
    raidOverlayScaleFactor,
    partyOverlayScaleFactor;

    private SetColorCallback setColorCallback;

    private SetScaleCallback setScaleCallback;

    private SetOffsetCallback setOffsetCallback;

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_groups.gui.settings.module.groups");
    }

    @Override
    public boolean hasCommonSettings() {
        return true;
    }

    @Override
    public boolean hasGUISettings() {
        return true;
    }

    @Override
    public void addCommon(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.oxygenMenu"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //add group menu to menu
        framework.addElement(new OxygenTextLabel(78, 34, ClientReference.localize("oxygen_groups.gui.settings.option.addGroupMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.addGroupMenuButton = new OxygenCheckBoxButton(68, 29));
        this.addGroupMenuButton.setToggled(EnumGroupsClientSetting.ADD_GROUP_MENU.get().asBoolean());
        this.addGroupMenuButton.setClickListener((mouseX, mouseY, mouseButton)->{
            EnumGroupsClientSetting.ADD_GROUP_MENU.get().setValue(String.valueOf(this.addGroupMenuButton.isToggled()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 45, ClientReference.localize("oxygen_core.gui.settings.option.misc"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //hide group overlay
        framework.addElement(new OxygenTextLabel(78, 54, ClientReference.localize("oxygen_groups.gui.settings.option.hideGroupOverlay"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.hideGroupOverlayButton = new OxygenCheckBoxButton(68, 49));
        this.hideGroupOverlayButton.setToggled(EnumGroupsClientSetting.HIDE_GROUP_OVERLAY.get().asBoolean());
        this.hideGroupOverlayButton.setClickListener((mouseX, mouseY, mouseButton)->{
            EnumGroupsClientSetting.HIDE_GROUP_OVERLAY.get().setValue(String.valueOf(this.hideGroupOverlayButton.isToggled()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        //show active effects
        framework.addElement(new OxygenTextLabel(78, 64, ClientReference.localize("oxygen_groups.gui.settings.option.showActiveEffects"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.showActiveEffectsButton = new OxygenCheckBoxButton(68, 59));
        this.showActiveEffectsButton.setToggled(EnumGroupsClientSetting.SHOW_ACTIVE_EFFECTS.get().asBoolean());
        this.showActiveEffectsButton.setClickListener((mouseX, mouseY, mouseButton)->{
            EnumGroupsClientSetting.SHOW_ACTIVE_EFFECTS.get().setValue(String.valueOf(this.showActiveEffectsButton.isToggled()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });
    }

    @Override
    public void addGUI(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 55, ClientReference.localize("oxygen_core.gui.settings.option.offset"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //group overlay horizontal offset
        framework.addElement(new OxygenTextLabel(68, 63, ClientReference.localize("oxygen_groups.gui.settings.option.groupOverlayOffsetX"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.groupOverlayOffsetX = new OffsetButton(68, 66, EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_X.get()));

        this.groupOverlayOffsetX.setClickListener((mouseX, mouseY, mouseButton)->{
            this.groupOverlayOffsetX.setHovered(false);
            this.setOffsetCallback.open(this.groupOverlayOffsetX);
        });

        //group overlay vertical offset
        framework.addElement(new OxygenTextLabel(68, 79, ClientReference.localize("oxygen_groups.gui.settings.option.groupOverlayOffsetY"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.groupOverlayOffsetY = new OffsetButton(68, 82, EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_Y.get()));

        this.groupOverlayOffsetY.setClickListener((mouseX, mouseY, mouseButton)->{
            this.groupOverlayOffsetY.setHovered(false);
            this.setOffsetCallback.open(this.groupOverlayOffsetY);
        });

        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.alignment"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //group overlay alignment

        String currAlignmentStr;
        switch (EnumGroupsGUISetting.GROUP_OVERLAY_ALIGNMENT.get().asInt()) {
        case 0: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        }
        framework.addElement(this.alignmentGroupOverlay = new OxygenDropDownList(135, 35, 55, currAlignmentStr));
        this.alignmentGroupOverlay.addElement(new OxygenDropDownListWrapperEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentGroupOverlay.addElement(new OxygenDropDownListWrapperEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentGroupOverlay.<OxygenDropDownListWrapperEntry<Integer>>setElementClickListener((element)->{
            EnumGroupsGUISetting.GROUP_OVERLAY_ALIGNMENT.get().setValue(String.valueOf(element.getWrapped()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(135, 33, ClientReference.localize("oxygen_groups.gui.settings.option.alignmentGroupOverlay"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        //group menu alignment

        switch (EnumGroupsGUISetting.GROUP_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 0:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        }
        framework.addElement(this.alignmentGroupMenu = new OxygenDropDownList(68, 35, 55, currAlignmentStr));
        this.alignmentGroupMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(- 1, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentGroupMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.center")));
        this.alignmentGroupMenu.addElement(new OxygenDropDownListWrapperEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentGroupMenu.<OxygenDropDownListWrapperEntry<Integer>>setElementClickListener((element)->{
            EnumGroupsGUISetting.GROUP_MENU_ALIGNMENT.get().setValue(String.valueOf(element.getWrapped()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 33, ClientReference.localize("oxygen_groups.gui.settings.option.alignmentGroupMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(new OxygenTextLabel(68, 97, ClientReference.localize("oxygen_core.gui.settings.option.color"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //bar colors

        //interface background color
        framework.addElement(new OxygenTextLabel(68, 105, ClientReference.localize("oxygen_groups.gui.settings.option.barColor"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.barBackgroundColor = new ColorButton(68, 107, EnumGroupsGUISetting.OVERAY_BAR_BACKGROUND_COLOR.get(), ClientReference.localize("oxygen_groups.gui.settings.tooltip.background")));

        this.barBackgroundColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.barBackgroundColor.setHovered(false);
            this.setColorCallback.open(this.barBackgroundColor);
        });

        framework.addElement(this.offlineBarColor = new ColorButton(78, 107, EnumGroupsGUISetting.OVERAY_OFFLINE_BAR_COLOR.get(), ClientReference.localize("oxygen_groups.gui.settings.tooltip.offline")));

        this.offlineBarColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.offlineBarColor.setHovered(false);
            this.setColorCallback.open(this.offlineBarColor);
        });

        framework.addElement(this.healthBarColor = new ColorButton(88, 107, EnumGroupsGUISetting.OVERAY_HEALTH_BAR_COLOR.get(), ClientReference.localize("oxygen_groups.gui.settings.tooltip.health")));

        this.healthBarColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.healthBarColor.setHovered(false);
            this.setColorCallback.open(this.healthBarColor);
        });

        framework.addElement(this.absorptionBarColor = new ColorButton(98, 107, EnumGroupsGUISetting.OVERAY_ABSORPTION_BAR_COLOR.get(), ClientReference.localize("oxygen_groups.gui.settings.tooltip.absorption")));

        this.absorptionBarColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.absorptionBarColor.setHovered(false);
            this.setColorCallback.open(this.absorptionBarColor);
        });

        framework.addElement(this.armorBarColor = new ColorButton(108, 107, EnumGroupsGUISetting.OVERAY_ARMOR_BAR_COLOR.get(), ClientReference.localize("oxygen_groups.gui.settings.tooltip.armor")));

        this.armorBarColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.armorBarColor.setHovered(false);
            this.setColorCallback.open(this.armorBarColor);
        });

        framework.addElement(new OxygenTextLabel(68, 125, ClientReference.localize("oxygen_core.gui.settings.option.scale"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //squad scale factor
        framework.addElement(new OxygenTextLabel(68, 134, ClientReference.localize("oxygen_groups.gui.settings.option.squadOverlayScaleFactor"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.squadOverlayScaleFactor = new ScaleButton(68, 138, EnumGroupsGUISetting.OVERLAY_SQUAD_SCALE_FACTOR.get()));

        this.squadOverlayScaleFactor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.squadOverlayScaleFactor.setHovered(false);
            this.setScaleCallback.open(this.squadOverlayScaleFactor);
        });

        //raid scale factor
        framework.addElement(new OxygenTextLabel(68, 151, ClientReference.localize("oxygen_groups.gui.settings.option.raidOverlayScaleFactor"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.raidOverlayScaleFactor = new ScaleButton(68, 155, EnumGroupsGUISetting.OVERLAY_RAID_SCALE_FACTOR.get()));

        this.raidOverlayScaleFactor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.raidOverlayScaleFactor.setHovered(false);
            this.setScaleCallback.open(this.raidOverlayScaleFactor);
        });

        //party scale factor
        framework.addElement(new OxygenTextLabel(68, 168, ClientReference.localize("oxygen_groups.gui.settings.option.partyOverlayScaleFactor"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.partyOverlayScaleFactor = new ScaleButton(68, 172, EnumGroupsGUISetting.OVERLAY_PARTY_SCALE_FACTOR.get()));

        this.partyOverlayScaleFactor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.partyOverlayScaleFactor.setHovered(false);
            this.setScaleCallback.open(this.partyOverlayScaleFactor);
        });

        //TODO
        framework.addElement(new OxygenTextLabel(68, 187, ClientReference.localize("oxygen_core.gui.settings.option.misc"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //status bar text

        String currBarTextStr = EnumStatusBarText.values()[EnumGroupsGUISetting.OVERLAY_STATUS_BAR_TEXT.get().asInt()].getLocalizedDescription();
        framework.addElement(this.statusBarText = new OxygenDropDownList(68, 197, 80, currBarTextStr));
        for (EnumStatusBarText enumText : EnumStatusBarText.values()) 
            this.statusBarText.addElement(new OxygenDropDownListWrapperEntry<Integer>(enumText.ordinal(), enumText.getLocalizedDescription()));

        this.statusBarText.<OxygenDropDownListWrapperEntry<Integer>>setElementClickListener((element)->{
            EnumGroupsGUISetting.OVERLAY_STATUS_BAR_TEXT.get().setValue(String.valueOf(element.getWrapped()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 195, ClientReference.localize("oxygen_groups.gui.settings.option.statusBarText"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

    }

    @Override
    public void resetCommon() {
        //add group menu to menu
        this.addGroupMenuButton.setToggled(true);
        EnumGroupsClientSetting.ADD_GROUP_MENU.get().reset();    

        //hide group overlay
        this.hideGroupOverlayButton.setToggled(false);
        EnumGroupsClientSetting.HIDE_GROUP_OVERLAY.get().reset(); 

        //show active effects
        this.showActiveEffectsButton.setToggled(true);
        EnumGroupsClientSetting.SHOW_ACTIVE_EFFECTS.get().reset(); 

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void resetGUI() {
        //group menu alignment
        this.alignmentGroupMenu.setDisplayText(ClientReference.localize("oxygen_core.alignment.center"));
        EnumGroupsGUISetting.GROUP_MENU_ALIGNMENT.get().reset();

        this.alignmentGroupOverlay.setDisplayText(ClientReference.localize("oxygen_core.alignment.left"));
        EnumGroupsGUISetting.GROUP_OVERLAY_ALIGNMENT.get().reset();

        EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_X.get().reset();
        this.groupOverlayOffsetX.setDisplayText(EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_X.get().getBaseValue());

        EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_Y.get().reset();
        this.groupOverlayOffsetY.setDisplayText(EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_Y.get().getBaseValue());

        //bar color
        EnumGroupsGUISetting.OVERAY_BAR_BACKGROUND_COLOR.get().reset();
        this.barBackgroundColor.setButtonColor(EnumGroupsGUISetting.OVERAY_BAR_BACKGROUND_COLOR.get().asInt());

        EnumGroupsGUISetting.OVERAY_OFFLINE_BAR_COLOR.get().reset();
        this.offlineBarColor.setButtonColor(EnumGroupsGUISetting.OVERAY_OFFLINE_BAR_COLOR.get().asInt());

        EnumGroupsGUISetting.OVERAY_HEALTH_BAR_COLOR.get().reset();
        this.healthBarColor.setButtonColor(EnumGroupsGUISetting.OVERAY_HEALTH_BAR_COLOR.get().asInt());

        EnumGroupsGUISetting.OVERAY_ABSORPTION_BAR_COLOR.get().reset();
        this.absorptionBarColor.setButtonColor(EnumGroupsGUISetting.OVERAY_ABSORPTION_BAR_COLOR.get().asInt());

        EnumGroupsGUISetting.OVERAY_ARMOR_BAR_COLOR.get().reset();
        this.armorBarColor.setButtonColor(EnumGroupsGUISetting.OVERAY_ARMOR_BAR_COLOR.get().asInt());

        //squad overlay scale factor
        EnumGroupsGUISetting.OVERLAY_SQUAD_SCALE_FACTOR.get().reset();
        this.squadOverlayScaleFactor.setDisplayText(EnumGroupsGUISetting.OVERLAY_SQUAD_SCALE_FACTOR.get().getBaseValue());

        //raid overlay scale factor
        EnumGroupsGUISetting.OVERLAY_RAID_SCALE_FACTOR.get().reset();
        this.raidOverlayScaleFactor.setDisplayText(EnumGroupsGUISetting.OVERLAY_RAID_SCALE_FACTOR.get().getBaseValue());

        //party overlay scale factor
        EnumGroupsGUISetting.OVERLAY_PARTY_SCALE_FACTOR.get().reset();
        this.partyOverlayScaleFactor.setDisplayText(EnumGroupsGUISetting.OVERLAY_PARTY_SCALE_FACTOR.get().getBaseValue());

        //status bar text
        EnumGroupsGUISetting.OVERLAY_STATUS_BAR_TEXT.get().reset();
        this.alignmentGroupMenu.setDisplayText(EnumStatusBarText.values()[EnumGroupsGUISetting.OVERLAY_STATUS_BAR_TEXT.get().asInt()].getLocalizedDescription());

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void initSetColorCallback(SetColorCallback callback) {
        this.setColorCallback = callback;
    }

    @Override
    public void initSetScaleCallback(SetScaleCallback callback) {
        this.setScaleCallback = callback;
    }

    @Override
    public void initSetOffsetCallback(SetOffsetCallback callback) {
        this.setOffsetCallback = callback;
    }

    @Override
    public void initSetKeyCallback(SetKeyCallback callback) {}
}

