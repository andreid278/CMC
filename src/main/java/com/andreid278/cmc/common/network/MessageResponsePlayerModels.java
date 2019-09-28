package com.andreid278.cmc.common.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.client.model.CMCModelOnPlayer.BodyPart;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.andreid278.cmc.common.ModelsInfo.ModelInfo;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageResponsePlayerModels implements IMessage {
	
	public UUID uuid;
	public List<CMCModelOnPlayer> models;
	
	public MessageResponsePlayerModels() {
		
	}
	
	public MessageResponsePlayerModels(UUID uuid, List<CMCModelOnPlayer> models) {
		this.uuid = uuid;
		this.models = models;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		long leastBits = buf.readLong();
		long mostBits = buf.readLong();
		uuid = new UUID(mostBits, leastBits);
		
		int s = buf.readInt();
		
		models = new ArrayList<CMCModelOnPlayer>();
		
		for(int i = 0; i < s; i++) {
			models.add(new CMCModelOnPlayer(null).readFromBuf(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		
		buf.writeInt(models.size());
		
		Iterator<CMCModelOnPlayer> it = models.iterator();
		
		while(it.hasNext()) {
			CMCModelOnPlayer entry = it.next();
			entry.writeToBuf(buf);
		}
	}
	
	public static class Handler implements IMessageHandler<MessageResponsePlayerModels, IMessage> {

		@Override
		public IMessage onMessage(MessageResponsePlayerModels message, MessageContext ctx) {
			System.out.println("Response for player models");
			return CMC.proxy.onMessage(message, ctx);
		}
		
	}

}
