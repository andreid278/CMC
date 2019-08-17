package com.andreid278.cmc.client.model;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public class CMCModelOnPlayer {
	public CMCModel model;
	public Matrix4f location = new Matrix4f();
	public FloatBuffer locationFloatBuffer = GLAllocation.createDirectFloatBuffer(16);
	
	public CMCModelOnPlayer(CMCModel model, Matrix4f location) {
		this.model = model;
		this.location = location;
		this.location.store(locationFloatBuffer);
	}
	
	public CMCModelOnPlayer(CMCModel model) {
		this.model = model;
		Matrix4f.setIdentity(this.location);
		this.location.store(locationFloatBuffer);
	}
}
