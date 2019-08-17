package com.andreid278.cmc.common.network;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.andreid278.cmc.common.CMCData;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDataToClient implements IMessage {
	public UUID uuid;
	public int messageID;
	public byte[] data;
	public int dataSize;
	public int dataPartSize;
	
	public MessageDataToClient() {
		
	}
	
	public MessageDataToClient(UUID uuid, int messageID, int dataSize, byte[] data, int dataPartSize) {
		this.uuid = uuid;
		this.messageID = messageID;
		this.dataSize = dataSize;
		this.data = data;
		this.dataPartSize = dataPartSize;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		long leastBits = buf.readLong();
		long mostBits = buf.readLong();
		uuid = new UUID(mostBits, leastBits);
		messageID = buf.readInt();
		dataSize = buf.readInt();
		dataPartSize = buf.readInt();
		data = new byte[dataPartSize];
		buf.readBytes(data);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeInt(messageID);
		buf.writeInt(dataSize);
		buf.writeInt(dataPartSize);
		buf.writeBytes(data);
	}
	
	public static class Handler implements IMessageHandler<MessageDataToClient, IMessage> {

		@Override
		public IMessage onMessage(MessageDataToClient message, MessageContext ctx) {
			if(CMCData.instance.curLoadedFromServerData == null) {
				CMCData.instance.curLoadedFromServerData = CMCData.instance.new LoadedFromServerData(message.dataSize);
			}
			
			CMCData.LoadedFromServerData data = CMCData.instance.curLoadedFromServerData;
			
			for(int i = 0; i < message.dataPartSize; i++) {
				data.data[message.messageID * CMCData.instance.maxLoadedSize + i] = message.data[i];
			}
			
			data.partsSize += message.dataPartSize;
			System.out.println("Client : Recieve " + message.messageID);
			if(data.partsSize >= data.data.length) {
				FileOutputStream stream;
				try {
					stream = new FileOutputStream(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + message.uuid.toString() + "." + CMCData.instance.fileExt);
					stream.write(data.data);
					stream.flush();
					stream.close();
					CMCData.instance.curLoadedFromServerData = null;
					CMCData.instance.dataIsLoadedFromServer = false;
					System.out.println("Client : End of recieving");
					System.out.println("Client : Saved to " + CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + message.uuid.toString() + "." + CMCData.instance.fileExt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Can't save file!!!");
					return null;
				}
			}
			
			return null;
		}
		
	}
}
