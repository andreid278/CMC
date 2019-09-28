package com.andreid278.cmc.client.render;

import java.util.List;
import java.util.UUID;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.network.DataLoadingHelper;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class CMCRenderLayer implements LayerRenderer<EntityLivingBase> {
	
	private final RenderPlayer renderer;
	
	public CMCRenderLayer(RenderPlayer rendererIn) {
		this.renderer = rendererIn;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EntityPlayer player = (EntityPlayer) entitylivingbaseIn;
		UUID playerUUID = entitylivingbaseIn.getUniqueID();
		
		if(!CMCData.instance.playersModels.containsKey(playerUUID)) {
			System.out.println("CMCLayer error");
		}
		
		List<CMCModelOnPlayer> modelsList = CMCData.instance.playersModels.get(playerUUID);
		
		if(modelsList == null) {
			return;
		}
		
		for(CMCModelOnPlayer modelOnPlayer : modelsList) {
			if(ModelStorage.instance.hasModel(modelOnPlayer.uuid)) {
				ModelPlayer modelPlayer = renderer.getMainModel();
				
				GlStateManager.pushMatrix();
				
				//GlStateManager.rotate(event.getEntityPlayer().rotationPitch, 1.0F, 0.0F, 0.0F);
                //GlStateManager.rotate(event.getEntityPlayer().rotationYaw, 0.0F, -1.0F, 0.0F);
				
				switch(modelOnPlayer.bodyPart) {
				case Torso:
					moveTo(modelPlayer.bipedBody, scale, false, false);
					break;
				case Head:
					moveTo(modelPlayer.bipedHead, scale, false, false);
					break;
				case LeftArm:
					moveTo(modelPlayer.bipedLeftArm, scale, true, false);
					break;
				case RightArm:
					moveTo(modelPlayer.bipedRightArm, scale, true, true);
					break;
				case LeftLeg:
					moveTo(modelPlayer.bipedLeftLeg, scale, false, false);
					break;
				case RightLeg:
					moveTo(modelPlayer.bipedRightLeg, scale, false, false);
					break;
				}
				
				GlStateManager.translate(0.0F, 1.501F, 0.0F);
				
				GlStateManager.scale(1.0F, -1.0F, -1.0F);
				
				float f = 1.0f / 0.9375f;
				GlStateManager.scale(f, f, f);
				
				GlStateManager.translate(0, player.height * 0.5, 0);
				
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

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	public void moveTo(ModelRenderer modelRenderer, float scale, boolean isArm, boolean isMirror) {
		float toX = modelRenderer.rotationPointX * scale;// - loc.m30;
		float toY = modelRenderer.rotationPointY * scale;// - loc.m31;
		float toZ = modelRenderer.rotationPointZ * scale;// - loc.m32;
		
		GlStateManager.translate(toX, toY, toZ);
		
		if (modelRenderer.rotateAngleZ != 0.0F)
        {
			float z = 0.0f;
			if(isArm) {
				z = 0.1f;
				if(isMirror) {
					z *= -1.0f;
				}
			}
            GlStateManager.rotate((modelRenderer.rotateAngleZ + z) * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
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
	}
	
}
