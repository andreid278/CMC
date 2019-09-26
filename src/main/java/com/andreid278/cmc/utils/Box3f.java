package com.andreid278.cmc.utils;

import org.lwjgl.util.vector.Vector4f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;

public class Box3f {
	public Vec3f minCorner;
	public Vec3f maxCorner;
	
	public boolean isValid;
	
	public Box3f() {
		isValid = false;
	}
	
	public void reset() {
		isValid = false;
	}
	
	public void addPoint(Vec3f p) {
		if(!isValid) {
			minCorner = new Vec3f(p);
			maxCorner = new Vec3f(p);
			isValid = true;
		}
		else {
			minCorner.min(p);
			maxCorner.max(p);
		}
	}
	
	public void union(Box3f b) {
		if(!isValid) {
			minCorner = new Vec3f(b.minCorner);
			maxCorner = new Vec3f(b.maxCorner);
			isValid = true;
		}
		else {
			minCorner.min(b.minCorner);
			maxCorner.max(b.maxCorner);
		}
	}
	
	public void writeTo(ByteBuf buf) {
		buf.writeBoolean(isValid);
		if(isValid) {
			minCorner.writeTo(buf);
			maxCorner.writeTo(buf);
		}
	}
	
	public void readFrom(ByteBuf buf) {
		isValid = buf.readBoolean();
		if(isValid) {
			minCorner = new Vec3f(0, 0, 0);
			minCorner.readFrom(buf);
			maxCorner = new Vec3f(0, 0, 0);
			maxCorner.readFrom(buf);
		}
	}
	
	public float getCenterX() {
		return (minCorner.x + maxCorner.x) * 0.5f;
	}
	
	public float getCenterY() {
		return (minCorner.y + maxCorner.y) * 0.5f;
	}
	
	public float getCenterZ() {
		return (minCorner.z + maxCorner.z) * 0.5f;
	}
	
	public float getSizeX() {
		return maxCorner.x - minCorner.x;
	}
	
	public float getSizeY() {
		return maxCorner.y - minCorner.y;
	}
	
	public float getSizeZ() {
		return maxCorner.z - minCorner.z;
	}
	
	public float getSize() {
		return (float) Math.sqrt(getSizeX() * getSizeX() + getSizeY() * getSizeY() + getSizeZ() * getSizeZ());
	}
	
	public void transform(Box3f src, Matrix4f m) {
		reset();
		
		float x1 = src.minCorner.x;
		float y1 = src.minCorner.y;
		float z1 = src.minCorner.z;
		float x2 = src.maxCorner.x;
		float y2 = src.maxCorner.y;
		float z2 = src.maxCorner.z;
		
		Vector4f p = new Vector4f(0, 0, 0, 0);
		Vec3f pToAdd = new Vec3f(0, 0, 0);
		
		p.set(x1, y1, z1, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x1, y1, z2, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x1, y2, z1, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x1, y2, z2, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x2, y1, z1, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x2, y1, z2, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x2, y2, z1, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
		
		p.set(x2, y2, z2, 1);
		Matrix4f.transform(m, p, p);
		pToAdd.set(p.x, p.y, p.z);
		addPoint(pToAdd);
	}
}
