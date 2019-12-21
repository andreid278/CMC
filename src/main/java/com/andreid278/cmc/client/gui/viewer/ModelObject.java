package com.andreid278.cmc.client.gui.viewer;

import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.IntersectionData;
import com.andreid278.cmc.utils.Ray3f;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.GlStateManager;

public class ModelObject extends MovableObject {
	
	public CMCModel model;
	
	public ModelObject(CMCModel model) {
		super();
		
		this.model = model;
	}

	@Override
	public void draw() {
		if(isModelNotNull()) {
			GlStateManager.pushMatrix();
			transformationFloatBuffer.rewind();
			GlStateManager.multMatrix(transformationFloatBuffer);
			model.draw();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public Box3f BoundingBox() {
		return model.bBox;
	}
	
	public boolean isModelNotNull() {
		return model != null;
	}
	
	@Override
	protected float intersectWithLocalRay(Ray3f ray, IntersectionData intersectionData) {
		if(isModelNotNull()) {
			return ray.intersectCMCModel(model, intersectionData);
		}
		return Float.MAX_VALUE;
	}

}
