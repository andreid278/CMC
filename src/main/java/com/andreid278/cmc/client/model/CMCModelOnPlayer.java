package com.andreid278.cmc.client.model;

import java.nio.FloatBuffer;
import java.util.UUID;

import com.andreid278.cmc.client.model.CMCModelOnPlayer.BodyPart;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public class CMCModelOnPlayer {
	public UUID uuid;
	public Matrix4f location = new Matrix4f();
	public FloatBuffer locationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	// 0 - torso
	// 1 - head
	// 2 - left leg
	// 3 - right leg
	// 4 - left arm
	// 5 - right arm
	public BodyPart bodyPart = BodyPart.Torso;
	public enum BodyPart {
		Torso,
		Head,
		LeftLeg,
		RightLeg,
		LeftArm,
		RightArm
	}
	
	public CMCModelOnPlayer(UUID uuid, Matrix4f location, BodyPart bodyPart) {
		this.uuid = uuid;
		this.location = location;
		this.location.store(locationFloatBuffer);
		this.bodyPart = bodyPart;
	}
	
	public CMCModelOnPlayer(UUID uuid) {
		this.uuid = uuid;
		Matrix4f.setIdentity(this.location);
		this.location.store(locationFloatBuffer);
	}
	
	public void writeToBuf(ByteBuf buf) {
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
		
		buf.writeInt(bodyPart.ordinal());
	}
	
	public CMCModelOnPlayer readFromBuf(ByteBuf buf) {
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
		
		bodyPart = BodyPart.values()[buf.readInt()];
		
		return this;
	}
}
