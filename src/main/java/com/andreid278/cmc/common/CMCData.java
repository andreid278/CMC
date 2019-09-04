package com.andreid278.cmc.common;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.google.common.io.Files;

public class CMCData {
	public static CMCData instance = new CMCData();
	
	public static final int version = 1;
	
	public final String fileExt = "cmc";
	
	public String dataPathClient = "";
	public String curWorldPath = "";
	public String dataPathServer = "";
	
	public Map<UUID, List<CMCModelOnPlayer>> playersModels = new HashMap<>();
	
	public final int maxLoadedSize = 300;
	
	// Data loaded to server, client info
	public class LoadedToServerData {
		public UUID uuid = null;
		public byte[] data = null;
		public int offset = 0;
		
		public LoadedToServerData(UUID uuid) {
			File file = new File(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + uuid.toString() + "." + fileExt);
			if(file.exists()) {
				try {
					this.uuid = uuid;
					data = Files.toByteArray(file);
					offset = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Error1");
			}
		}
	}
	public LoadedToServerData curLoadedToServerData = null;
	
	// Data loaded to client, client info
	public class LoadedFromServerData {
		public byte[] data = null;
		public int partsSize = 0;
		
		public LoadedFromServerData(int size) {
			data = new byte[size];
			partsSize = 0;
		}
	}
	public LoadedFromServerData curLoadedFromServerData = null;
	public boolean dataIsLoadedFromServer = false;
	
	// Data loaded to server, server info
	public class LoadedFromClientsData {
		public byte[] data = null;
		public int partsSize = 0;
		
		public LoadedFromClientsData(int size) {
			data = new byte[size];
			partsSize = 0;
		}
	}
	public Map<UUID, LoadedFromClientsData> curLoadedFromClientsData = new HashMap<>();
	
	// Data loaded to clients, server info
	public class LoadedToClientsData {
		public UUID uuid;
		public byte[] data = null;
		public int offset = 0;
		
		public LoadedToClientsData(UUID uuid) {
			File file = new File(CMCData.instance.dataPathServer + uuid.toString() + "." + fileExt);
			if(file.exists()) {
				try {
					this.uuid = uuid;
					data = Files.toByteArray(file);
					offset = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Error2");
			}
		}
	}
	public Map<UUID, LoadedToClientsData> curLoadedToClientsData = new HashMap<>();
}
