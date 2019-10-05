package com.andreid278.cmc.client.model.reader;

import java.util.UUID;

import com.andreid278.cmc.client.model.CMCModel;

public class ModelReader extends CMCModelReader {
	
	UUID uuid;
	
	public ModelReader(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean read() {
		model = new CMCModel();
		return model.loadFromFile(uuid.toString());
	}

}
