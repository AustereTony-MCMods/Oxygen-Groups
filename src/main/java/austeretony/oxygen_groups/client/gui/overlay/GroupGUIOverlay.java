package austeretony.oxygen_groups.client.gui.overlay;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_groups.client.GroupDataClient;
import austeretony.oxygen_groups.client.GroupEntryClient;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.GroupsGUITextures;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.main.GroupsMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupGUIOverlay {

    private Minecraft mc = ClientReference.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.TEXT)
            this.drawOverlay();
    }   

    @SubscribeEvent
    public void onRenderPlayer(RenderLivingEvent.Specials.Post event) {
        if (event.getEntity() instanceof EntityPlayer) 
            this.drawGroupMark((EntityPlayer) event.getEntity(), event.getRenderer(), event.getX(), event.getY(), event.getZ());
    }

    private void drawOverlay() {
        if (!GroupsManagerClient.instance().haveGroup() || OxygenHelperClient.getClientSettingBoolean(GroupsMain.HIDE_GROUP_OVERLAY_SETTING_ID)) 
            return;        
        GroupDataClient group = GroupsManagerClient.instance().getGroupData();
        float scale = 1.0F;
        switch (group.getMode()) {//display up to 24 entries may cause mess up on the screen, so it is scaled down if group too big
        case SQUAD:
            scale = GUISettings.instance().getOverlayScale();
            break;
        case RAID:
            scale = GUISettings.instance().getOverlayScale() * 0.8F;
            break;
        case PARTY:
            scale = GUISettings.instance().getOverlayScale() * 0.6F;
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
            this.mc.fontRenderer.drawStringWithShadow(data.username, x, y, GUISettings.instance().getBaseOverlayTextColor());
            if (group.isLeader(data.playerUUID)) {
                this.mc.getTextureManager().bindTexture(GroupsGUITextures.LEADER_MARK);
                GUIAdvancedElement.drawCustomSizedTexturedRect(x + this.mc.fontRenderer.getStringWidth(data.username) + 4, y, 0, 0, 8, 8, 8, 8);
            }
            if (OxygenHelperClient.isOnline(data.playerUUID) 
                    && !OxygenHelperClient.isOfflineStatus(data.playerUUID)) {
                healthPercents = this.toPercents(data.getHealth(), data.getMaxHealth());
                GUISimpleElement.drawRect(x + 1, y + 12, x + 81, y + 19, 0xFF303030);
                GUISimpleElement.drawRect(x, y + 11, x + (int) (healthPercents * 0.8F), y + 18, 0xFF990000);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.8F, 0.8F, 0.0F);  
                this.mc.fontRenderer.drawStringWithShadow(healthPercents + "%", 0.0F, 0.0F, GUISettings.instance().getAdditionalOverlayTextColor());
                GlStateManager.popMatrix();

            } else {
                GUISimpleElement.drawRect(x + 1, y + 12, x + 81, y + 19, 0xFF303030);
                GUISimpleElement.drawRect(x, y + 11, x + 80, y + 18, 0xFF606060);

                GlStateManager.pushMatrix();           
                GlStateManager.translate(x + 1.0F, y + 12.0F, 0.0F);     
                GlStateManager.scale(0.8F, 0.8F, 0.0F);  
                this.mc.fontRenderer.drawStringWithShadow(I18n.format("oxygen.status.offline"), 0.0F, 0.0F, GUISettings.instance().getAdditionalOverlayTextColor());
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

    private int toPercents(float curr, float max) {
        return (int) (curr / max * 100.0F);
    }

    private void drawGroupMark(EntityPlayer target, RenderLivingBase render, double x, double y, double z) { 
        if (!GroupsManagerClient.instance().haveGroup() || target == render.getRenderManager().renderViewEntity || render.getRenderManager().options.hideGUI)
            return;
        double 
        distance = target.getDistanceSq(render.getRenderManager().renderViewEntity),
        viewDistance = target.isSneaking() ? RenderLivingBase.NAME_TAG_RANGE_SNEAK : RenderLivingBase.NAME_TAG_RANGE;
        UUID targetUUID = target.getGameProfile().getId();

        if (distance < viewDistance * viewDistance 
                && GroupsManagerClient.instance().getGroupData().getPlayersUUIDs().contains(targetUUID)) {//TODO debug
            boolean isLeader = GroupsManagerClient.instance().getGroupData().isLeader(targetUUID);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(render.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.025F, 0.025F, 0.025F);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            render.getRenderManager().renderEngine.bindTexture(isLeader ? GroupsGUITextures.LEADER_MARK : GroupsGUITextures.GROUP_MARK);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);    
            bufferbuilder.pos(- 3.0D, - 96.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(5.0D, - 96.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(5.0D, - 104.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(- 3.0D, - 104.0D, 0.0D).tex(0.0D, 0.0D).endVertex();    
            tessellator.draw();

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
