package com.andreid278.cmc.client.gui.viewer;

import com.andreid278.cmc.utils.MathUtils;
import com.andreid278.cmc.utils.MathUtils.Box3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerObject extends MovableObject {
	public Box3f bBox = MathUtils.instance.new Box3f();
	
	public PlayerObject() {
		float height = Minecraft.getMinecraft().player.height;
		bBox.addPoint(MathUtils.instance.new Vec3f(-height * 0.5f, -height * 0.5f, -height * 0.5f));
		bBox.addPoint(MathUtils.instance.new Vec3f(height * 0.5f, height * 0.5f, height * 0.5f));
	}

	@Override
	public void draw() {
		GlStateManager.pushMatrix();
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		float f = player.renderYawOffset;
		float f1 = player.rotationYaw;
		float f2 = player.rotationPitch;
		float f3 = player.prevRotationYawHead;
		float f4 = player.rotationYawHead;
		
		player.renderYawOffset = 0.0f;
		player.rotationYaw = 0.0f;
		player.rotationPitch = 0.0f;
		player.rotationYawHead = 0.0f;
		player.prevRotationYawHead = 0.0f;
		
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0, -Minecraft.getMinecraft().player.height * 0.5f, 0);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(Minecraft.getMinecraft().player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		
		player.renderYawOffset = f;
		player.rotationYaw = f1;
		player.rotationPitch = f2;
		player.prevRotationYawHead = f3;
		player.rotationYawHead = f4;
		
		GlStateManager.popMatrix();
	}

	@Override
	public Box3f BoundingBox() {
		return bBox;
	}

}
