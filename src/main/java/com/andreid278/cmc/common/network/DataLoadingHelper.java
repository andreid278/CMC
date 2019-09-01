package com.andreid278.cmc.common.network;

import java.io.File;
import java.util.UUID;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.common.CMCData;

import net.minecraft.client.Minecraft;

public class DataLoadingHelper {
	public static DataLoadingHelper instance = new DataLoadingHelper();
	
	public static void sendDataToServer(UUID uuid, String author, String name, boolean isPublic) {
		MessageDataToServer message = new MessageDataToServer(uuid, author, name, isPublic);
		CMC.network.sendToServer(message);
			
		if(Minecraft.getMinecraft().isSingleplayer()) {
			System.out.println("Integrated server");
			return;
		}
		
		if(CMCData.instance.curLoadedToServerData == null) {
			CMCData.instance.curLoadedToServerData = CMCData.instance.new LoadedToServerData(uuid);
			System.out.println("Start of sending");
		}
	}
	
	public static void requestDataFromServer(UUID uuid) {
		if(Minecraft.getMinecraft().isSingleplayer()) {
			System.out.println("Integrated server");
			return;
		}
		
		if(CMCData.instance.dataIsLoadedFromServer) {
			return;
		}
		
		CMCData.instance.dataIsLoadedFromServer = true;
		MessageRequestDataFromServer message = new MessageRequestDataFromServer(uuid);
	}
	
	public static void requestData(UUID uuid) {
		if(ModelStorage.instance.hasModel(uuid)) {
			return;
		}
		
		File file = new File(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + uuid.toString() + "." + CMCData.instance.fileExt); 
		if(file.exists()) {
			return;
		}
		
		requestDataFromServer(uuid);
	}
	
	public static void sendDataToClient(UUID player, UUID uuid) {
		
	}
	
	public static void requestModelsInfo(int startIndex, int count) {
		MessageRequestModelsInfo message = new MessageRequestModelsInfo(startIndex, count);
		CMC.network.sendToServer(message);
	}
	
	public static void requestModelsCount() {
		MessageRequestModelsCount message = new MessageRequestModelsCount();
		CMC.network.sendToServer(message);
	}
}
