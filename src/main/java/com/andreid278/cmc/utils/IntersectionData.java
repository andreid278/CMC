package com.andreid278.cmc.utils;

import com.andreid278.cmc.client.gui.viewer.MovableObject;
import com.andreid278.cmc.client.model.MaterialGroup;

public class IntersectionData {
	public Vec3f point = null;
	public Vec3i triangle = null;
	public MaterialGroup material = null;
	
	public void copy(IntersectionData data) {
		point = data.point;
		triangle = data.triangle;
		material = data.material;
	}
}
