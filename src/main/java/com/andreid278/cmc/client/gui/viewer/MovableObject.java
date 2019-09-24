package com.andreid278.cmc.client.gui.viewer;

import com.andreid278.cmc.utils.MathUtils;
import com.andreid278.cmc.utils.MathUtils.Box3f;

import net.minecraft.client.renderer.Matrix4f;

public abstract class MovableObject {
	public Matrix4f transformation;
	public boolean isMovable;
	public Box3f globalBBox;
	
	public MovableObject() {
		transformation = new Matrix4f();
		transformation.setIdentity();
		
		globalBBox = MathUtils.instance.new Box3f();
	}
	
	public abstract void draw();
	
	public abstract Box3f BoundingBox();
	
	public Box3f GlobalBoundingBox() {
		if(!globalBBox.isValid) {
			globalBBox.transform(BoundingBox(), transformation);
		}
		
		return globalBBox;
	}
}
