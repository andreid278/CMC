package com.andreid278.cmc.client.gui.viewer;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.cmc.client.gui.GuiUtils;
import com.andreid278.cmc.utils.Plane;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;
import com.andreid278.cmc.utils.Vec3i;

import net.minecraft.client.renderer.GlStateManager;

public class TransformControl {
	public MovableObject attachedObject;
	public int mode;
	
	public Vec3f center = new Vec3f(0, 0, 0);
	public Vec3f[] axis = new Vec3f[3];
	
	public int selectedAxis;
	
	public boolean isDragged;
	
	public Vec3i colorAxisX = new Vec3i(255, 0, 0);
	public Vec3i colorAxisY = new Vec3i(0, 255, 0);
	public Vec3i colorAxisZ = new Vec3i(0, 0, 255);
	public Vec3i selectionColor = new Vec3i(255, 255, 0);
	
	public float tolerance;
	
	public Vec3f startPoint = new Vec3f(0, 0, 0);
	public Plane draggingPlane = new Plane();
	
	float prevScale = 1.0f;
	Vec3f startCenter = new Vec3f();
	
	public TransformControl() {
		attachedObject = null;
		mode = 0;
		selectedAxis = -1;
		isDragged = false;
		tolerance = 1.0f;
		
		axis[0] = new Vec3f(1, 0, 0);
		axis[1] = new Vec3f(0, 1, 0);
		axis[2] = new Vec3f(0, 0, 1);
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void attachObject(MovableObject object) {
		attachedObject = object;
		calculateCenterAxis();
		startCenter.copy(center);
	}
	
	public void draw() {
		if(attachedObject == null || !attachedObject.GlobalBoundingBox().isValid) {
			return;
		}
		
		if(mode == 0) {
			drawTranslation();
		}
		else if(mode == 1) {
			drawRotation();
		}
		else if(mode == 2) {
			drawScaling();
		}
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, Ray3f ray) {
		selectedAxis = -1;
		if(mode == 0) {
			selectedAxis = checkTranslation(ray);
			
			if(selectedAxis != -1) {
				Vec3f v = new Vec3f();
				Vec3f.cross(axis[selectedAxis], ray.direction, v);
				v.normalise();
				draggingPlane.setFromPointVectorVector(startPoint, axis[selectedAxis], v);
			}
		}
		else if(mode == 1) {
			selectedAxis = checkRotation(ray);
			
			if(selectedAxis != -1) {
				draggingPlane.setFromPointNormal(center, axis[selectedAxis]);
			}
		}
		else if(mode == 2) {
			selectedAxis = checkScale(ray);
			
			if(selectedAxis != -1) {
				Vec3f v = new Vec3f();
				Vec3f.cross(axis[selectedAxis], ray.direction, v);
				v.normalise();
				draggingPlane.setFromPointVectorVector(startPoint, axis[selectedAxis], v);
			}
		}
		
		isDragged = selectedAxis != -1;
		
		return isDragged;
	}
	
	public boolean mouseClickMove(Ray3f ray) {
		if(isDragged) {
			if(mode == 0) {
				processTranslation(ray);
			}
			else if(mode == 1) {
				processRotation(ray);
			}
			else if(mode == 2) {
				processScale(ray);
			}
			
			return true;
		}
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		isDragged = false;
		selectedAxis = -1;
	}
	
	private void drawTranslation() {
		GlStateManager.disableDepth();
		
		Vec3i color;
		
		color = selectedAxis == 0 ? selectionColor : colorAxisX;
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axis[0].x * tolerance, center.y + axis[0].y * tolerance, center.z + axis[0].z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 1 ? selectionColor : colorAxisY;
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axis[1].x * tolerance, center.y + axis[1].y * tolerance, center.z + axis[1].z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 2 ? selectionColor : colorAxisZ;
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axis[2].x * tolerance, center.y + axis[2].y * tolerance, center.z + axis[2].z * tolerance, color.x, color.y, color.z);
		
		GlStateManager.enableDepth();
	}
	
	private void drawRotation() {
		GlStateManager.disableDepth();
		
		Vec3i color;
		
		color = selectedAxis == 0 ? selectionColor : colorAxisX;
		GuiUtils.drawCircle(center, axis[0], tolerance, 32, color.x, color.y, color.z);
		color = selectedAxis == 1 ? selectionColor : colorAxisY;
		GuiUtils.drawCircle(center, axis[1], tolerance, 32, color.x, color.y, color.z);
		color = selectedAxis == 2 ? selectionColor : colorAxisZ;
		GuiUtils.drawCircle(center, axis[2], tolerance, 32, color.x, color.y, color.z);
		
		GlStateManager.enableDepth();
	}
	
