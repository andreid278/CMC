package com.andreid278.cmc.client.gui;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.client.model.CMCModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
	public float angleX = 0;
	public float angleY = 0;
	Quaternion rotation = new Quaternion();
	
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
		curMouseX = mouseX;
		curMouseY = mouseY;
		
		//GlStateManager.viewport(x, y, w, h);
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableTexture2D();
		
		GuiUtils.drawFilledRectangle(x, y, x + w, y + h, 0, 0, 0);
		
		if(model != null) {
			GlStateManager.pushMatrix();
			float scale = 30;
			GlStateManager.translate(x + w / 2, y + h / 2, model.getSize() * scale);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(rotation);
			GlStateManager.translate(-model.getSizeX() * 0.5f, -model.getSizeY() * 0.5f, -model.getSizeZ() * 0.5f);
			model.draw();
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableTexture2D();
		
		GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
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
			Quaternion.mul(rot, rotation, rotation);
			rot.setFromAxisAngle(new Vector4f(1, 0, 0, -angleY));
			Quaternion.mul(rot, rotation, rotation);
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
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}
}
