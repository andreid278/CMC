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

public class MessageDataToServer implements IMessage {
	
	public UUID uuid;
	public int messageID;
	public byte[] data;
	public int dataSize;
	public int dataPartSize;
	String author;
	String name;
	boolean isPublic;
	
	public MessageDataToServer() {
		
	}
	
	public MessageDataToServer(UUID uuid, String author, String name, boolean isPublic) {
		this.uuid = uuid;
		this.author = author;
		this.name = name;
		this.isPublic = isPublic;
		messageID = -1;
	}
	
	public MessageDataToServer(UUID uuid, int messageID, int dataSize, byte[] data, int dataPartSize) {
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
		if(messageID == -1) {
			int l = buf.readInt();
			author = buf.readCharSequence(l, Charsets.UTF_8).toString();
			l = buf.readInt();
			name = buf.readCharSequence(l, Charsets.UTF_8).toString();
			isPublic = buf.readBoolean();
		}
		else {
			dataSize = buf.readInt();
			dataPartSize = buf.readInt();
			data = new byte[dataPartSize];
			buf.readBytes(data);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeInt(messageID);
		if(messageID == -1) {
			buf.writeInt(author.length());
			buf.writeCharSequence(author, Charsets.UTF_8);
			buf.writeInt(name.length());
			buf.writeCharSequence(name, Charsets.UTF_8);
			buf.writeBoolean(isPublic);
		}
		else {
			buf.writeInt(dataSize);
			buf.writeInt(dataPartSize);
			buf.writeBytes(data);
		}
	}
	
	public static class Handler implements IMessageHandler<MessageDataToServer, IMessage> {

		@Override
		public IMessage onMessage(MessageDataToServer message, MessageContext ctx) {
			if(message.messageID == -1) {
				try {
					FileWriter output = new FileWriter(CMCData.instance.dataPathServer + message.uuid.toString() + ".properties");
					Properties prop = new Properties();
					prop.setProperty("author", message.author);
					prop.setProperty("name", message.name);
					prop.setProperty("isPublic", message.isPublic ? "true" : "false");
					prop.store(output, null);
					output.close();
					ModelsInfo.instance.add(message.uuid, message.author, message.name, message.isPublic);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Can't save property file!!!");
					return null;
				}
			}
			else {
				if(!CMCData.instance.curLoadedFromClientsData.containsKey(message.uuid)) {
					CMCData.instance.curLoadedFromClientsData.put(message.uuid, CMCData.instance.new LoadedFromClientsData(message.dataSize));
				}
				CMCData.LoadedFromClientsData data = CMCData.instance.curLoadedFromClientsData.get(message.uuid);
				for(int i = 0; i < message.dataPartSize; i++) {
					data.data[message.messageID * CMCData.instance.maxLoadedSize + i] = message.data[i];
				}
				data.partsSize += message.dataPartSize;
				System.out.println("Server : Recieve " + message.messageID);
				if(data.partsSize >= data.data.length) {
					FileOutputStream stream;
					try {
						stream = new FileOutputStream(CMCData.instance.dataPathServer + message.uuid.toString() + "." + CMCData.instance.fileExt);
						stream.write(data.data);
						stream.flush();
						stream.close();
						CMCData.instance.curLoadedFromClientsData.remove(message.uuid);
						System.out.println("Server : End of recieving");
						System.out.println("Server : Saved to " + CMCData.instance.dataPathServer + message.uuid.toString() + "." + CMCData.instance.fileExt);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Can't save file!!!");
						return null;
					}
				}
			}
			return null;
		}
		
	}

}
