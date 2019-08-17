package com.andreid278.cmc.common.network;

import java.util.UUID;

import com.andreid278.cmc.common.CMCData;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestDataFromServer implements IMessage {
	
	public UUID uuid;
	
	public MessageRequestDataFromServer() {
		
	}
	
	public MessageRequestDataFromServer(UUID uuid) {
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
	
	public static class Handler implements IMessageHandler<MessageRequestDataFromServer, IMessage> {

		@Override
		public IMessage onMessage(MessageRequestDataFromServer message, MessageContext ctx) {
			if(!CMCData.instance.curLoadedToClientsData.containsKey(ctx.getServerHandler().player.getUniqueID())) {
				CMCData.instance.curLoadedToClientsData.put(ctx.getServerHandler().player.getUniqueID(), CMCData.instance.new LoadedToClientsData(message.uuid));
				System.out.println("Server : Start sending " + message.uuid.toString());
			}
			return null;
		}
		
	}

}
