package com.andreid278.cmc.client.model.reader;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.cmc.client.gui.viewer.ModelObject;
import com.andreid278.cmc.client.gui.viewer.MovableObject;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.MaterialGroup;

public class ConstructReader extends CMCModelReader {
	
	public List<MovableObject> objects;
	
	public ConstructReader(List<MovableObject> objects) {
		this.objects = objects;
	}

	@Override
	public boolean read() {
		if(this.objects == null || this.objects.size() == 0) {
			return false;
		}
		
		List<MaterialGroup> materials = new ArrayList<>();
		
		for(MovableObject object : objects) {
			if(object instanceof ModelObject) {
				ModelObject modelObject = (ModelObject) object;
				if(!modelObject.isModelNotNull()) {
					continue;
				}
				
				for(MaterialGroup objectMaterial : modelObject.model.materials) {
					materials.add(objectMaterial.copyWithTransformation(modelObject.transformation));
				}
			}
		}
		
		model = new CMCModel(materials);
		
		return true;
	}
	
}
