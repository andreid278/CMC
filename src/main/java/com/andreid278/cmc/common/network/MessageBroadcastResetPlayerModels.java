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

public class MessageBroadcastResetPlayerModels implements IMessage {
	
	public UUID uuid;
	
	public MessageBroadcastResetPlayerModels() {
		
	}
	
	public MessageBroadcastResetPlayerModels(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		long leastBits = buf.readLong();
		long mostBits = buf.readLong();
		uuid = new UUID(mostBits, leastBits);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
	}
	
	public static class Handler implements IMessageHandler<MessageBroadcastResetPlayerModels, IMessage> {

		@Override
		public IMessage onMessage(MessageBroadcastResetPlayerModels message, MessageContext ctx) {
			return CMC.proxy.onMessage(message, ctx);
		}
		
	}

}
