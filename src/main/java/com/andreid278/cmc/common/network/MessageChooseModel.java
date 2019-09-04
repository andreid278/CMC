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

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.ModelsInfo;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChooseModel implements IMessage {
	
	public UUID uuid;
	Matrix4f location;
	
	public MessageChooseModel() {
		
	}
	
	public MessageChooseModel(UUID uuid, Matrix4f location) {
		this.uuid = uuid;
		this.location = location;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		long leastBits = buf.readLong();
		long mostBits = buf.readLong();
		uuid = new UUID(mostBits, leastBits);
		
		location = new Matrix4f();
		location.m00 = buf.readFloat();
		location.m01 = buf.readFloat();
		location.m02 = buf.readFloat();
		location.m03 = buf.readFloat();
		location.m10 = buf.readFloat();
		location.m11 = buf.readFloat();
		location.m12 = buf.readFloat();
		location.m13 = buf.readFloat();
		location.m20 = buf.readFloat();
		location.m21 = buf.readFloat();
		location.m22 = buf.readFloat();
		location.m23 = buf.readFloat();
		location.m30 = buf.readFloat();
		location.m31 = buf.readFloat();
		location.m32 = buf.readFloat();
		location.m33 = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		
		buf.writeFloat(location.m00);
		buf.writeFloat(location.m01);
		buf.writeFloat(location.m02);
		buf.writeFloat(location.m03);
		buf.writeFloat(location.m10);
		buf.writeFloat(location.m11);
		buf.writeFloat(location.m12);
		buf.writeFloat(location.m13);
		buf.writeFloat(location.m20);
		buf.writeFloat(location.m21);
		buf.writeFloat(location.m22);
		buf.writeFloat(location.m23);
		buf.writeFloat(location.m30);
		buf.writeFloat(location.m31);
		buf.writeFloat(location.m32);
		buf.writeFloat(location.m33);
	}
	
	public static class Handler implements IMessageHandler<MessageChooseModel, IMessage> {

		@Override
		public IMessage onMessage(MessageChooseModel message, MessageContext ctx) {
			MessageBroadcastResetPlayerModels response = new MessageBroadcastResetPlayerModels(message.uuid);
			CMC.network.sendToAll(response);
			return null;
		}
		
	}

}
