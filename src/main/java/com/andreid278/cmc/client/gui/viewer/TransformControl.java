package com.andreid278.cmc.client.gui.viewer;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.cmc.client.gui.GuiUtils;
import com.andreid278.cmc.utils.MathUtils;
import com.andreid278.cmc.utils.MathUtils.Ray3f;
import com.andreid278.cmc.utils.MathUtils.Vec3f;
import com.andreid278.cmc.utils.MathUtils.Vec3i;

import net.minecraft.client.renderer.GlStateManager;

public class TransformControl {
	public MovableObject attachedObject;
	public int mode;
	
	public Vec3f center = MathUtils.instance.new Vec3f(0, 0, 0);
	public Vec3f axisX = MathUtils.instance.new Vec3f(1, 0, 0);
	public Vec3f axisY = MathUtils.instance.new Vec3f(0, 1, 0);
	public Vec3f axisZ = MathUtils.instance.new Vec3f(0, 0, 1);
	
	public int selectedAxis;
	
	public boolean isDragged;
	
	public Vec3i colorAxisX = MathUtils.instance.new Vec3i(255, 0, 0);
	public Vec3i colorAxisY = MathUtils.instance.new Vec3i(0, 255, 0);
	public Vec3i colorAxisZ = MathUtils.instance.new Vec3i(0, 0, 255);
	public Vec3i selectionColor = MathUtils.instance.new Vec3i(255, 255, 0);
	
	public float tolerance;
	
	public TransformControl() {
		attachedObject = null;
		mode = 0;
		selectedAxis = -1;
		isDragged = false;
		tolerance = 1.0f;
	}
	
	public void attachObject(MovableObject object) {
		attachedObject = object;
		
		if(attachedObject != null && attachedObject.GlobalBoundingBox().isValid) {
			center.set(attachedObject.GlobalBoundingBox().getCenterX(),
					attachedObject.GlobalBoundingBox().getCenterY(),
					attachedObject.GlobalBoundingBox().getCenterZ());
			
			axisX.set(attachedObject.transformation.m00, attachedObject.transformation.m10, attachedObject.transformation.m20);
			axisY.set(attachedObject.transformation.m01, attachedObject.transformation.m11, attachedObject.transformation.m21);
			axisZ.set(attachedObject.transformation.m02, attachedObject.transformation.m12, attachedObject.transformation.m22);
			
			axisX.normalise();
			axisY.normalise();
			axisZ.normalise();
		}
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
		}
		
		isDragged = selectedAxis != -1;
		
		return isDragged;
	}
	
	public boolean mouseClickMove(Ray3f ray) {
		if(!isDragged) {
			if(mode == 0) {
				
			}
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
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axisX.x * tolerance, center.y + axisX.y * tolerance, center.z + axisX.z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 1 ? selectionColor : colorAxisY;
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axisY.x * tolerance, center.y + axisY.y * tolerance, center.z + axisY.z * tolerance, color.x, color.y, color.z);
		color = selectedAxis == 2 ? selectionColor : colorAxisZ;
		GuiUtils.drawLine(center.x, center.y, center.z, center.x + axisZ.x * tolerance, center.y + axisZ.y * tolerance, center.z + axisZ.z * tolerance, color.x, color.y, color.z);
		
		GlStateManager.enableDepth();
	}
	
	private void drawRotation() {
		
	}
	
	private void drawScaling() {
		
	}
	
	private int checkTranslation(Ray3f ray) {
		int res = -1;
		if(ray.intersectLine(center, MathUtils.instance.new Vec3f(center).add(axisX).mul(tolerance), 0.1f * tolerance)) {
			res = 0;
		}
		else if(ray.intersectLine(center, MathUtils.instance.new Vec3f(center).add(axisY).mul(tolerance), 0.1f * tolerance)) {
			res = 1;
		}
		else if(ray.intersectLine(center, MathUtils.instance.new Vec3f(center).add(axisZ).mul(tolerance), 0.1f * tolerance)) {
			res = 2;
		}
		return res;
	}
}
