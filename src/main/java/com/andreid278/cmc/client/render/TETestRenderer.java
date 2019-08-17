package com.andreid278.cmc.client.render;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.common.tileentity.TETest;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.model.animation.FastTESR;

public class TETestRenderer extends TileEntitySpecialRenderer<TETest> {

	@Override
	public void render(TETest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(te.uuid != null) {
			CMCModel model = ModelStorage.instance.getModel(te.uuid);
			
			GlStateManager.pushMatrix();
			this.setLightmapDisabled(true);
			GlStateManager.translate(x, y, z);
			model.draw();
			this.setLightmapDisabled(false);
			GlStateManager.popMatrix();
		}
	}

}
