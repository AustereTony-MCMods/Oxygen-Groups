package austeretony.oxygen_groups.client.gui.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.overlay.Overlay;
import austeretony.oxygen_core.client.instant.InstantDataContainer;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_groups.client.GroupDataClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.GroupsGUITextures;
import austeretony.oxygen_groups.client.settings.EnumGroupsClientSetting;
import austeretony.oxygen_groups.client.settings.gui.EnumGroupsGUISetting;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class GroupOverlay implements Overlay {

    private Minecraft mc;

    private float scale;

    private int x, y, playersPerSquad, baseOverlayTextColor, additionalOverlayTextColor, barBackgroundColor, 
    barOfflineColor, healthBarColor, absorptionBarColor, armorBarColor;

    private boolean valid, showEffects;

    private Set<GroupMemberOverlayEntry> members = new TreeSet<>();

    private String offlineStr;

    @Override
    public boolean valid() {
        if (ClientReference.getClientPlayer().ticksExisted % 20 == 0) {
            this.valid = GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() && !EnumGroupsClientSetting.HIDE_GROUP_OVERLAY.get().asBoolean();
            if (this.valid)
                this.updateOverlay();
        }
        return this.valid;
    }

    @Override
    public boolean drawWhileInGUI() {
        return true;
    }

    private void updateOverlay() {
        this.mc = ClientReference.getMinecraft();

        GroupDataClient groupData = GroupsManagerClient.instance().getGroupDataManager().getGroupData();

        this.scale = EnumBaseGUISetting.OVERLAY_SCALE.get().asFloat();

        int xOffset = 0;
        switch (groupData.getMode()) {
        case SQUAD:
            this.scale *= EnumGroupsGUISetting.OVERLAY_SQUAD_SCALE_FACTOR.get().asFloat();
            xOffset = (int) (90 * 1 * this.scale);
            break;
        case RAID:
            this.scale *= EnumGroupsGUISetting.OVERLAY_RAID_SCALE_FACTOR.get().asFloat();
            xOffset = (int) (90 * 2 * this.scale);
            break;
        case PARTY:
            this.scale *= EnumGroupsGUISetting.OVERLAY_PARTY_SCALE_FACTOR.get().asFloat();
            xOffset = (int) (90 * 3 * this.scale);
            break;
        }

        ScaledResolution sr = new ScaledResolution(this.mc);
        this.x = EnumGroupsGUISetting.GROUP_OVERLAY_ALIGNMENT.get().asInt() == 0 ? EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_X.get().asInt() : sr.getScaledWidth() - xOffset - EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_X.get().asInt();
        this.y = EnumGroupsGUISetting.GROUP_OVERLAY_OFFSET_Y.get().asInt();

        this.showEffects = EnumGroupsClientSetting.SHOW_ACTIVE_EFFECTS.get().asBoolean();

        this.members.clear();
        PlayerSharedData sharedData;
        InstantDataContainer instantData;
        List<PotionEffect> effects;
        for (UUID memberUUID : groupData.getMembers()) {
            sharedData = OxygenHelperClient.getPlayerSharedData(memberUUID);
            if (sharedData != null) {
                instantData = OxygenHelperClient.getInstantDataContainer(sharedData.getIndex());
                if (instantData != null) {
                    effects = new ArrayList<>((Collection<PotionEffect>) instantData.get(OxygenMain.ACTIVE_EFFECTS_INSTANT_DATA_INDEX).getValue());
                    Collections.sort(effects, (e1, e2)->e1.getDuration() - e2.getDuration());
                    this.members.add(new GroupMemberOverlayEntry(
                            sharedData.getUsername(),
                            (Float) instantData.get(OxygenMain.HEALTH_INSTANT_DATA_INDEX).getValue(),
                            (Float) instantData.get(OxygenMain.MAX_HEALTH_INSTANT_DATA_INDEX).getValue(),
                            (Float) instantData.get(OxygenMain.ABSORPTION_INSTANT_DATA_INDEX).getValue(),
                            (Integer) instantData.get(OxygenMain.TOTAL_ARMOR_INSTANT_DATA_INDEX).getValue(),
                            20,
                            effects,
                            groupData.isLeader(memberUUID),
                            OxygenHelperClient.isOfflineStatus(memberUUID)));
                }
            }
        }

        this.baseOverlayTextColor = EnumBaseGUISetting.OVERLAY_TEXT_BASE_COLOR.get().asInt();
        this.additionalOverlayTextColor = EnumBaseGUISetting.OVERLAY_TEXT_ADDITIONAL_COLOR.get().asInt();

        this.barBackgroundColor = EnumGroupsGUISetting.OVERAY_BAR_BACKGROUND_COLOR.get().asInt();
        this.barOfflineColor = EnumGroupsGUISetting.OVERAY_OFFLINE_BAR_COLOR.get().asInt();
        this.healthBarColor = EnumGroupsGUISetting.OVERAY_HEALTH_BAR_COLOR.get().asInt();
        this.absorptionBarColor = EnumGroupsGUISetting.OVERAY_ABSORPTION_BAR_COLOR.get().asInt();
        this.armorBarColor = EnumGroupsGUISetting.OVERAY_ARMOR_BAR_COLOR.get().asInt();

        this.offlineStr = ClientReference.localize("oxygen_core.status.offline");
        this.playersPerSquad = GroupsConfig.PLAYERS_PER_SQUAD.asInt();
    }

    @Override
    public void draw(float partialTicks) {
        GlStateManager.pushMatrix();           
        GlStateManager.translate(this.x, this.y, 0.0F);          
        GlStateManager.scale(this.scale, this.scale, 0.0F);  

        int 
        x, y, healthBarWidth, absorptionBarWidth, armorBarWidth,
        index = 0, 
        xOffsetsCount = 0;
        for (GroupMemberOverlayEntry entry : this.members) {
            x = xOffsetsCount * 90;
            y = index * (this.showEffects ? 38 : 26);

            this.mc.fontRenderer.drawStringWithShadow(entry.username, x, y, this.baseOverlayTextColor);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            if (entry.leader) {
                this.mc.getTextureManager().bindTexture(GroupsGUITextures.LEADER_MARK);
                GUIAdvancedElement.drawCustomSizedTexturedRect(x + ClientReference.getMinecraft().fontRenderer.getStringWidth(entry.username) + 4, y, 0, 0, 8, 8, 8, 8);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            if (!entry.offline) {
                healthBarWidth = (int) (entry.health / entry.maxHealth * 100.0F);
                OxygenGUIUtils.drawRect(x + 1, y + 12, x + 81, y + 19, this.barBackgroundColor);
                OxygenGUIUtils.drawRect(x, y + 11, x + healthBarWidth * 0.8F, y + 18, this.healthBarColor);

                absorptionBarWidth = (int) (entry.absorption / entry.maxHealth * 100.0F);
                OxygenGUIUtils.drawRect(x, y + 11, x + absorptionBarWidth * 0.8F, y + 18, this.absorptionBarColor);

                armorBarWidth = (int) ((float) entry.armor / (float) entry.maxArmor * 100.0F);
                OxygenGUIUtils.drawRect(x, y + 11, x + armorBarWidth * 0.8D, y + 11.8D, this.armorBarColor);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.7F, 0.7F, 0.0F);  
                this.mc.fontRenderer.drawStringWithShadow(entry.barText, 0.0F, 0.0F, this.additionalOverlayTextColor);
                GlStateManager.popMatrix();

                if (this.showEffects) {
                    GlStateManager.pushMatrix();           
                    GlStateManager.translate(x, y + 21.0F, 0.0F);     
                    GlStateManager.scale(0.5F, 0.5F, 0.0F);  

                    Potion potion;
                    int i = 0, iconX = 0, iconIndex;
                    this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
                    for (PotionEffect effect : entry.effects) {
                        if (i > 5) break;

                        potion = effect.getPotion(); 
                        if (!potion.shouldRender(effect)) continue;

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                        this.mc.ingameGUI.drawTexturedModalRect(iconX, 0, 141, 166, 24, 24);
                        if (potion.hasStatusIcon()) {
                            iconIndex = potion.getStatusIconIndex();
                            this.mc.ingameGUI.drawTexturedModalRect(iconX + 3, 3, 0 + iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);
                        }

                        iconX += 27;
                        i++;
                    }

                    GlStateManager.popMatrix();
                }
            } else {
                OxygenGUIUtils.drawRect(x + 1, y + 12, x + 81, y + 19, this.barBackgroundColor);
                OxygenGUIUtils.drawRect(x, y + 11, x + 80, y + 18, this.barOfflineColor);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.7F, 0.7F, 0.0F);  
                this.mc.fontRenderer.drawStringWithShadow(this.offlineStr, 0.0F, 0.0F, this.additionalOverlayTextColor);
                GlStateManager.popMatrix();
            }

            index++;
            if (index == this.playersPerSquad) {
                xOffsetsCount++;
                index = 0;
            }
        }

        GlStateManager.popMatrix();
    }

    static class GroupMemberOverlayEntry implements Comparable<GroupMemberOverlayEntry> {

        final String username, barText;

        final float health, maxHealth, absorption;

        final int armor, maxArmor;

        final boolean leader, offline;

        final Collection<PotionEffect> effects = new ArrayList<>(6);

        GroupMemberOverlayEntry(String username, float health, float maxHealth, float absorption, int armor, int maxArmor, Collection<PotionEffect> effects, boolean leader, boolean offline) {
            this.username = username;
            this.health = health > maxHealth ? maxHealth : health;
            this.maxHealth = maxHealth;
            this.absorption = absorption > maxHealth ? maxHealth : absorption;
            this.armor = armor > maxArmor ? maxArmor : armor;
            this.maxArmor = maxArmor;
            this.effects.addAll(effects);
            this.leader = leader;
            this.offline = offline;

            EnumStatusBarText enumText = EnumStatusBarText.values()[EnumGroupsGUISetting.OVERLAY_STATUS_BAR_TEXT.get().asInt()];
            switch (enumText) {
            case NONE:
                this.barText = "";
                break;
            case HEALTH_AND_MAX_HEALTH:
                this.barText = String.valueOf((int) health) + "/" + String.valueOf((int) maxHealth);
                break;
            case HEALTH_AND_MAX_HEALTH_PLUS_ABSORPTION:
                this.barText = String.valueOf((int) health) + "/" + String.valueOf((int) maxHealth) + (absorption >= 1.0F ? "+" + String.valueOf((int) absorption) : "");
                break;
            case HEALTH_PERCENT:
                this.barText = String.valueOf((int) (health / maxHealth * 100.0F)) + "%";
                break;
            case HEALTH_PERCENT_PLUS_ABSORPTION:
                this.barText = String.valueOf((int) (health / maxHealth * 100.0F)) + "%" + (absorption >= 1.0F ? "+" + String.valueOf((int) absorption) : "");
                break;
            default:
                this.barText = "";
                break;
            }
        }

        @Override
        public int compareTo(GroupMemberOverlayEntry other) {
            return this.username.compareTo(other.username);
        }
    }
}
