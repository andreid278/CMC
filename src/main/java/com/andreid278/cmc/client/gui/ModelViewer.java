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
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.utils.MathUtils;
import com.andreid278.cmc.utils.MathUtils.Box3f;
import com.andreid278.cmc.utils.MathUtils.Ray3f;
import com.andreid278.cmc.utils.MathUtils.Vec3f;

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
	Box3f globalBBox = MathUtils.instance.new Box3f();
	boolean showPlayer = false;
	
	TransformControl transformControl = new TransformControl();
	public boolean canAttachTransformControl = false;
	
	public boolean isDragged = false;
	public int mouseStartX;
	public int mouseStartY;
	
	Vec3f cameraTranslation = MathUtils.instance.new Vec3f(0, 0, 0);
	Quaternion cameraRotation = new Quaternion();
	float cameraScale = 1.0f;
	
	Matrix4f cameraMatrix = new Matrix4f();
	Matrix4f translationMatrix = new Matrix4f();
	Matrix4f rotationMatrix = new Matrix4f();
	Matrix4f scaleMatrix = new Matrix4f();
	
	Ray3f ray = MathUtils.instance.new Ray3f(0, 0, 0, 1, 0, 0);
	
	public int curMouseX;
	public int curMouseY;
	
	public ModelViewer(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
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
		
		resetAndFitCamera();
		
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
		
		/*if(model != null) {
			GlStateManager.pushMatrix();
			float scale = 15 * cameraScale * model.bBox.getSize();
			GlStateManager.translate(x + w / 2, y + h / 2, scale);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(cameraRotation);
			GlStateManager.translate(-model.bBox.getSizeX() * 0.5f, -model.bBox.getSizeY() * 0.5f, -model.bBox.getSizeZ() * 0.5f);
			model.draw();
			GlStateManager.popMatrix();
		}*/
		
		calculateBBox();
		
		if(globalBBox.isValid) {
			GlStateManager.pushMatrix();
			float scale = 15 * cameraScale * globalBBox.getSize();
			GlStateManager.translate(x + w / 2, y + h / 2, scale);
			GlStateManager.translate(cameraTranslation.x, cameraTranslation.y, cameraTranslation.z);
			GlStateManager.rotate(cameraRotation);
			GlStateManager.scale(scale, scale, scale);
			
			Iterator<MovableObject> it = objects.iterator();
			while(it.hasNext()) {
				MovableObject object = it.next();
				object.draw();
			}
			
			transformControl.tolerance = 1.0f / cameraScale;
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
				/*ray.setOrigin(mouseX - x - w * 0.5f, mouseY - y - h * 0.5f, 10000);
				ray.setDirection(0, 0, -1);
				
				Matrix4f cameraMatrix = new Matrix4f();
				cameraMatrix.setIdentity();
				Matrix4f.mul(cameraMatrix, scaleMatrix, cameraMatrix);
				//Vec3f tr = MathUtils.instance.new Vec3f(ray.origin).sub(cameraTranslation);
				Vec3f tr = MathUtils.instance.new Vec3f(0, 0, 15 * cameraScale * globalBBox.getSize());
				//Matrix4f.translate(tr, cameraMatrix, cameraMatrix);
				Matrix4f.mul(cameraMatrix, rotationMatrix, cameraMatrix);
				tr.negate();
				//Matrix4f.translate(tr, cameraMatrix, cameraMatrix);
				Matrix4f.mul(cameraMatrix, translationMatrix, cameraMatrix);
				
				//cameraMatrix.invert();
				cameraMatrix.transpose();
				ray.transform(cameraMatrix);*/
				mouseToRay(mouseX, mouseY);
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
				Quaternion rot = new Quaternion();
				rot.setFromAxisAngle(new Vector4f(0, 1, 0, angleX));
				Quaternion.mul(rot, cameraRotation, cameraRotation);
				rot.setFromAxisAngle(new Vector4f(1, 0, 0, -angleY));
				Quaternion.mul(rot, cameraRotation, cameraRotation);
			}
			else if(clickedMouseButton == 2) {
				Vec3f cameraDir = MathUtils.instance.new Vec3f(0, 0, -1);
				//cameraDir.applyQuaternion(cameraRotation);
				Vec3f cameraRight = MathUtils.instance.new Vec3f(1, 0, 0);
				//cameraRight.applyQuaternion(cameraRotation);
				Vec3f cameraUp = MathUtils.instance.new Vec3f(0, -1, 0);
				Vec3f.cross(cameraRight, cameraDir, cameraUp);
				
				float dX = mouseX - mouseStartX;
				float dY = mouseY - mouseStartY;
				
				cameraRight.mul(dX / w * 100);
				cameraUp.mul(dY / h * 100);
				
				cameraTranslation.add(cameraRight);
				cameraTranslation.add(cameraUp);
			}
			
			mouseStartX = mouseX;
			mouseStartY = mouseY;
			calculateCameraMatrix();
			
			return true;
		}
		else if(transformControl.isDragged) {
			mouseToRay(mouseX, mouseY);
			
			transformControl.mouseClickMove(ray);
			
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
		float min = 0.1f;
		float max = 10.0f;
		float step = 0.05f;
		cameraScale = (float) Math.pow(cameraScale, 1.0 / 3.0);
		cameraScale += wheel * step;
		if(cameraScale < min) cameraScale = min;
		if(cameraScale > max) cameraScale = max;
		cameraScale = cameraScale * cameraScale * cameraScale;
		
		calculateCameraMatrix();
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}
	
	public void resetTransformation() {
		cameraTranslation.set(0, 0, 0);
		cameraRotation.setIdentity();
		cameraScale = 1.0f;
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
		resetAndFitCamera();
	}
	
	public void resetAndFitCamera() {
		calculateBBox();
		
		resetTransformation();
		
		if(globalBBox.isValid) {
			float scale = 15 * globalBBox.getSize();
			cameraTranslation.set(-globalBBox.getCenterX() * scale, -globalBBox.getCenterY() * scale, -globalBBox.getCenterZ() * scale);
		}
		
		calculateCameraMatrix();
	}
	
	private void calculateCameraMatrix() {
		if(!globalBBox.isValid) {
			return;
		}
		
		translationMatrix.setIdentity();
		Vec3f inverseCameraTranslation = MathUtils.instance.new Vec3f(cameraTranslation);
		inverseCameraTranslation.negate();
		Matrix4f.translate(inverseCameraTranslation, translationMatrix, translationMatrix);
		
		Quaternion inverseCameraRotation = new Quaternion(cameraRotation.x, cameraRotation.y, cameraRotation.z, cameraRotation.w);
		inverseCameraRotation.negate();
		rotationMatrix.m00 = 1 - 2 * inverseCameraRotation.y * inverseCameraRotation.y - 2 * inverseCameraRotation.z * inverseCameraRotation.z;
		rotationMatrix.m01 = 2 * inverseCameraRotation.x * inverseCameraRotation.y + 2 * inverseCameraRotation.z * inverseCameraRotation.w;
		rotationMatrix.m02 = 2 * inverseCameraRotation.x * inverseCameraRotation.z - 2 * inverseCameraRotation.y * inverseCameraRotation.w;
		rotationMatrix.m03 = 0;
		rotationMatrix.m10 = 2 * inverseCameraRotation.x * inverseCameraRotation.y - 2 * inverseCameraRotation.z * inverseCameraRotation.w;
		rotationMatrix.m11 = 1 - 2 * inverseCameraRotation.x * inverseCameraRotation.x - 2 * inverseCameraRotation.z * inverseCameraRotation.z;
		rotationMatrix.m12 = 2 * inverseCameraRotation.y * inverseCameraRotation.z + 2 * inverseCameraRotation.x * inverseCameraRotation.w;
		rotationMatrix.m13 = 0;
		rotationMatrix.m20 = 2 * inverseCameraRotation.x * inverseCameraRotation.z + 2 * inverseCameraRotation.y * inverseCameraRotation.w;
		rotationMatrix.m21 = 2 * inverseCameraRotation.y * inverseCameraRotation.z - 2 * inverseCameraRotation.x * inverseCameraRotation.w;
		rotationMatrix.m22 = 1 - 2 * inverseCameraRotation.x * inverseCameraRotation.x - 2 * inverseCameraRotation.y * inverseCameraRotation.y;
		rotationMatrix.m23 = 0;
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		rotationMatrix.m33 = 1;
		
		scaleMatrix.setIdentity();
		float scale = 1.0f / (15 * cameraScale * globalBBox.getSize());
		Vec3f scaleVector = MathUtils.instance.new Vec3f(scale, scale, scale);
		Matrix4f.scale(scaleVector, scaleMatrix, scaleMatrix);
		
		cameraMatrix.setIdentity();
		Matrix4f.mul(cameraMatrix, scaleMatrix, cameraMatrix);
		//Vec3f tr = MathUtils.instance.new Vec3f(ray.origin).sub(cameraTranslation);
		//Vec3f tr = MathUtils.instance.new Vec3f(0, 0, 15 * cameraScale * globalBBox.getSize());
		//Matrix4f.translate(tr, cameraMatrix, cameraMatrix);
		Matrix4f.mul(cameraMatrix, rotationMatrix, cameraMatrix);
		//tr.negate();
		//Matrix4f.translate(tr, cameraMatrix, cameraMatrix);
		Matrix4f.mul(cameraMatrix, translationMatrix, cameraMatrix);
		
		//cameraMatrix.invert();
		cameraMatrix.transpose();
	}
	
	private void mouseToRay(int mouseX, int mouseY) {
		ray.setOrigin(mouseX - x - w * 0.5f, mouseY - y - h * 0.5f, 10000);
		ray.setDirection(0, 0, -1);
		ray.transform(cameraMatrix);
	}
}