	private void drawScaling() {
		GlStateManager.disableDepth();
		
		Vec3i color;
		
		color = selectedAxis == 0 ? selectionColor : colorAxisX;
		GuiUtils.drawLine(center.x - axis[0].x * tolerance, center.y - axis[0].y * tolerance, center.z - axis[0].z * tolerance, center.x + axis[0].x * tolerance, center.y + axis[0].y * tolerance, center.z + axis[0].z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 1 ? selectionColor : colorAxisY;
		GuiUtils.drawLine(center.x - axis[1].x * tolerance, center.y - axis[1].y * tolerance, center.z - axis[1].z * tolerance, center.x + axis[1].x * tolerance, center.y + axis[1].y * tolerance, center.z + axis[1].z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 2 ? selectionColor : colorAxisZ;
		GuiUtils.drawLine(center.x - axis[2].x * tolerance, center.y - axis[2].y * tolerance, center.z - axis[2].z * tolerance, center.x + axis[2].x * tolerance, center.y + axis[2].y * tolerance, center.z + axis[2].z * tolerance, color.x, color.y, color.z);
		
		GlStateManager.enableDepth();
	}
	
	private int checkTranslation(Ray3f ray) {
		int res = -1;
		float dist = Float.MAX_VALUE;
		
		float distX = ray.intersectLine(center, new Vec3f(axis[0]).mul(tolerance).add(center), 0.1f * tolerance);
		float distY = ray.intersectLine(center, new Vec3f(axis[1]).mul(tolerance).add(center), 0.1f * tolerance);
		float distZ = ray.intersectLine(center, new Vec3f(axis[2]).mul(tolerance).add(center), 0.1f * tolerance);
		
		if(distX < dist) {
			dist = distX;
			res = 0;
		}
		
		if(distY < dist) {
			dist = distY;
			res = 1;
		}
		
		if(distZ < dist) {
			dist = distZ;
			res = 2;
		}
		
		if(res != -1) {
			startPoint.set(ray.direction.x * dist + ray.origin.x, ray.direction.y * dist + ray.origin.y, ray.direction.z * dist + ray.origin.z);
		}
		
		return res;
	}
	
	private void processTranslation(Ray3f ray) {
		float t = ray.intersectPlane(draggingPlane);
		if(t < Float.MAX_VALUE && t > 1e-5) {
			Vec3f point = new Vec3f(ray.direction).mul(t).add(ray.origin);
			Vec3f dir = new Vec3f(point).sub(startPoint);
			float length = Vec3f.dot(dir, axis[selectedAxis]);
			dir.copy(axis[selectedAxis]);
			dir.mul(length);
			startPoint.copy(point);
		
			if(attachedObject != null) {
				attachedObject.translate(dir);
				center.add(dir);
			}
		}
	}
	
	private int checkRotation(Ray3f ray) {
		int res = -1;
		float dist = Float.MAX_VALUE;
		
		float distX = ray.intersectCircle(center, axis[0], tolerance, 0.1f * tolerance);
		float distY = ray.intersectCircle(center, axis[1], tolerance, 0.1f * tolerance);
		float distZ = ray.intersectCircle(center, axis[2], tolerance, 0.1f * tolerance);
		
		if(distX < dist) {
			dist = distX;
			res = 0;
		}
		
		if(distY < dist) {
			dist = distY;
			res = 1;
		}
		
		if(distZ < dist) {
			dist = distZ;
			res = 2;
		}
		
		if(res != -1) {
			startPoint.set(ray.direction.x * dist + ray.origin.x, ray.direction.y * dist + ray.origin.y, ray.direction.z * dist + ray.origin.z);
		}
		
		return res;
	}
	
	private void processRotation(Ray3f ray) {
		float t = ray.intersectPlane(draggingPlane);
		if(t < Float.MAX_VALUE && t > 1e-5) {
			Vec3f point = new Vec3f(ray.direction).mul(t).add(ray.origin);
			Vec3f startDir = new Vec3f(startPoint).sub(center);
			Vec3f curDir = new Vec3f(point).sub(center);
			Vec3f cross = new Vec3f();
			Vec3f.cross(startDir, curDir, cross);
			startPoint.copy(point);
		
			if(attachedObject != null) {
				attachedObject.rotate(center, axis[selectedAxis], Vec3f.angle(startDir, curDir) * (Vec3f.dot(cross, axis[selectedAxis]) > 0 ? 1 : -1));
				
				axis[0].set(attachedObject.transformation.m00, attachedObject.transformation.m01, attachedObject.transformation.m02);
				axis[1].set(attachedObject.transformation.m10, attachedObject.transformation.m11, attachedObject.transformation.m12);
				axis[2].set(attachedObject.transformation.m20, attachedObject.transformation.m21, attachedObject.transformation.m22);
				
				axis[0].normalise();
				axis[1].normalise();
				axis[2].normalise();
			}
		}
	}
	
	private int checkScale(Ray3f ray) {
		int res = -1;
		float dist = Float.MAX_VALUE;
		
		float distX = ray.intersectLine(new Vec3f(axis[0]).mul(-tolerance).add(center), new Vec3f(axis[0]).mul(tolerance).add(center), 0.1f * tolerance);
		float distY = ray.intersectLine(new Vec3f(axis[1]).mul(-tolerance).add(center), new Vec3f(axis[1]).mul(tolerance).add(center), 0.1f * tolerance);
		float distZ = ray.intersectLine(new Vec3f(axis[2]).mul(-tolerance).add(center), new Vec3f(axis[2]).mul(tolerance).add(center), 0.1f * tolerance);
		
		if(distX < dist) {
			dist = distX;
			res = 0;
		}
		
		if(distY < dist) {
			dist = distY;
			res = 1;
		}
		
		if(distZ < dist) {
			dist = distZ;
			res = 2;
		}
		
		if(res != -1) {
			startPoint.set(ray.direction.x * dist + ray.origin.x, ray.direction.y * dist + ray.origin.y, ray.direction.z * dist + ray.origin.z);
			prevScale = 1.0f;
		}
		
		return res;
	}
	
	private void processScale(Ray3f ray) {
		float t = ray.intersectPlane(draggingPlane);
		if(t < Float.MAX_VALUE && t > 1e-5) {
			Vec3f point = new Vec3f(ray.direction).mul(t).add(ray.origin);
			Vec3f startDir = new Vec3f(startPoint).sub(center);
			Vec3f curDir = new Vec3f(point).sub(center);
			float startLength = Vec3f.dot(axis[selectedAxis], startDir);
			float curLength = Vec3f.dot(axis[selectedAxis], curDir);
			
			if(Math.abs(startLength) < 1e-4 || Math.abs(curLength) < 1e-4) {
				return;
			}
			
			float curScale = curLength / startLength;
		
			if(attachedObject != null) {
				attachedObject.scale(startCenter, selectedAxis, 1.0f / prevScale);
				attachedObject.scale(startCenter, selectedAxis, curScale);
				prevScale = curScale;
				
				axis[0].set(attachedObject.transformation.m00, attachedObject.transformation.m01, attachedObject.transformation.m02);
				axis[1].set(attachedObject.transformation.m10, attachedObject.transformation.m11, attachedObject.transformation.m12);
				axis[2].set(attachedObject.transformation.m20, attachedObject.transformation.m21, attachedObject.transformation.m22);
				
				axis[0].normalise();
				axis[1].normalise();
				axis[2].normalise();
			}
		}
	}
	
	private void calculateCenterAxis() {
		if(attachedObject != null && attachedObject.GlobalBoundingBox().isValid) {
			center.set(attachedObject.GlobalBoundingBox().getCenterX(),
					attachedObject.GlobalBoundingBox().getCenterY(),
					attachedObject.GlobalBoundingBox().getCenterZ());
			
			//axis[0].set(attachedObject.transformation.m00, attachedObject.transformation.m10, attachedObject.transformation.m20);
			//axis[1].set(attachedObject.transformation.m01, attachedObject.transformation.m11, attachedObject.transformation.m21);
			//axis[2].set(attachedObject.transformation.m02, attachedObject.transformation.m12, attachedObject.transformation.m22);
			axis[0].set(attachedObject.transformation.m00, attachedObject.transformation.m01, attachedObject.transformation.m02);
			axis[1].set(attachedObject.transformation.m10, attachedObject.transformation.m11, attachedObject.transformation.m12);
			axis[2].set(attachedObject.transformation.m20, attachedObject.transformation.m21, attachedObject.transformation.m22);
			
			axis[0].normalise();
			axis[1].normalise();
			axis[2].normalise();
		}
	}
}
