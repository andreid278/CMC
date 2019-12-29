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
import com.andreid278.cmc.utils.IntersectionData;
import com.andreid278.cmc.utils.Plane;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

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
	ModelObject modelObject = null;
	PlayerObject playerObject = null;
	Box3f globalBBox = new Box3f();
	boolean showPlayer = false;
	
	TransformControl transformControl = new TransformControl();
	public boolean canAttachTransformControl = false;
	
	public boolean isDragged = false;
	public boolean wasDragged = false;
	public int mouseStartX;
	public int mouseStartY;
	
	ViewerCamera camera;
	
	public int curMouseX;
	public int curMouseY;
	
	MovableObject intersectionResult = null;
	Vec3f intersectionPoint = null;
	Vec3f rotationPoint = new Vec3f();
	Vec3f rotationPointToShow = new Vec3f();
	
	boolean paintingMode = false;
	float brushSize = 0.01f;
	int color = 0xffffffff;
	
	public ModelViewer(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		camera = new ViewerCamera(x, y, w, h, globalBBox);
		
		modelObject = new ModelObject(null);
		playerObject = new PlayerObject();
	}
	
	public void setModel(CMCModel model) {
		modelObject = new ModelObject(model);
		if(canAttachTransformControl) {
			transformControl.attachObject(modelObject);
		}
		
		globalBBox.reset();
		calculateBBox();
		camera.setSceneBox(globalBBox);
	}
	
	public void addObject(CMCModel model) {
		objects.add(new ModelObject(model));
		
		globalBBox.reset();
		calculateBBox();
		camera.setSceneBox(globalBBox);
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
		
		GuiUtils.drawFilledRectangle(x, y, x + w, y + h, 20, 20, 20);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		calculateBBox();
		
		if(globalBBox.isValid) {
			GlStateManager.pushMatrix();
			
			GlStateManager.multMatrix(camera.getOrientationMatrixBuffer());
			
			modelObject.draw();
			
			if(showPlayer) {
				playerObject.draw();
			}
			
			Iterator<MovableObject> it = objects.iterator();
			while(it.hasNext()) {
				MovableObject object = it.next();
				object.draw();
			}
			
			transformControl.tolerance = 1.0f / camera.getScale();
			transformControl.draw(camera);
			
			if(isDragged)
			{
				GlStateManager.disableDepth();
				Vec3f camDir = camera.getDir();
				float camScale = 1.0f / camera.getScale();
				GuiUtils.drawCircle(rotationPointToShow, camDir, camScale / 50, 32, 255, 255, 255);
				GuiUtils.drawCircle(rotationPointToShow, camDir, camScale / 100, 32, 0, 0, 0);
				GlStateManager.enableDepth();
			}
		
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableTexture2D();
		
		GL11.glScissor(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		
		if(!scissor) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		intersectionResult = null;
		if(isMouseInside(mouseX, mouseY)) {
			wasDragged = false;
			if(mouseButton == 0) {
				IntersectionData intersectionData = new IntersectionData();
				
				Ray3f ray = camera.project(mouseX, mouseY);
				System.out.println("(" + ray.origin.x + ", " + ray.origin.y + ", " + ray.origin.z + "), " + "(" + ray.direction.x + ", " + ray.direction.y + ", " + ray.direction.z + ")");
				
				if(transformControl.attachedObject != null) {
					if(transformControl.mouseClicked(mouseX, mouseY, mouseButton, ray)) {
						return;
					}
				}
				
				if(canAttachTransformControl || paintingMode) {
					intersectionData = checkIntersection(ray, true);
				}
			
				if(paintingMode) {
					if(intersectionData.material != null) {
						intersectionData.material.paint(intersectionData.point, brushSize, color);
					}
					return;
				}
			}
			else {
				Ray3f ray = camera.project(mouseX, mouseY);
				IntersectionData intersectionData = checkIntersection(ray, false);
				if(intersectionData.point != null) {
					rotationPointToShow.set(intersectionData.point);
					rotationPoint.copy(rotationPointToShow).applyMatrix(camera.getLocalOrientationMatrix());
				}
				else {
					Plane plane = new Plane(camera.getDir(), rotationPointToShow);
					float dist = ray.intersectPlane(plane);
					if(dist != Float.MAX_VALUE) {
						rotationPointToShow.copy(ray.direction).mul(dist).add(ray.origin);
						rotationPoint.copy(rotationPointToShow).applyMatrix(camera.getLocalOrientationMatrix());
					}
					else {
						rotationPointToShow.set(0.0f, 0.0f, 0.0f);
						rotationPoint.copy(rotationPointToShow).applyMatrix(camera.getLocalOrientationMatrix());
					}
				}
				System.out.println("Rotation around " + rotationPoint.x + " " + rotationPoint.y + " " + rotationPoint.z);
				isDragged = true;
				mouseStartX = mouseX;
				mouseStartY = mouseY;
			}
		}
		else {
			wasDragged = true;
		}
	}
	
	public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(!isMouseInside(mouseX, mouseY)) {
			return false;
		}
		
		if(paintingMode && clickedMouseButton == 0) {
			Ray3f ray = camera.project(mouseX, mouseY);
			IntersectionData intersectionData = checkIntersection(ray, true);
			if(intersectionData.material != null) {
				intersectionData.material.paint(intersectionData.point, brushSize, color);
			}
			
			return true;
		}
		
		wasDragged = true;
		
		if(isDragged) {
			if(clickedMouseButton == 1) {
				float angleX = 2 * (float) Math.PI * (mouseX - mouseStartX) / w;
				float angleY = 2 * (float) Math.PI * (mouseY - mouseStartY) / h;
				
				camera.rotate(rotationPoint, new Vec3f(0, 1, 0), angleX);
				camera.rotate(rotationPoint, new Vec3f(1, 0, 0), -angleY);
			}
			else if(clickedMouseButton == 2) {
				camera.translate(mouseX - mouseStartX, mouseY - mouseStartY, 0);
			}
			
			mouseStartX = mouseX;
			mouseStartY = mouseY;
			
			return true;
		}
		else if(transformControl.isDragged) {
			transformControl.mouseClickMove(camera.project(mouseX, mouseY));
			
			mouseStartX = mouseX;
			mouseStartY = mouseY;
		}
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		isDragged = false;
		transformControl.mouseReleased(mouseX, mouseY, state);
		
		// Click
		if(!wasDragged && isMouseInside(mouseX, mouseY) && state == 0) {
			if(intersectionResult != null && !paintingMode) {
				System.out.println("Intersection");
				transformControl.attachObject(intersectionResult);
				return;
			}
			else {
				transformControl.attachObject(null);
			}
		}
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
		
		if(modelObject.isModelNotNull() && modelObject.GlobalBoundingBox().isValid) {
			globalBBox.union(modelObject.GlobalBoundingBox());
		}
		
		if(showPlayer && playerObject.GlobalBoundingBox().isValid) {
			globalBBox.union(playerObject.GlobalBoundingBox());
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
		
		showPlayer = toShow;
		
		globalBBox.reset();
		calculateBBox();
		camera.setSceneBox(globalBBox);
	}
	
	public void resetCamera() {
		camera.resetTransformation();
	}
	
	public void saveModel() {
		
	}
	
	private IntersectionData checkIntersection(Ray3f ray, boolean isLocal) {
		IntersectionData intersectionData = new IntersectionData();
		float resDist = Float.MAX_VALUE;
		for(MovableObject object : objects) {
			IntersectionData localData = new IntersectionData();
			float dist = object.intersectRay(ray, localData);
			if(dist < resDist) {
				resDist = dist;
				intersectionResult = object;
				intersectionData.copy(localData);
			}
		}
		if(modelObject != null) {
			IntersectionData localData = new IntersectionData();
			float dist = modelObject.intersectRay(ray, localData);
			if(dist < resDist) {
				resDist = dist;
				intersectionResult = modelObject;
				intersectionData.copy(localData);
			}
		}
		
		if(intersectionResult != null) {
			intersectionData.point = new Vec3f(ray.direction).mul(resDist).add(ray.origin);
			
			if(isLocal) {
				intersectionData.point.applyMatrix(intersectionResult.invertTransformation);
			}
		}
		
		return intersectionData;
	}
	
	public void togglePainting() {
		paintingMode = !paintingMode;
	}
}
