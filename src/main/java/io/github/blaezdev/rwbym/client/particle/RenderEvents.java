package io.github.blaezdev.rwbym.client.particle;

import io.github.blaezdev.rwbym.Init.RegUtil;
import io.github.blaezdev.rwbym.RWBYModels;
import io.github.blaezdev.rwbym.capabilities.Aura.AuraProvider;
import io.github.blaezdev.rwbym.capabilities.Aura.IAura;
import io.github.blaezdev.rwbym.capabilities.CapabilityHandler;
import io.github.blaezdev.rwbym.capabilities.ISemblance;
import io.github.blaezdev.rwbym.utility.RWBYConfig;
import io.github.blaezdev.rwbym.weaponry.ArmourBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEvents {
    public RenderEvents() {
    }

    public static void init() {
    }

    @SubscribeEvent
    public void renderPlayerPostEvent(net.minecraftforge.client.event.RenderPlayerEvent.Post e) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (e.getEntityPlayer() != null && e.getRenderer().getMainModel() instanceof ModelPlayer) {
            ModelPlayer model = e.getRenderer().getMainModel();
            Item legs = RegUtil.getLegSlot(player);
            Item body = RegUtil.getBodySlot(player);
            if (body instanceof ArmourBase) {
                model.bipedLeftArmwear.isHidden = model.bipedRightArmwear.isHidden = model.bipedBodyWear.isHidden = true;
            } else {
                model.bipedLeftArmwear.isHidden = model.bipedRightArmwear.isHidden = model.bipedBodyWear.isHidden = false;
            }

            if (legs instanceof ArmourBase) {
                model.bipedLeftLegwear.isHidden = model.bipedRightLegwear.isHidden = true;
            } else {
                model.bipedLeftLegwear.isHidden = model.bipedRightLegwear.isHidden = false;
            }
        }
    }
    
    @SubscribeEvent
    public void RenderGameOverlay(RenderGameOverlayEvent.Pre event) {
    	
    	if (event.getType() == ElementType.ALL & RWBYConfig.aura.RenderAurabar) {
    		Minecraft mc = Minecraft.getMinecraft();
    		
    		EntityPlayer player = mc.player;
    		if (!player.capabilities.isCreativeMode && player.hasCapability(AuraProvider.AURA_CAP, null)) {
    			
    			IAura aura = player.getCapability(AuraProvider.AURA_CAP, null);
    			
    			int width = (int) (aura.getPercentage() * 79F);
    			
	    		GlStateManager.enableAlpha();
	    		GlStateManager.enableBlend();
		    	
		    	int posx = event.getResolution().getScaledWidth() / 2 - RWBYConfig.aura.aurapositionx;
		    	int posy = event.getResolution().getScaledHeight() - RWBYConfig.aura.aurapositiony;
		    	
		    	mc.renderEngine.bindTexture(new ResourceLocation(RWBYModels.MODID, "textures/overlay/aura.png"));
		    	
		    	mc.ingameGUI.drawTexturedModalRect(posx, posy, 0, 18, 81, 9);
		    	
		    	mc.ingameGUI.drawTexturedModalRect(posx + 1, posy + 1, 0, 10, width, 7);
		    	
		    	float color[] = new float[3];
		    	
		    	ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);
		    	
		    	if (semblance != null) {
		    		color = semblance.getColor();
		    		GlStateManager.color(color[0], color[1], color[2]);
		    	}
		    	
		    	mc.ingameGUI.drawTexturedModalRect(posx, posy, 0, 0, 81, 9);
		    	
		    	GlStateManager.color(1, 1, 1);
		    	
		    	GlStateManager.disableBlend();
		    	GlStateManager.disableAlpha();
    		}
	    	
    	}
    	
    }

}