package com.andreid278.cmc.client.gui.viewer;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public class ViewerCamera {
	public int x;
	public int y;
	public int w;
	public int h;
	
	Vec3f offsetToCenter = new Vec3f();
	Vec3f eye = new Vec3f();
	Vec3f dir = new Vec3f();
	Vec3f up = new Vec3f();
	float dist = 0.0f;
	
	float scale = 1.0f;
	
	Matrix4f orientationMatrix = new Matrix4f();
	Matrix4f inversedOrientationMatrix = new Matrix4f();
	Matrix4f localOrientationMatrix = new Matrix4f();
	
	public FloatBuffer transformationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	public FloatBuffer inversedTransformationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	
	boolean isOrientationValid = true;
	
	Ray3f ray = new Ray3f(0, 0, 0, 1, 0, 0);
	
	Box3f sceneBox;
	
	float translationCoeff = 100.0f;
	float scaleConst = 15.0f;
	
	public ViewerCamera(int x, int y, int w, int h, Box3f sceneBox) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		this.sceneBox = sceneBox;
		
		orientationMatrix.setIdentity();
		inversedOrientationMatrix.setIdentity();
		
		transformationFloatBuffer.rewind();
		orientationMatrix.store(transformationFloatBuffer);
		
		inversedTransformationFloatBuffer.rewind();
		inversedOrientationMatrix.store(inversedTransformationFloatBuffer);
		
		resetTransformation();
	}
	
	private void calculateMatrix() {
		if(isOrientationValid) {
			return;
		}
		
		localOrientationMatrix.setIdentity();
		
		float sc = scale * scaleConst * (sceneBox.isValid ? sceneBox.getSize() : 1.0f);
		
		Vec3f side = new Vec3f();
		Vec3f.cross(dir, up, side);
		side.normalise();
		
		localOrientationMatrix.m30 = eye.x;
		localOrientationMatrix.m31 = eye.y;
		localOrientationMatrix.m32 = eye.z;
		
		localOrientationMatrix.m00 = side.x * sc;
		localOrientationMatrix.m01 = side.y * sc;
		localOrientationMatrix.m02 = side.z * sc;
		localOrientationMatrix.m10 = up.x * sc;
		localOrientationMatrix.m11 = up.y * sc;
		localOrientationMatrix.m12 = up.z * sc;
		localOrientationMatrix.m20 = dir.x * sc;
		localOrientationMatrix.m21 = dir.y * sc;
		localOrientationMatrix.m22 = dir.z * sc;
		
		orientationMatrix.load(localOrientationMatrix);
		orientationMatrix.m30 += offsetToCenter.x;
		orientationMatrix.m31 += offsetToCenter.y;
		orientationMatrix.m32 += offsetToCenter.z;
		
		transformationFloatBuffer.rewind();
		orientationMatrix.store(transformationFloatBuffer);
		
		Matrix4f.invert(orientationMatrix, inversedOrientationMatrix);
		
		inversedTransformationFloatBuffer.rewind();
		inversedOrientationMatrix.store(inversedTransformationFloatBuffer);
		
		isOrientationValid = true;
	}
	
	public Matrix4f getOrientationMatrix() {
		calculateMatrix();
		
		return orientationMatrix;
	}
	
	public FloatBuffer getOrientationMatrixBuffer() {
		calculateMatrix();
		
		transformationFloatBuffer.rewind();
		return transformationFloatBuffer;
	}
	
	public Matrix4f getInversedOrientationMatrix() {
		calculateMatrix();
		
		return inversedOrientationMatrix;
	}
	
	public FloatBuffer getInversedOrientationMatrixBuffer() {
		calculateMatrix();
		
		inversedTransformationFloatBuffer.rewind();
		return inversedTransformationFloatBuffer;
	}
	
	public Matrix4f getLocalOrientationMatrix() {
		calculateMatrix();
		
		return localOrientationMatrix;
	}
	
	public void translate(int dX, int dY, int dZ) {
		float prevX = eye.x;
		float prevY = eye.y;
		float prevZ = eye.z;
		
		eye.x += (float)dX / w * translationCoeff;
		eye.y += (float)dY / h * translationCoeff;
		eye.z += (float)dZ;
		
		invalidate();
	}
	
	public void rotate(Vec3f p, Vec3f axis, float angle) {
		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.setIdentity();
		
		Matrix4f.translate(p, rotationMatrix, rotationMatrix);
		
		Matrix4f.rotate(angle, axis, rotationMatrix, rotationMatrix);
		
		Vec3f mp = new Vec3f(p);
		mp.mul(-1.0f);
		Matrix4f.translate(mp, rotationMatrix, rotationMatrix);
		
		eye.applyMatrix(rotationMatrix);
		dir.transformDirection(rotationMatrix);
		up.transformDirection(rotationMatrix);
		
		invalidate();
	}
	
	public void scale(float ds) {
		float min = 0.1f;
		float max = 10.0f;
		float step = 0.05f;
		
		scale = (float) Math.pow(scale, 1.0 / 3.0);
		scale += ds * step;
		if(scale < min) scale = min;
		if(scale > max) scale = max;
		scale = scale * scale * scale;
		
		invalidate();
	}
	
	private void invalidate() {
		isOrientationValid = false;
	}
	
	public Ray3f project(int mouseX, int mouseY) {
		ray.setOrigin(mouseX, mouseY, 10000);
		ray.setDirection(0, 0, -1);
		getOrientationMatrix();
		ray.transform(inversedOrientationMatrix);
		return ray;
	}
	
	public Vec3f getDir() {
		Vec3f camDir = new Vec3f(0, 0, 1);
		camDir.transformDirection(getInversedOrientationMatrix());
		return camDir;
	}
	
	public void resetTransformation() {
		scale = 1.0f;
		
		if(sceneBox.isValid) {
			float boxSize = sceneBox.getSize() * scaleConst;
			offsetToCenter.set(x + w * 0.5f - sceneBox.getCenterX() * boxSize, y + w * 0.5f - sceneBox.getCenterY() * boxSize, -sceneBox.getCenterZ() * boxSize);
		}
		else {
			offsetToCenter.set(x + w * 0.5f, y + w * 0.5f, 0.0f);
		}
		
		eye.set(0.0f, 0.0f, 0.0f);
		dir.set(0.0f, 0.0f, 1.0f);
		up.set(0.0f, -1.0f, 0.0f);
		
		invalidate();
	}
	
	public void setSceneBox(Box3f box) {
		sceneBox = box;
		resetTransformation();
	}
	
	public float getScale() {
		return scale;
	}
}
