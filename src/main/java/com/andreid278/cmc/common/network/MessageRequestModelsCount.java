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

public class MessageRequestModelsCount implements IMessage {
	
	public MessageRequestModelsCount() {
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public static class Handler implements IMessageHandler<MessageRequestModelsCount, IMessage> {

		@Override
		public IMessage onMessage(MessageRequestModelsCount message, MessageContext ctx) {
			System.out.println("Request for models count");
			MessageResponseModelsCount response = new MessageResponseModelsCount(ModelsInfo.instance.getCount());
			return response;
		}
		
	}

}
