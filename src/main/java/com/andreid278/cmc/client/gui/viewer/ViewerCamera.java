package com.andreid278.cmc.client.gui.viewer;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.Matrix4f;

public class ViewerCamera {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public Vec3f cameraTranslation = new Vec3f(0, 0, 0);
	public Quaternion cameraRotation = new Quaternion();
	public float cameraScale = 1.0f;
	Matrix4f cameraMatrix = new Matrix4f();
	boolean isUpdated = false;
	
	Ray3f ray = new Ray3f(0, 0, 0, 1, 0, 0);
	
	public static float scaleConst = 15.0f;
	
	public ViewerCamera(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public Ray3f project(int mouseX, int mouseY, Box3f box) {
		calculateCameraMatrix(box);
		
		ray.setOrigin(mouseX - x - w * 0.5f, mouseY - y - h * 0.5f, 10000);
		ray.setDirection(0, 0, -1);
		ray.transform(cameraMatrix);
		
		return ray;
	}
	
	public void resetTransformation() {
		cameraTranslation.set(0, 0, 0);
		cameraRotation.setIdentity();
		//cameraRotation.setFromAxisAngle(new Vector4f(0, 0, 1, (float)Math.PI));
		Quaternion rot = new Quaternion();
		//rot.setFromAxisAngle(new Vector4f(1, 0, 0, (float) Math.PI));
		rot.setFromAxisAngle(new Vector4f(0, 0, 1, (float) Math.PI));
		Quaternion.mul(cameraRotation, rot, cameraRotation);
		//rot.setFromAxisAngle(new Vector4f(0, 1, 0, (float) Math.PI));
		//Quaternion.mul(cameraRotation, rot, cameraRotation);
		cameraScale = 1.0f;
		
		isUpdated = true;
	}
	
	public void resetAndFitCamera(Box3f box) {
		resetTransformation();
		
		if(box.isValid) {
			float scale = scaleConst * box.getSize();
			cameraTranslation.set(-box.getCenterX() * scale, box.getCenterY() * scale, -box.getCenterZ() * scale);
		}
	}
	
	public void rotate(float axisX, float axisY, float axisZ, float angle) {
		Quaternion rot = new Quaternion();
		rot.setFromAxisAngle(new Vector4f(axisX, axisY, axisZ, angle));
		Quaternion.mul(rot, cameraRotation, cameraRotation);
		
		isUpdated = true;
	}
	
	public void translateOnScreenPlane(int dX, int dY) {
		/*Vec3f cameraDir = MathUtils.instance.new Vec3f(0, 0, -1);
		Vec3f cameraRight = MathUtils.instance.new Vec3f(1, 0, 0);
		Vec3f cameraUp = MathUtils.instance.new Vec3f(0, -1, 0);
		Vec3f.cross(cameraRight, cameraDir, cameraUp);
		
		cameraRight.mul((float)dX / w * 100);
		cameraUp.mul((float)dY / h * 100);
		
		cameraTranslation.add(cameraRight);
		cameraTranslation.add(cameraUp);*/
		
		cameraTranslation.x += (float)dX / w * 100;
		cameraTranslation.y += (float)dY / h * 100;
		
		isUpdated = true;
	}
	
	public void scale(int wheel) {
		float min = 0.1f;
		float max = 10.0f;
		float step = 0.05f;
		
		cameraScale = (float) Math.pow(cameraScale, 1.0 / 3.0);
		cameraScale += wheel * step;
		if(cameraScale < min) cameraScale = min;
		if(cameraScale > max) cameraScale = max;
		cameraScale = cameraScale * cameraScale * cameraScale;
		
		isUpdated = true;
	}
	
	private void calculateCameraMatrix(Box3f box) {
		if(!isUpdated) {
			return;
		}
		
		cameraMatrix.setIdentity();
		
		if(!box.isValid) {
			return;
		}
		
		// Scale
		float scale = 1.0f / (scaleConst * cameraScale * box.getSize());
		Vec3f scaleVector = new Vec3f(-scale, scale, scale);
		Matrix4f.scale(scaleVector, cameraMatrix, cameraMatrix);
		
		// Rotation
		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.m00 = 1 - 2 * cameraRotation.y * cameraRotation.y - 2 * cameraRotation.z * cameraRotation.z;
		rotationMatrix.m01 = 2 * cameraRotation.x * cameraRotation.y - 2 * cameraRotation.z * cameraRotation.w;
		rotationMatrix.m02 = 2 * cameraRotation.x * cameraRotation.z + 2 * cameraRotation.y * cameraRotation.w;
		rotationMatrix.m03 = 0;
		rotationMatrix.m10 = 2 * cameraRotation.x * cameraRotation.y + 2 * cameraRotation.z * cameraRotation.w;
		rotationMatrix.m11 = 1 - 2 * cameraRotation.x * cameraRotation.x - 2 * cameraRotation.z * cameraRotation.z;
		rotationMatrix.m12 = 2 * cameraRotation.y * cameraRotation.z - 2 * cameraRotation.x * cameraRotation.w;
		rotationMatrix.m13 = 0;
		rotationMatrix.m20 = 2 * cameraRotation.x * cameraRotation.z - 2 * cameraRotation.y * cameraRotation.w;
		rotationMatrix.m21 = 2 * cameraRotation.y * cameraRotation.z + 2 * cameraRotation.x * cameraRotation.w;
		rotationMatrix.m22 = 1 - 2 * cameraRotation.x * cameraRotation.x - 2 * cameraRotation.y * cameraRotation.y;
		rotationMatrix.m23 = 0;
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		rotationMatrix.m33 = 1;
		Matrix4f.mul(cameraMatrix, rotationMatrix, cameraMatrix);
		
		// Translation
		Vec3f inverseCameraTranslation = new Vec3f(cameraTranslation);
		inverseCameraTranslation.negate();
		Matrix4f.translate(inverseCameraTranslation, cameraMatrix, cameraMatrix);
		//cameraMatrix.transpose();
	}
}
