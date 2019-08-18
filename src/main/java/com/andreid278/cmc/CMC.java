package com.andreid278.cmc;

import com.andreid278.cmc.common.CommonProxy;
import com.andreid278.cmc.common.network.MessageDataToClient;
import com.andreid278.cmc.common.network.MessageDataToServer;
import com.andreid278.cmc.common.network.MessagePlayerLoggedIn;
import com.andreid278.cmc.common.network.MessageRequestDataFromServer;
import com.andreid278.cmc.common.network.MessageRequestModelsInfo;
import com.andreid278.cmc.common.network.MessageResponseModelsInfo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CMC.MODID, name = CMC.NAME, version = CMC.VERSION)
public class CMC {
	public static final String MODID = "cmc";
    public static final String NAME = "Custom Models Creator";
    public static final String VERSION = "0.1";
    
    @SidedProxy(clientSide = "com.andreid278.cmc.client.ClientProxy", serverSide = "com.andreid278.cmc.common.CommonProxy")
	public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper network;
    
    @Instance("cmc")
	public static CMC instance;

    @Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("cmc");
		network.registerMessage(MessagePlayerLoggedIn.Handler.class, MessagePlayerLoggedIn.class, 0, Side.CLIENT);
		network.registerMessage(MessageDataToServer.Handler.class, MessageDataToServer.class, 1, Side.SERVER);
		network.registerMessage(MessageRequestDataFromServer.Handler.class, MessageRequestDataFromServer.class, 2, Side.SERVER);
		network.registerMessage(MessageDataToClient.Handler.class, MessageDataToClient.class, 3, Side.CLIENT);
		network.registerMessage(MessageRequestModelsInfo.Handler.class, MessageRequestModelsInfo.class, 4, Side.SERVER);
		network.registerMessage(MessageResponseModelsInfo.Handler.class, MessageResponseModelsInfo.class, 5, Side.CLIENT);
		/*network.registerMessage(MessageRequestForPhoto.Handler.class, MessageRequestForPhoto.class, 6, Side.SERVER);
		network.registerMessage(PhotoLoaderToClient.Handler.class, PhotoLoaderToClient.class, 7, Side.CLIENT);
		network.registerMessage(MessageRequestNoPhoto.Handler.class, MessageRequestNoPhoto.class, 8, Side.CLIENT);
		network.registerMessage(MessagePrinterToClient.Handler.class, MessagePrinterToClient.class, 9, Side.CLIENT);
		network.registerMessage(MessagePrinterToServer.Handler.class, MessagePrinterToServer.class, 10, Side.SERVER);
		network.registerMessage(MessagePainterToClient.Handler.class, MessagePainterToClient.class, 11, Side.CLIENT);
		network.registerMessage(MessagePainterToServer.Handler.class, MessagePainterToServer.class, 12, Side.SERVER);
		network.registerMessage(MessageDeletePhotoRequest.Handler.class, MessageDeletePhotoRequest.class, 13, Side.SERVER);
		network.registerMessage(MessageDeletePhotoToClients.Handler.class, MessageDeletePhotoToClients.class, 14, Side.CLIENT);*/
		proxy.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
