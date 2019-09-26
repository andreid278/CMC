package com.andreid278.cmc.client.gui.viewer;

import java.nio.FloatBuffer;

import com.andreid278.cmc.utils.Box3f;
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
		
		Matrix4f.translate(dir, transformation, transformation);
		transformationFloatBuffer.rewind();
		transformation.store(transformationFloatBuffer);
		globalBBox.isValid = false;
	}
}
