package austeretony.oxygen_groups.client.gui.overlay;

import java.util.Set;
import java.util.TreeSet;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.overlay.Overlay;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_groups.client.GroupDataClient;
import austeretony.oxygen_groups.client.GroupEntryClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.GroupsGUITextures;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraft.client.renderer.GlStateManager;

public class GroupOverlay implements Overlay {

    @Override
    public boolean shouldDraw() {
        return GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() 
                && !OxygenHelperClient.getClientSettingBoolean(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID);
    }

    @Override
    public boolean drawWhileInGUI() {
        return true;
    }

    @Override
    public void draw(float partialTicks) {
        GroupDataClient group = GroupsManagerClient.instance().getGroupDataManager().getGroupData();
        float scale = 1.0F;
        switch (group.getMode()) {//display up to 24 entries may cause mess up on the screen, so it is scaled down if group too big
        case SQUAD:
            scale = GUISettings.get().getOverlayScale();
            break;
        case RAID:
            scale = GUISettings.get().getOverlayScale() * 0.8F;
            break;
        case PARTY:
            scale = GUISettings.get().getOverlayScale() * 0.6F;
            break;
        }
        GlStateManager.pushMatrix();           
        GlStateManager.translate(10.0F, 40.0F, 0.0F);//top left corner           
        GlStateManager.scale(scale, scale, 0.0F);  
        Set<GroupEntryClient> ordered = new TreeSet<GroupEntryClient>(group.getPlayersData());
        int 
        x, y, healthPercents,
        index = 0, 
        xOffsetsCount = 0;
        for (GroupEntryClient data : ordered) {
            x = xOffsetsCount * 90;
            y = index * 28;
            ClientReference.getMinecraft().fontRenderer.drawStringWithShadow(data.username, x, y, GUISettings.get().getBaseOverlayTextColor());
            if (group.isLeader(data.playerUUID)) {
                ClientReference.getMinecraft().getTextureManager().bindTexture(GroupsGUITextures.LEADER_MARK);
                GUIAdvancedElement.drawCustomSizedTexturedRect(x + ClientReference.getMinecraft().fontRenderer.getStringWidth(data.username) + 4, y, 0, 0, 8, 8, 8, 8);
            }
            if (OxygenHelperClient.isPlayerOnline(data.playerUUID) 
                    && !OxygenHelperClient.isOfflineStatus(data.playerUUID)) {
                healthPercents = (int) (data.getHealth() / data.getMaxHealth() * 100.0F);
                GUISimpleElement.drawRect(x + 1, y + 12, x + 81, y + 19, 0xFF303030);
                GUISimpleElement.drawRect(x, y + 11, x + (int) (healthPercents * 0.8F), y + 18, 0xFF990000);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.8F, 0.8F, 0.0F);  
                ClientReference.getMinecraft().fontRenderer.drawStringWithShadow(healthPercents + "%", 0.0F, 0.0F, GUISettings.get().getAdditionalOverlayTextColor());
                GlStateManager.popMatrix();

            } else {
                GUISimpleElement.drawRect(x + 1, y + 12, x + 81, y + 19, 0xFF303030);
                GUISimpleElement.drawRect(x, y + 11, x + 80, y + 18, 0xFF606060);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.8F, 0.8F, 0.0F);  
                ClientReference.getMinecraft().fontRenderer.drawStringWithShadow(ClientReference.localize("oxygen.status.offline"), 0.0F, 0.0F, GUISettings.get().getAdditionalOverlayTextColor());
                GlStateManager.popMatrix();
            }
            index++;
            if (index == GroupsConfig.PLAYERS_PER_SQUAD.getIntValue()) {
                xOffsetsCount++;
                index = 0;
            }
        }
        GlStateManager.popMatrix();
    }
}
