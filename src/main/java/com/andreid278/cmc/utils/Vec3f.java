package com.andreid278.cmc.utils;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;

public class Vec3f extends Vector3f {
	
	public Vec3f() {
		super(0, 0, 0);
	}
	
	public Vec3f(float x, float y, float z) {
		super(x, y, z);
	}
	
	public Vec3f(Vec3f p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public Vec3f copy(Vec3f p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		
		return this;
	}
	
	public void min(Vec3f p) {
		x = Math.min(x, p.x);
		y = Math.min(y, p.y);
		z = Math.min(z, p.z);
	}
	
	public void max(Vec3f p) {
		x = Math.max(x, p.x);
		y = Math.max(y, p.y);
		z = Math.max(z, p.z);
	}
	
	public void writeTo(ByteBuf buf) {
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
	}
	
	public void readFrom(ByteBuf buf) {
		x = buf.readFloat();
		y = buf.readFloat();
		z = buf.readFloat();
	}
	
	public void applyMatrix(Matrix4f m) {
		float prevX = x;
		float prevY = y;
		float prevZ = z;
		
		/*float w = 1.0f / (m.m30 * prevX + m.m31 * prevY + m.m32 * prevZ + m.m33);
		
		x = (m.m00 * prevX + m.m01 * prevY + m.m02 * prevZ + m.m03) * w;
		y = (m.m10 * prevX + m.m11 * prevY + m.m12 * prevZ + m.m13) * w;
		z = (m.m20 * prevX + m.m21 * prevY + m.m22 * prevZ + m.m23) * w;*/
		
		float w = 1.0f / (m.m03 * prevX + m.m13 * prevY + m.m23 * prevZ + m.m33);
		
		x = (m.m00 * prevX + m.m10 * prevY + m.m20 * prevZ + m.m30) * w;
		y = (m.m01 * prevX + m.m11 * prevY + m.m21 * prevZ + m.m31) * w;
		z = (m.m02 * prevX + m.m12 * prevY + m.m22 * prevZ + m.m32) * w;
	}
	
	public void transformDirection(Matrix4f m) {
		float prevX = x;
		float prevY = y;
		float prevZ = z;
		
		x = m.m00 * prevX + m.m10 * prevY + m.m20 * prevZ;
		y = m.m01 * prevX + m.m11 * prevY + m.m21 * prevZ;
		z = m.m02 * prevX + m.m12 * prevY + m.m22 * prevZ;
		
		this.normalise();
	}
	
	public void applyQuaternion(Quaternion q) {
		float prevX = x;
		float prevY = y;
		float prevZ = z;
		
		float ix =  q.w * prevX + q.y * prevZ - q.z * prevY;
		float iy =  q.w * prevY + q.z * prevX - q.x * prevZ;
		float iz =  q.w * prevZ + q.x * prevY - q.y * prevX;
		float iw = -q.x * prevX - q.y * prevY - q.z * prevZ;
		
		x = ix * q.w + iw * - q.x + iy * - q.z - iz * - q.y;
		y = iy * q.w + iw * - q.y + iz * - q.x - ix * - q.z;
		z = iz * q.w + iw * - q.z + ix * - q.y - iy * - q.x;
	}
	
	public Vec3f add(Vec3f v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	public Vec3f sub(Vec3f v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		
		return this;
	}
	
	public Vec3f mul(float a) {
		x *= a;
		y *= a;
		z *= a;
		
		return this;
	}
	
	public float distTo(Vec3f v) {
		return (float) Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
	}
	
	public void buildSpace(Vec3f v1, Vec3f v2) {
		v1.set(1, 0, 0);
		if(Math.abs(Vec3f.dot(this, v1)) > this.length() * 0.9999) {
			v1.set(0, 1, 0);
		}
		
		Vec3f.cross(this, v1, v2);
		v2.normalise();
		
		Vec3f.cross(this, v2, v1);
		v1.normalise();
	}
}
