package com.andreid278.cmc.common.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestPlayerModels implements IMessage {
	
	public UUID uuid;
	
	public MessageRequestPlayerModels() {
		
	}
	
	public MessageRequestPlayerModels(UUID uuid) {
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
	
	public static class Handler implements IMessageHandler<MessageRequestPlayerModels, IMessage> {

		@Override
		public IMessage onMessage(MessageRequestPlayerModels message, MessageContext ctx) {
			System.out.println("Request for player models");
			List<CMCModelOnPlayer> models;
			if(CMCData.instance.playersModels.containsKey(message.uuid)) {
				models = CMCData.instance.playersModels.get(message.uuid);
			}
			else {
				models = new ArrayList<CMCModelOnPlayer>();
			}
			MessageResponsePlayerModels response = new MessageResponsePlayerModels(message.uuid, models);
			return response;
		}
		
	}

}
