package com.andreid278.cmc.client.gui.viewer;

import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.IntersectionData;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class PlayerObject extends MovableObject {
	public Box3f bBox = new Box3f();
	
	public PlayerObject() {
		super();
		
		float height = Minecraft.getMinecraft().player.height;
		bBox.addPoint(new Vec3f(-height * 0.5f, -height * 0.5f, -height * 0.5f));
		bBox.addPoint(new Vec3f(height * 0.5f, height * 0.5f, height * 0.5f));
		
		isMovable = false;
	}

	@Override
	public void draw() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		Render<EntityPlayer> renderer = rendermanager.getEntityRenderObject(player);
		
		if(!(renderer instanceof RenderLivingBase<?>)) {
			return;
		}
		RenderLivingBase<?> entityRenderer = (RenderLivingBase<?>) renderer;
		if(!(entityRenderer instanceof RenderPlayer)) {
			return;
		}
		RenderPlayer playerRenderer = (RenderPlayer) entityRenderer;
		boolean isRiding = playerRenderer.getMainModel().isRiding;
		playerRenderer.getMainModel().isRiding = false;
		boolean isSneak = playerRenderer.getMainModel().isSneak;
		playerRenderer.getMainModel().isSneak = false;
		
		/*EnumHand activeHand = player.getActiveHand();
		ItemStack activeItemMainHand;
		ItemStack activeStackOffHand;
		if(activeHand == EnumHand.MAIN_HAND) {
			activeItemMainHand = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			activeStackOffHand = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
		}
		else {
			activeItemMainHand = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			activeStackOffHand = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}
		player.resetActiveHand();*/
		
		GlStateManager.enableTexture2D();
		GlStateManager.pushMatrix();
		
		float f = player.renderYawOffset;
		float f1 = player.rotationYaw;
		float f2 = player.rotationPitch;
		float f3 = player.prevRotationYawHead;
		float f4 = player.rotationYawHead;
		int f5 = player.ticksExisted;
		boolean f6 = player.isSneaking();
		
		player.renderYawOffset = 0.0f;
		player.rotationYaw = 0.0f;
		player.rotationPitch = 0.0f;
		player.rotationYawHead = 0.0f;
		player.prevRotationYawHead = 0.0f;
		player.ticksExisted = -1;
		
		rendermanager.setPlayerViewY(180.0F);
		//GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		//GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0, -player.height * 0.5f, 0);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		
		player.renderYawOffset = f;
		player.rotationYaw = f1;
		player.rotationPitch = f2;
		player.prevRotationYawHead = f3;
		player.rotationYawHead = f4;
		player.ticksExisted = f5;
		
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
		
		playerRenderer.getMainModel().isRiding = isRiding;
		playerRenderer.getMainModel().isSneak = isSneak;
		
		/*if(activeHand == EnumHand.MAIN_HAND) {
			player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, activeItemMainHand);
			player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, activeStackOffHand);
		}
		else {
			player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, activeItemMainHand);
			player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, activeStackOffHand);
		}
		player.setActiveHand(activeHand);*/
	}

	@Override
	public Box3f BoundingBox() {
		return bBox;
	}
	
	@Override
	protected float intersectWithLocalRay(Ray3f ray, IntersectionData intersectionData) {
		return Float.MAX_VALUE;
	}

}
