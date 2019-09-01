package com.andreid278.cmc.client;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.gui.ModelsSelectionGui;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.ModelReader;
import com.andreid278.cmc.client.render.PlayerRenderer;
import com.andreid278.cmc.client.render.TETestRenderer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.CommonProxy;
import com.andreid278.cmc.common.network.MessagePlayerLoggedIn;
import com.andreid278.cmc.common.network.MessageResponseModelsCount;
import com.andreid278.cmc.common.network.MessageResponseModelsInfo;
import com.andreid278.cmc.common.tileentity.TETest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain(CMC.MODID);
		
		MinecraftForge.EVENT_BUS.register(new PlayerRenderer());

		super.preInit(event);
		
		//createResourcePack();
		
		String minecraftPath;
		try {
			minecraftPath = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
			minecraftPath = minecraftPath.replace("\\", "/");
			CMCData.instance.dataPathClient = minecraftPath + "/cmc/";
			System.out.println("dataPathClient = " + CMCData.instance.dataPathClient);
			File file = new File(CMCData.instance.dataPathClient);
			if (!file.isDirectory()) {
				file.mkdirs();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		ClientRegistry.bindTileEntitySpecialRenderer(TETest.class, new TETestRenderer());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	/*private void createResourcePack() {
		Class mc = Minecraft.getMinecraft().getClass();
		Field drp = null;
		try {
			drp = mc.getDeclaredField("defaultResourcePacks");
		} catch (NoSuchFieldException e) {
			try {
				drp = mc.getDeclaredField("field_110449_ao");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if (drp != null) {
			drp.setAccessible(true);
			List<IResourcePack> defaultResourcePacks;
			try {
				defaultResourcePacks = (List<IResourcePack>) drp.get(Minecraft.getMinecraft());
				String minecraftPath = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
				minecraftPath = minecraftPath.replace("\\", "/");
				CMCData.instance.dataPathClient = minecraftPath + "/cmc/assets/cmc/";
				System.out.println("dataPathClient = " + CMCData.instance.dataPathClient);
				File file = new File(minecraftPath + "/cmc");
				if (!file.isDirectory())
					file.mkdirs();
				File file1 = new File(minecraftPath + "/cmc/assets/cmc");
				if (!file1.isDirectory())
					file1.mkdirs();
				CustomFolderResourcePack frp = new CustomFolderResourcePack(file);
				defaultResourcePacks.add(frp);
				drp.set(Minecraft.getMinecraft(), defaultResourcePacks);
				//Minecraft.getMinecraft().refreshResources();
				//net.minecraftforge.fml.client.FMLClientHandler.instance().refreshResources(net.minecraftforge.client.resource.VanillaResourceType.TEXTURES);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/
	
	@Override
	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		if(!Minecraft.getMinecraft().isSingleplayer()) {
			try {
				String s = Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase();
				if(s.indexOf(':') > 0)
					s = s.replace(':', '-');
				CMCData.instance.curWorldPath = "multiplayer/" + s + "/";
				System.out.println("curWorldPath = " + CMCData.instance.curWorldPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		File file = new File(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath);
		if(!file.isDirectory())
			file.mkdirs();

		for(String f : file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("^[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}.(cmc)$");
			}
		})) {
			UUID uuid = UUID.fromString(f.substring(0, f.lastIndexOf(".")));
			ModelReader reader = new ModelReader(uuid, false);
			CMCModel model = reader.getModel();
			ModelStorage.instance.addModel(uuid, model);
			System.out.println(uuid.toString());
		}
		
		return null;
	}
	
	@Override
	public IMessage onMessage(MessageResponseModelsInfo message, MessageContext ctx) {
		if(Minecraft.getMinecraft().currentScreen instanceof ModelsSelectionGui) {
			ModelsSelectionGui gui = (ModelsSelectionGui) Minecraft.getMinecraft().currentScreen;
			gui.updateModelsInfo(message.info);
		}
		
		return null;
	}
	
	@Override
	public IMessage onMessage(MessageResponseModelsCount message, MessageContext ctx) {
		if(Minecraft.getMinecraft().currentScreen instanceof ModelsSelectionGui) {
			ModelsSelectionGui gui = (ModelsSelectionGui) Minecraft.getMinecraft().currentScreen;
			gui.updateModelsCount(message.count);
		}
		return null;
	}
}
