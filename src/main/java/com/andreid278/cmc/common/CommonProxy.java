package com.andreid278.cmc.common;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.gui.GuiHandler;
import com.andreid278.cmc.common.block.TestBlock;
import com.andreid278.cmc.common.event.PlayerEvents;
import com.andreid278.cmc.common.network.MessageBroadcastResetPlayerModels;
import com.andreid278.cmc.common.network.MessagePlayerLoggedIn;
import com.andreid278.cmc.common.network.MessageResponseModelsCount;
import com.andreid278.cmc.common.network.MessageResponseModelsInfo;
import com.andreid278.cmc.common.network.MessageResponsePlayerModels;
import com.andreid278.cmc.common.tileentity.TETest;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public static TestBlock testBlock;
	public static Item itemTestBlock;
	
	public void preInit(FMLPreInitializationEvent event) {
		testBlock = new TestBlock(Material.IRON);
		itemTestBlock = new ItemBlock(testBlock).setRegistryName(testBlock.getRegistryName());
		MinecraftForge.EVENT_BUS.register(new ItemBlockRegister());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
	}
	
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TETest.class, "tetest");
		NetworkRegistry.INSTANCE.registerGuiHandler(CMC.instance, new GuiHandler());
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	private class ItemBlockRegister {
		@SubscribeEvent
		public void registerBlocks(RegistryEvent.Register<Block> event) {
			event.getRegistry().registerAll(testBlock);
		}

		@SubscribeEvent
		public void registerItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().registerAll(itemTestBlock);
		}
		
		@SubscribeEvent
		public void registerModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(itemTestBlock, 0, new ModelResourceLocation(itemTestBlock.getRegistryName(), "inventory"));
		}
	}
	
	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageResponseModelsInfo message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageResponseModelsCount message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageResponsePlayerModels message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageBroadcastResetPlayerModels message, MessageContext ctx) {
		return null;
	}
}
