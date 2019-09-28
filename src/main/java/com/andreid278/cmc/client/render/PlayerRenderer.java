package com.andreid278.cmc.client.render;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.network.DataLoadingHelper;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderer {
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		if(!CMCData.instance.playersModels.containsKey(event.getEntityPlayer().getUniqueID())) {
			System.out.println("Added layer for player " + event.getEntityPlayer().getName());
			event.getRenderer().addLayer(new CMCRenderLayer(event.getRenderer()));
			CMCData.instance.playersModels.put(event.getEntityPlayer().getUniqueID(), null);
		}
		
		List<CMCModelOnPlayer> modelsList = CMCData.instance.playersModels.get(event.getEntityPlayer().getUniqueID());
		
		if(modelsList == null) {
			DataLoadingHelper.requestPlayerModels(event.getEntityPlayer().getUniqueID());
			return;
		}
		
		if(modelsList.isEmpty()) {
			return;
		}
		
		/*for(CMCModelOnPlayer modelOnPlayer : modelsList) {
			if(ModelStorage.instance.hasModel(modelOnPlayer.uuid)) {
				ModelPlayer modelPlayer = event.getRenderer().getMainModel();
				
				GlStateManager.pushMatrix();
				
				GlStateManager.translate(0, event.getEntityPlayer().height * 0.5, 0);
				
				GlStateManager.rotate(event.getEntityPlayer().rotationPitch, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(event.getEntityPlayer().rotationYaw, 0.0F, -1.0F, 0.0F);
				
				switch(modelOnPlayer.bodyPart) {
				case Torso:
					moveToTorso(modelPlayer, modelOnPlayer.location);
					break;
				}
				
				modelOnPlayer.locationFloatBuffer.rewind();
				GlStateManager.multMatrix(modelOnPlayer.locationFloatBuffer);
				
				ModelStorage.instance.getModel(modelOnPlayer.uuid).draw();
				
				GlStateManager.popMatrix();
			}
			else {
				DataLoadingHelper.requestDataFromServer(modelOnPlayer.uuid);
			}
		}*/
	}
	
	/*public void moveToTorso(ModelPlayer modelPlayer, Matrix4f loc) {
		ModelRenderer modelRenderer = modelPlayer.bipedBody;
		
		float scale = 0.0625f;
		Vec3f pos = new Vec3f(loc.m30, loc.m31, loc.m32);
		float toX = modelRenderer.rotationPointX * scale - loc.m30;
		float toY = modelRenderer.rotationPointY * scale - loc.m31;
		float toZ = modelRenderer.rotationPointZ * scale - loc.m32;
		
		GlStateManager.translate(toX, toY, toZ);
		
		if (modelRenderer.rotateAngleZ != 0.0F)
        {
            GlStateManager.rotate(modelRenderer.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
        }

        if (modelRenderer.rotateAngleY != 0.0F)
        {
            GlStateManager.rotate(modelRenderer.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (modelRenderer.rotateAngleX != 0.0F)
        {
            GlStateManager.rotate(modelRenderer.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
        }
        
        GlStateManager.translate(-toX, -toY, -toZ);
	}*/
}
