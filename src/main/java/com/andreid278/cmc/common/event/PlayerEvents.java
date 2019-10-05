package com.andreid278.cmc.common.event;

import java.io.File;
import java.io.FilenameFilter;
import java.util.UUID;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.andreid278.cmc.common.network.MessageDataToClient;
import com.andreid278.cmc.common.network.MessageDataToServer;
import com.andreid278.cmc.common.network.MessagePlayerLoggedIn;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEvents {
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if(!event.player.world.getMinecraftServer().isDedicatedServer()) {
			try {
				String s = event.player.world.getSaveHandler().getWorldDirectory().getCanonicalPath();
				s = s.replace("\\", "/");
				CMCData.instance.curWorldPath = "singleplayer/" + s.substring(s.lastIndexOf("/") + 1) + "/";
				CMCData.instance.dataPathServer = CMCData.instance.dataPathClient + CMCData.instance.curWorldPath;
				File file = new File(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath);
				System.out.println("curWorldPath = " + CMCData.instance.curWorldPath);
				if(!file.isDirectory())
					file.mkdirs();
				System.out.println("dataPathServer = " + CMCData.instance.dataPathServer);
				
				ModelsInfo.instance.reInit(CMCData.instance.dataPathServer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			if(CMCData.instance.dataPathServer == "") {
				try {
					CMCData.instance.dataPathServer = event.player.world.getMinecraftServer().getDataDirectory().getCanonicalPath().replace("\\", "/").toLowerCase() + "/cmc/";
					File file = new File(CMCData.instance.dataPathServer);
					if(!file.isDirectory())
						file.mkdirs();
					System.out.println("dataPathServer = " + CMCData.instance.dataPathServer);
					
					ModelsInfo.instance.init(CMCData.instance.dataPathServer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		MessagePlayerLoggedIn message = new MessagePlayerLoggedIn();
		CMC.network.sendTo(message, (EntityPlayerMP)event.player);
	}
	
	@SubscribeEvent
	public void updatePlayer(PlayerTickEvent event) {
		if(event.player.world.isRemote) {
			if(CMCData.instance.curLoadedToServerData != null) {
				if(event.player.ticksExisted % 10 == 0) {
					int partSize = Math.min(CMCData.instance.maxLoadedSize, CMCData.instance.curLoadedToServerData.data.length - CMCData.instance.curLoadedToServerData.offset * CMCData.instance.maxLoadedSize);
					byte[] data = new byte[partSize];
					for(int i = 0; i < partSize; i++) {
						data[i] = CMCData.instance.curLoadedToServerData.data[CMCData.instance.curLoadedToServerData.offset * CMCData.instance.maxLoadedSize + i];
					}
					MessageDataToServer message = new MessageDataToServer(CMCData.instance.curLoadedToServerData.uuid,
							CMCData.instance.curLoadedToServerData.offset,
							CMCData.instance.curLoadedToServerData.data.length,
							data,
							partSize);
					CMC.network.sendToServer(message);
					System.out.println("Client : Send " + CMCData.instance.curLoadedToServerData.offset);
					CMCData.instance.curLoadedToServerData.offset++;
					if(CMCData.instance.curLoadedToServerData.offset * CMCData.instance.maxLoadedSize >= CMCData.instance.curLoadedToServerData.data.length) {
						CMCData.instance.curLoadedToServerData = null;
						System.out.println("Client : End of sending");
					}
				}
			}
		}
		else {
			if(CMCData.instance.curLoadedToClientsData.containsKey(event.player.getUniqueID())) {
				if(event.player.ticksExisted % 10 == 0) {
					CMCData.LoadedToClientsData data = CMCData.instance.curLoadedToClientsData.get(event.player.getUniqueID());
					int partSize = Math.min(CMCData.instance.maxLoadedSize, data.data.length - data.offset * CMCData.instance.maxLoadedSize);
					byte[] dataToTransfer = new byte[partSize];
					for(int i = 0; i < partSize; i++) {
						dataToTransfer[i] = data.data[data.offset * CMCData.instance.maxLoadedSize + i];
					}
					MessageDataToClient message = new MessageDataToClient(data.uuid,
							data.offset,
							data.data.length,
							dataToTransfer,
							partSize);
					CMC.network.sendTo(message, (EntityPlayerMP)event.player);
					System.out.println("Server : Send " + data.offset);
					data.offset++;
					if(data.offset * CMCData.instance.maxLoadedSize >= data.data.length) {
						CMCData.instance.curLoadedToClientsData.remove(event.player.getUniqueID());
						System.out.println("Server : End of sending");
					}
				}
			}
		}
	}
}
