package com.andreid278.cmc.common.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestModelsInfo implements IMessage {
	
	public MessageRequestModelsInfo() {
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public static class Handler implements IMessageHandler<MessageRequestModelsInfo, IMessage> {

		@Override
		public IMessage onMessage(MessageRequestModelsInfo message, MessageContext ctx) {
			System.out.println("Request for models info");
			MessageResponseModelsInfo response = new MessageResponseModelsInfo(ModelsInfo.instance.getAll());
			return response;
		}
		
	}

}
