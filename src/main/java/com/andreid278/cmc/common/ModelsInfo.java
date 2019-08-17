package com.andreid278.cmc.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.ModelReader;

public class ModelsInfo {
	public static ModelsInfo instance = new ModelsInfo();
	
	public class ModelInfo {
		public String author;
		public String name;
		public boolean isPublic;
		
		public ModelInfo(String author, String name, boolean isPublic) {
			this.author = author;
			this.name = name;
			this.isPublic = isPublic;
		}
	}
	
	Map<UUID, ModelInfo> modelToInfoMap = new LinkedHashMap<>();
	Map<String, List<UUID>> authorToModelsMap = new LinkedHashMap<>();
	Map<String, List<UUID>> nameToModelsMap = new LinkedHashMap<>();
	List<UUID> publicModelsList = new ArrayList<>();
	
	public boolean isInitialized = false;
	
	public void add(UUID uuid, String author, String name, boolean isPublic) {
		if(!modelToInfoMap.containsKey(uuid)) {
			modelToInfoMap.put(uuid, new ModelInfo(author, name, isPublic));
		}
		
		List<UUID> authorModelList;
		if(!authorToModelsMap.containsKey(author)) {
			authorModelList = new ArrayList<>();
			authorToModelsMap.put(author, authorModelList);
		}
		else {
			authorModelList = authorToModelsMap.get(author);
		}
		authorModelList.add(uuid);
		
		List<UUID> nameModelList;
		if(!nameToModelsMap.containsKey(name)) {
			nameModelList = new ArrayList<>();
			nameToModelsMap.put(name, nameModelList);
		}
		else {
			nameModelList = nameToModelsMap.get(name);
		}
		nameModelList.add(uuid);
		
		if(isPublic) {
			publicModelsList.add(uuid);
		}
		
		System.out.println("Added " + uuid.toString() + ", author = " + author + ", name = " + name + ", isPublic = " + (isPublic ? "true" : "false"));
	}
	
	public List<UUID> getModelsByAuthor(String author) {
		return authorToModelsMap.get(author);
	}
	
	public void init(String path) {
		if(isInitialized) {
			return;
		}
		
		modelToInfoMap.clear();
		authorToModelsMap.clear();
		nameToModelsMap.clear();
		publicModelsList.clear();
		
		File file = new File(path);
		if(!file.isDirectory())
			file.mkdirs();

		for(String f : file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("^[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}.(properties)$");
			}
		})) {
			UUID uuid = UUID.fromString(f.substring(0, f.lastIndexOf(".")));
			String name = null;
			String author = null;
			String isPublic = null;
			try {
				FileReader reader = new FileReader(path + f);
				Properties prop = new Properties();
				prop.load(reader);
				
				name = prop.getProperty("name");
				author = prop.getProperty("author");
				isPublic = prop.getProperty("isPublic");
			} catch (IOException e) {
				System.out.println("Can't load properties for " + f);
			}
			
			if(name == null) {
				name = "Unknown name";
			}
			if(author == null) {
				author = "Unknown author";
			}
			if(isPublic == null) {
				isPublic = "false";
			}
			
			add(uuid, author, name, isPublic.equals("true"));
		}
		
		isInitialized = true;
	}
	
	public void reInit(String path) {
		isInitialized = false;
		init(path);
	}
	
	public Map<UUID, ModelInfo> getAll() {
		return modelToInfoMap;
	}
}
