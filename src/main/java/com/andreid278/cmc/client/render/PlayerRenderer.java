package com.andreid278.cmc.client.render;

import java.util.List;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.network.DataLoadingHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderer {
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		if(!CMCData.instance.playersModels.containsKey(event.getEntityPlayer().getUniqueID())) {
			DataLoadingHelper.requestPlayerModels(event.getEntityPlayer().getUniqueID());
			return;
		}
		
		List<CMCModelOnPlayer> modelsList = CMCData.instance.playersModels.get(event.getEntityPlayer().getUniqueID());
		
		if(modelsList == null || modelsList.isEmpty()) {
			return;
		}
		
		for(CMCModelOnPlayer modelOnPlayer : modelsList) {
			if(ModelStorage.instance.hasModel(modelOnPlayer.uuid)) {
				GlStateManager.pushMatrix();
				modelOnPlayer.locationFloatBuffer.rewind();
				GlStateManager.multMatrix(modelOnPlayer.locationFloatBuffer);
				ModelStorage.instance.getModel(modelOnPlayer.uuid).draw();
				GlStateManager.popMatrix();
			}
			else {
				DataLoadingHelper.requestDataFromServer(modelOnPlayer.uuid);
			}
		}
	}
}
