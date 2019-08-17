package com.andreid278.cmc.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.andreid278.cmc.client.model.CMCModel;

import net.minecraftforge.client.model.IModel;

public class ModelStorage {
	public static ModelStorage instance = new ModelStorage();
	private Map<UUID, CMCModel> models = new HashMap<>();
	
	private CMCModel defaultModel;
	
	public CMCModel getModel(UUID uuid) {
		if(models.containsKey(uuid)) {
			return models.get(uuid);
		}
		
		return defaultModel;
	}
	
	public boolean hasModel(UUID uuid) {
		return models.containsKey(uuid);
	}
	
	public void addModel(UUID uuid, CMCModel model) {
		models.put(uuid, model);
	}
	
	public boolean isEmpty() {
		return models.isEmpty();
	}
	
	public CMCModel getAnyModel() {
		return (CMCModel) models.values().toArray()[0];
	}
}
