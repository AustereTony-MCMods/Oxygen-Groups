package austeretony.oxygen_groups.client.gui.overlay;

import java.util.UUID;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.GroupsGUITextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupMarkRenderer {

    @SubscribeEvent
    public void onRenderPlayer(RenderLivingEvent.Specials.Post event) {
        if (event.getEntity() instanceof EntityPlayer) 
            this.drawGroupMark((EntityPlayer) event.getEntity(), event.getRenderer(), event.getX(), event.getY(), event.getZ());
    }

    private void drawGroupMark(EntityPlayer target, RenderLivingBase render, double x, double y, double z) { 
        if (!GroupsManagerClient.instance().getGroupDataManager().getGroupData().isActive() 
                || target == render.getRenderManager().renderViewEntity 
                || render.getRenderManager().options.hideGUI)
            return;
        double 
        distance = target.getDistanceSq(render.getRenderManager().renderViewEntity),
        viewDistance = target.isSneaking() ? RenderLivingBase.NAME_TAG_RANGE_SNEAK : RenderLivingBase.NAME_TAG_RANGE;
        UUID targetUUID = ClientReference.getPersistentUUID(target);

        if (distance < viewDistance * viewDistance 
                && GroupsManagerClient.instance().getGroupDataManager().getGroupData().getPlayersUUIDs().contains(targetUUID)) {

            boolean isLeader = GroupsManagerClient.instance().getGroupDataManager().getGroupData().isLeader(targetUUID);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(render.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.025F, 0.025F, 0.025F);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

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
            GlStateManager.popMatrix();
        }
    }
}
