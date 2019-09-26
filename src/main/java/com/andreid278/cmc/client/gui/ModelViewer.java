package com.andreid278.cmc.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.client.gui.viewer.ModelObject;
import com.andreid278.cmc.client.gui.viewer.MovableObject;
import com.andreid278.cmc.client.gui.viewer.PlayerObject;
import com.andreid278.cmc.client.gui.viewer.TransformControl;
import com.andreid278.cmc.client.gui.viewer.ViewerCamera;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Ray3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.RenderManager;

public class ModelViewer extends Gui {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public List<MovableObject> objects = new ArrayList<>();
	Box3f globalBBox = new Box3f();
	boolean showPlayer = false;
	
	TransformControl transformControl = new TransformControl();
	public boolean canAttachTransformControl = false;
	
	public boolean isDragged = false;
	public int mouseStartX;
	public int mouseStartY;
	
	ViewerCamera camera;
	
	public int curMouseX;
	public int curMouseY;
	
	public ModelViewer(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		camera = new ViewerCamera(x, y, w, h);
	}
	
	public void setModel(CMCModel model) {
		globalBBox.reset();
		
		Iterator<MovableObject> it = objects.iterator();
		while(it.hasNext()) {
			if(it.next() instanceof ModelObject) {
				it.remove();
				break;
			}
		}
		if(model != null) {
			ModelObject modelObject = new ModelObject(model);
			objects.add(modelObject);
			
			if(canAttachTransformControl) {
				transformControl.attachObject(modelObject);
			}
		}
		
		calculateBBox();
		camera.resetAndFitCamera(globalBBox);
		
		//Quaternion rot = new Quaternion();
		//rot.setFromAxisAngle(new Vector4f(1, 0, 0, (float) -Math.PI * 0.5f));
		//Quaternion.mul(rot, cameraRotation, cameraRotation);
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
		
		calculateBBox();
		
		if(globalBBox.isValid) {
			GlStateManager.pushMatrix();
			float scale = camera.scaleConst * camera.cameraScale * globalBBox.getSize();
			GlStateManager.translate(x + w / 2, y + h / 2, scale);
			GlStateManager.translate(camera.cameraTranslation.x, camera.cameraTranslation.y, camera.cameraTranslation.z);
			GlStateManager.rotate(camera.cameraRotation);
			GlStateManager.scale(scale, scale, scale);
			
			Iterator<MovableObject> it = objects.iterator();
			while(it.hasNext()) {
				MovableObject object = it.next();
				object.draw();
			}
			
			transformControl.tolerance = 1.0f / camera.cameraScale;
			transformControl.draw();
		
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
			if(mouseButton == 0) {
				Ray3f ray = camera.project(mouseX, mouseY, globalBBox);
				System.out.println("(" + ray.origin.x + ", " + ray.origin.y + ", " + ray.origin.z + "), " + "(" + ray.direction.x + ", " + ray.direction.y + ", " + ray.direction.z + ")");
				
				if(transformControl.attachedObject != null) {
					if(transformControl.mouseClicked(mouseX, mouseY, mouseButton, ray)) {
						return;
					}
				}
			}
			
			isDragged = true;
			mouseStartX = mouseX;
			mouseStartY = mouseY;
		}
	}
	
	public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(!isMouseInside(mouseX, mouseY)) {
			return false;
		}
		
		if(isDragged) {
			if(clickedMouseButton == 0) {
				float angleX = 2 * (float) Math.PI * (mouseX - mouseStartX) / w;
				float angleY = 2 * (float) Math.PI * (mouseY - mouseStartY) / h;
				camera.rotate(0, 1, 0, angleX);
				camera.rotate(1, 0, 0, -angleY);
			}
			else if(clickedMouseButton == 2) {
				camera.translateOnScreenPlane(mouseX - mouseStartX, mouseY - mouseStartY);
			}
			
			mouseStartX = mouseX;
			mouseStartY = mouseY;
			
			return true;
		}
		else if(transformControl.isDragged) {
			transformControl.mouseClickMove(camera.project(mouseX, mouseY, globalBBox));
			
			mouseStartX = mouseX;
			mouseStartY = mouseY;
		}
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		isDragged = false;
		transformControl.mouseReleased(mouseX, mouseY, state);
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
		
		camera.scale(wheel);
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}
	
	private void calculateBBox() {
		if(globalBBox.isValid) {
			return;
		}
		
		Iterator<MovableObject> it = objects.iterator();
		while(it.hasNext()) {
			Box3f childBox = it.next().GlobalBoundingBox();
			if(childBox.isValid) {
				globalBBox.union(childBox);
			}
		}
	}
	
	public void showPlayer(boolean toShow) {
		if(showPlayer == toShow) {
			return;
		}
		
		if(toShow) {
			objects.add(new PlayerObject());
		}
		else {
			Iterator<MovableObject> it = objects.iterator();
			while(it.hasNext()) {
				if(it.next() instanceof PlayerObject) {
					it.remove();
					break;
				}
			}
		}
		
		globalBBox.reset();
		camera.resetAndFitCamera(globalBBox);
	}
	
	public void resetCamera() {
		camera.resetTransformation();
	}
}
