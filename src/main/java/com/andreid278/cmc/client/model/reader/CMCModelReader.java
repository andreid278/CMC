package com.andreid278.cmc.client.model.reader;

import com.andreid278.cmc.client.model.CMCModel;

public abstract class CMCModelReader {
	protected CMCModel model = null;
	
	public abstract boolean read();
	
	public CMCModel getModel() {
		return model;
	}
}
