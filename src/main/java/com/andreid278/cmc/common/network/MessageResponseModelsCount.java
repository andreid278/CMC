package com.andreid278.cmc.common.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.andreid278.cmc.common.ModelsInfo.ModelInfo;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageResponseModelsCount implements IMessage {
	
	public int count;
	
	public MessageResponseModelsCount() {
		
	}
	
	public MessageResponseModelsCount(int count) {
		this.count = count;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		count = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
	}
	
	public static class Handler implements IMessageHandler<MessageResponseModelsCount, IMessage> {

		@Override
		public IMessage onMessage(MessageResponseModelsCount message, MessageContext ctx) {
			System.out.println("Response for models count");
			return CMC.proxy.onMessage(message, ctx);
		}
		
	}

}
