package com.andreid278.cmc.client.render;

import java.util.List;

import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderer {
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		if(!CMCData.instance.playersModels.containsKey(event.getEntityPlayer().getUniqueID())) {
			return;
		}
		
		List<CMCModelOnPlayer> modelsList = CMCData.instance.playersModels.get(event.getEntityPlayer().getUniqueID());
		
		if(modelsList == null || modelsList.isEmpty()) {
			return;
		}
		
		for(CMCModelOnPlayer modelOnPlayer : modelsList) {
			GlStateManager.pushMatrix();
			modelOnPlayer.locationFloatBuffer.rewind();
			GlStateManager.multMatrix(modelOnPlayer.locationFloatBuffer);
			modelOnPlayer.model.draw();
			GlStateManager.popMatrix();
		}
	}
}
