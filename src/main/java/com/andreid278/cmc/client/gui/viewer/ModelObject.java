package com.andreid278.cmc.client.gui.viewer;

import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.utils.MathUtils.Box3f;

public class ModelObject extends MovableObject {
	
	CMCModel model;
	
	public ModelObject(CMCModel model) {
		super();
		
		this.model = model;
	}

	@Override
	public void draw() {
		model.draw();
	}

	@Override
	public Box3f BoundingBox() {
		return model.bBox;
	}

}
