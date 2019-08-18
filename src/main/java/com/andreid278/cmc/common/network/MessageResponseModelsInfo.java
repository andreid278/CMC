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

public class MessageResponseModelsInfo implements IMessage {
	
	public Map<UUID, ModelInfo> info;
	
	public MessageResponseModelsInfo() {
		
	}
	
	public MessageResponseModelsInfo(Map<UUID, ModelInfo> info) {
		this.info = info;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		info = new LinkedHashMap<UUID, ModelsInfo.ModelInfo>();
		
		int s = buf.readInt();
		
		for(int i = 0; i < s; i++) {
			long leastBits = buf.readLong();
			long mostBits = buf.readLong();
			UUID uuid = new UUID(mostBits, leastBits);
			
			ModelInfo modelInfo = ModelsInfo.instance.new ModelInfo();
			modelInfo.readFrom(buf);
			
			info.put(uuid, modelInfo);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(info.size());
		
		Set<Entry<UUID, ModelInfo>> entrySet = info.entrySet();
		Iterator<Entry<UUID, ModelInfo>> it = entrySet.iterator();
		
		while(it.hasNext()) {
			Entry<UUID, ModelInfo> entry = it.next();
			
			buf.writeLong(entry.getKey().getLeastSignificantBits());
			buf.writeLong(entry.getKey().getMostSignificantBits());
			
			entry.getValue().writeTo(buf);
		}
	}
	
	public static class Handler implements IMessageHandler<MessageResponseModelsInfo, IMessage> {

		@Override
		public IMessage onMessage(MessageResponseModelsInfo message, MessageContext ctx) {
			System.out.println("Response for models info");
			return CMC.proxy.onMessage(message, ctx);
		}
		
	}

}
