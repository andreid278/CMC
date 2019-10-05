package com.andreid278.cmc.client.gui.viewer;

import java.nio.FloatBuffer;

import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public abstract class MovableObject {
	public Matrix4f transformation;
	public FloatBuffer transformationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	public boolean isMovable;
	public Box3f globalBBox;
	
	public MovableObject() {
		transformation = new Matrix4f();
		transformation.setIdentity();
		transformationFloatBuffer.rewind();
		transformation.store(transformationFloatBuffer);
		
		globalBBox = new Box3f();
		
		isMovable = true;
	}
	
	public abstract void draw();
	
	public abstract Box3f BoundingBox();
	
	public Box3f GlobalBoundingBox() {
		if(!globalBBox.isValid) {
			globalBBox.transform(BoundingBox(), transformation);
		}
		
		return globalBBox;
	}
	
	public void translate(Vec3f dir) {
		Matrix4f translationMatrix = new Matrix4f();
		translationMatrix.setIdentity();
		Matrix4f.translate(dir, translationMatrix, translationMatrix);
		
		Matrix4f.mul(translationMatrix, transformation, transformation);
		
		transformationFloatBuffer.rewind();
		transformation.store(transformationFloatBuffer);
		globalBBox.isValid = false;
	}
	
	public void rotate(Vec3f center, Vec3f axis, float angle) {
		Matrix4f translationMatrix = new Matrix4f();
		translationMatrix.setIdentity();
		Matrix4f.translate(new Vec3f(center), translationMatrix, translationMatrix);
		
		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.setIdentity();
		Matrix4f.rotate(angle, axis, rotationMatrix, rotationMatrix);
		
		Matrix4f inverseTranslationMatrix = new Matrix4f();
		inverseTranslationMatrix.setIdentity();
		Matrix4f.translate(new Vec3f(center).mul(-1.0f), inverseTranslationMatrix, inverseTranslationMatrix);
		
		Matrix4f.mul(inverseTranslationMatrix, transformation, transformation);
		Matrix4f.mul(rotationMatrix, transformation, transformation);
		Matrix4f.mul(translationMatrix, transformation, transformation);
		
		transformationFloatBuffer.rewind();
		transformation.store(transformationFloatBuffer);
		globalBBox.isValid = false;
	}
	
	public void scale(Vec3f center, int axis, float coeff) {
		Vec3f translationVector = new Vec3f(center);
		
		Matrix4f translationMatrix = new Matrix4f();
		translationMatrix.setIdentity();
		Matrix4f.translate(translationVector, translationMatrix, translationMatrix);
		
		Matrix4f scaleMatrix = new Matrix4f();
		scaleMatrix.setIdentity();
		Vec3f scaleVector = new Vec3f(1, 1, 1);
		if(axis == 0) {
			scaleVector.setX(coeff);
		}
		else if(axis == 1) {
			scaleVector.setY(coeff);
		}
		else if(axis == 2) {
			scaleVector.setZ(coeff);
		}
		Matrix4f.scale(scaleVector, scaleMatrix, scaleMatrix);
		
		Matrix4f inverseTranslationMatrix = new Matrix4f();
		inverseTranslationMatrix.setIdentity();
		translationVector.negate();
		Matrix4f.translate(translationVector, inverseTranslationMatrix, inverseTranslationMatrix);
		
		Matrix4f.mul(transformation, translationMatrix, transformation);
		Matrix4f.mul(transformation, scaleMatrix, transformation);
		Matrix4f.mul(transformation, inverseTranslationMatrix, transformation);
		
		transformationFloatBuffer.rewind();
		transformation.store(transformationFloatBuffer);
		globalBBox.isValid = false;
	}
	
	public float intersectRay(Ray3f ray) {
		if(!isMovable) {
			return Float.MAX_VALUE;
		}
		
		Ray3f localRay = new Ray3f(ray.origin.x, ray.origin.y, ray.origin.z, ray.direction.x, ray.direction.y, ray.direction.z);
		Matrix4f inverseMatrix = new Matrix4f();
		inverseMatrix.setIdentity();
		Matrix4f.invert(transformation, inverseMatrix);
		localRay.transform(inverseMatrix);
		
		return intersectWithLocalRay(localRay);
	}
	
	protected abstract float intersectWithLocalRay(Ray3f ray);
}
