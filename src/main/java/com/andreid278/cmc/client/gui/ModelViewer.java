package com.andreid278.cmc.client.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.client.model.CMCModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class ModelViewer extends Gui {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public CMCModel model;
	
	public boolean isDragged = false;
	public float mouseStartX;
	public float mouseStartY;
	
	Quaternion cameraRotation = new Quaternion();
	float cameraScale = 1.0f;
	
	public int curMouseX;
	public int curMouseY;
	
	public ModelViewer(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setModel(CMCModel model) {
		this.model = model;
	}
	
	public void draw(Minecraft mc, int mouseX, int mouseY) {
		boolean scissor = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		
		GL11.glScissor(x * Minecraft.getMinecraft().displayWidth / i,
				(j - y - h) * Minecraft.getMinecraft().displayHeight / j,
				w * Minecraft.getMinecraft().displayWidth / i,
				h * Minecraft.getMinecraft().displayHeight / j);
		
		curMouseX = mouseX;
		curMouseY = mouseY;
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableTexture2D();
		
		GuiUtils.drawFilledRectangle(x, y, x + w, y + h, 0, 0, 0);
		
		if(model != null) {
			GlStateManager.pushMatrix();
			float scale = 15 * cameraScale * model.bBox.getSize();
			GlStateManager.translate(x + w / 2, y + h / 2, scale);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(cameraRotation);
			GlStateManager.translate(-model.bBox.getSizeX() * 0.5f, -model.bBox.getSizeY() * 0.5f, -model.bBox.getSizeZ() * 0.5f);
			model.draw();
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableTexture2D();
		
		GL11.glScissor(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		
		if(!scissor) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(isMouseInside(mouseX, mouseY)) {
			isDragged = true;
			mouseStartX = mouseX;
			mouseStartY = mouseY;
		}
	}
	
	public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(isDragged) {
			float angleX = (mouseX - mouseStartX) / w * 2 * (float) Math.PI;
			float angleY = (mouseY - mouseStartY) / h * 2 * (float) Math.PI;
			Quaternion rot = new Quaternion();
			rot.setFromAxisAngle(new Vector4f(0, 1, 0, angleX));
			Quaternion.mul(rot, cameraRotation, cameraRotation);
			rot.setFromAxisAngle(new Vector4f(1, 0, 0, -angleY));
			Quaternion.mul(rot, cameraRotation, cameraRotation);
			mouseStartX = mouseX;
			mouseStartY = mouseY;
			return true;
		}
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		isDragged = false;
	}
	
	public void handleMouseInput() {
		if(!isMouseInside(curMouseX, curMouseY)) {
			return;
		}
		
		int wheel = Mouse.getEventDWheel();
		if (wheel > 0) {
			wheel = 1;
		}
		else if (wheel < 0) {
			wheel = -1;
		}
		float min = 0.1f;
		float max = 10.0f;
		float step = 0.05f;
		cameraScale += wheel * step;
		if(cameraScale < min) cameraScale = min;
		if(cameraScale > max) cameraScale = max;
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}
	
	public void resetTransformation() {
		cameraRotation.setIdentity();
		cameraScale = 1.0f;
	}
}
