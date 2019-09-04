package com.andreid278.cmc.client.model;

import java.nio.FloatBuffer;
import java.util.UUID;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public class CMCModelOnPlayer {
	public UUID uuid;
	public Matrix4f location = new Matrix4f();
	public FloatBuffer locationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	
	public CMCModelOnPlayer(UUID uuid, Matrix4f location) {
		this.uuid = uuid;
		this.location = location;
		this.location.store(locationFloatBuffer);
	}
	
	public CMCModelOnPlayer(UUID uuid) {
		this.uuid = uuid;
		Matrix4f.setIdentity(this.location);
		this.location.store(locationFloatBuffer);
	}
}
