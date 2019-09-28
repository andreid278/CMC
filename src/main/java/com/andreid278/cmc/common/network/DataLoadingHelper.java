package com.andreid278.cmc.common.network;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.client.model.CMCModelOnPlayer.BodyPart;
import com.andreid278.cmc.common.CMCData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;

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
	
	public static void requestModelsInfo(int startIndex, int count) {
		MessageRequestModelsInfo message = new MessageRequestModelsInfo(startIndex, count);
		CMC.network.sendToServer(message);
	}
	
	public static void requestModelsCount() {
		MessageRequestModelsCount message = new MessageRequestModelsCount();
		CMC.network.sendToServer(message);
	}
	
	public static void requestPlayerModels(UUID uuid) {
		CMCData.instance.playersModels.put(uuid, new ArrayList<CMCModelOnPlayer>());
		MessageRequestPlayerModels message = new MessageRequestPlayerModels(uuid);
		CMC.network.sendToServer(message);
	}
	
	public static void chooseModel(UUID uuid, Matrix4f location, BodyPart bodyPart) {
		UUID playerUUID = Minecraft.getMinecraft().player.getUniqueID();
		if(!CMCData.instance.playersModels.containsKey(playerUUID)) {
			System.out.println("chooseModel error");
			return;
		}
		
		List<CMCModelOnPlayer> list = CMCData.instance.playersModels.get(playerUUID);
		if(list == null) {
			list = new ArrayList<>();
			CMCData.instance.playersModels.put(playerUUID, list);
		}
		
		for(Iterator<CMCModelOnPlayer> it = list.iterator(); it.hasNext(); ) {
			if(it.next().uuid.equals(uuid)) {
				it.remove();
				break;
			}
		}
		list.add(new CMCModelOnPlayer(uuid, location, bodyPart));
		
		if(Minecraft.getMinecraft().isSingleplayer()) {
			return;
		}
		MessageChooseModel message = new MessageChooseModel(uuid, location, bodyPart);
		CMC.network.sendToServer(message);
	}
}
