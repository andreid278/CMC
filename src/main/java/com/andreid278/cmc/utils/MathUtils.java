package com.andreid278.cmc.utils;

import io.netty.buffer.ByteBuf;

public class MathUtils {
	public static MathUtils instance = new MathUtils();
	
	public class Vec3f {
		public float x;
		public float y;
		public float z;
		
		public Vec3f(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Vec3f(Vec3f p) {
			this.x = p.x;
			this.y = p.y;
			this.z = p.z;
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
	}
	
	public class Vec3i {
		public int x;
		public int y;
		public int z;
		
		public Vec3i(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public class Vec2f {
		public float x;
		public float y;
		
		public Vec2f(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public class Box3f {
		Vec3f minCorner;
		Vec3f maxCorner;
		
		boolean isValid;
		
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
	}
}
