package austeretony.oxygen_groups.client.gui.group;

import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.PlayerGUIButton;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.api.EnumDimension;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.GroupsGUITextures;
import net.minecraft.client.renderer.GlStateManager;

public class GroupEntryGUIButton extends PlayerGUIButton {

    private String dimension, lastActivity;

    private int statusIconU;

    private GUIImageLabel statusImageLabel;

    private boolean isLeader;   

    public GroupEntryGUIButton(SharedPlayerData sharedData, OxygenPlayerData.EnumActivityStatus status) {
        super(sharedData.getPlayerUUID());
        this.isLeader = GroupsManagerClient.instance().getGroupData().isLeader(sharedData.getPlayerUUID());
        this.dimension = EnumDimension.getLocalizedNameFromId(OxygenHelperClient.getPlayerDimension(sharedData));
        this.setDisplayText(sharedData.getUsername());//need for search mechanic
        this.statusIconU = status.ordinal() * 3;
        if (status == OxygenPlayerData.EnumActivityStatus.OFFLINE)
            this.lastActivity = OxygenUtils.getLastActivityTimeLocalizedString(sharedData.getLastActivityTime());
    }

    @Override
    public void init() {
        this.statusImageLabel = new GUIImageLabel(7, 3, 3, 3).setTexture(OxygenGUITextures.STATUS_ICONS, 3, 3, this.statusIconU, 0, 12, 3).initScreen(this.getScreen());
        this.statusImageLabel.setTextureUV(this.statusIconU, 0);
        if (this.lastActivity != null)
            this.statusImageLabel.initSimpleTooltip(this.lastActivity, GUISettings.instance().getTooltipScale());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {  
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);
            int color, textColor, textY;                      
            if (!this.isEnabled()) {                 
                color = this.getDisabledBackgroundColor();
                textColor = this.getDisabledTextColor();           
            } else if (this.isHovered() || this.isToggled()) {                 
                color = this.getHoveredBackgroundColor();
                textColor = this.getHoveredTextColor();
            } else {                   
                color = this.getEnabledBackgroundColor(); 
                textColor = this.getEnabledTextColor();      
            }
            drawRect(0, 0, this.getWidth(), this.getHeight(), color);
            textY = (this.getHeight() - this.textHeight(this.getTextScale())) / 2 + 1;
            GlStateManager.pushMatrix();           
            GlStateManager.translate(24.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F); 
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();    
            GlStateManager.translate(100.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F); 
            this.mc.fontRenderer.drawString(this.dimension, 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();
            this.statusImageLabel.draw(mouseX, mouseY);
            if (this.isLeader) {
                this.mc.getTextureManager().bindTexture(GroupsGUITextures.LEADER_MARK); 
                drawCustomSizedTexturedRect(this.getWidth() - 15, 3, 0, 0, 6, 6, 6, 6);  
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        this.statusImageLabel.drawTooltip(mouseX, mouseY);
    }

    @Override
    public void mouseOver(int mouseX, int mouseY) {
        this.statusImageLabel.mouseOver(mouseX - this.getX(), mouseY - this.getY());
        this.setHovered(this.isEnabled() && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + (int) (this.getWidth() * this.getScale()) && mouseY < this.getY() + (int) (this.getHeight() * this.getScale()));   
    }
}
