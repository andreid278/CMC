package com.andreid278.cmc.utils;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
	public static MathUtils instance = new MathUtils();
	
	public class Vec3f extends Vector3f {
		public Vec3f(float x, float y, float z) {
			super(x, y, z);
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
		
		public void applyMatrix(Matrix4f m) {
			float prevX = x;
			float prevY = y;
			float prevZ = z;
			
			float w = 1.0f / (m.m30 * prevX + m.m31 * prevY + m.m32 * prevZ + m.m33);
			
			x = (m.m00 * prevX + m.m01 * prevY + m.m02 * prevZ + m.m03) * w;
			y = (m.m10 * prevX + m.m11 * prevY + m.m12 * prevZ + m.m13) * w;
			z = (m.m20 * prevX + m.m21 * prevY + m.m22 * prevZ + m.m23) * w;
		}
		
		public void transformDirection(Matrix4f m) {
			float prevX = x;
			float prevY = y;
			float prevZ = z;
			
			x = m.m00 * prevX + m.m01 * prevY + m.m02 * prevZ;
			y = m.m10 * prevX + m.m11 * prevY + m.m12 * prevZ;
			z = m.m20 * prevX + m.m21 * prevY + m.m22 * prevZ;
			
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
	
	public class Ray3f {
		public Vec3f origin = new Vec3f(0, 0, 0);
		public Vec3f direction = new Vec3f(1, 0, 0);
		
		public Ray3f(float Ox, float Oy, float Oz, float Dx, float Dy, float Dz) {
			origin.set(Ox, Oy, Oz);
			direction.set(Dx, Dy, Dz);
		}
		
		public void setOrigin(float Ox, float Oy, float Oz) {
			origin.set(Ox, Oy, Oz);
		}
		
		public void setDirection(float Dx, float Dy, float Dz) {
			direction.set(Dx, Dy, Dz);
		}
		
		public void transform(Matrix4f m) {
			origin.applyMatrix(m);
			//origin.transformDirection(m);
			direction.transformDirection(m);
		}
		
		public boolean intersectLine(Vec3f p1, Vec3f p2, float tolerance) {
			Vec3d segCenter = new Vec3d((p1.x + p2.x) * 0.5f, (p1.y + p2.y) * 0.5f, (p1.z + p2.z) * 0.5f);
			float l = p2.distTo(p1);
			if(l > 1e-4) l = 1.0f / l;
			Vec3d segDir = new Vec3d((p2.x - p1.x) * l, (p2.y - p1.y) * l, (p2.z - p1.z) * l);
			Vec3d diff = new Vec3d(origin.x - segCenter.x, origin.y - segCenter.y, origin.z - segCenter.z);

			float segExtent = p1.distTo(p2) * 0.5f;
			//float a01 = -Vec3d.dot(direction, segDir);
			double a01 = -(direction.x * segDir.x + direction.y * segDir.y + direction.z * segDir.z);
			//float b0 = Vec3f.dot(diff, direction);
			double b0 = direction.x * diff.x + direction.y * diff.y + direction.z * diff.z;
			//float b1 = -Vec3f.dot(diff, segDir);
			double b1 = -(diff.x * segDir.x + diff.y * segDir.y + diff.z * segDir.z);
			double c = diff.lengthSquared();
			double det = Math.abs( 1 - a01 * a01 );
			double s0, s1, sqrDist, extDet;

			if(det > 0) {
				s0 = a01 * b1 - b0;
				s1 = a01 * b0 - b1;
				extDet = segExtent * det;
				
				if(s0 >= 0) {
					if(s1 >= -extDet) {
						if(s1 <= extDet) {
							double invDet = 1 / det;
							s0 *= invDet;
							s1 *= invDet;
							sqrDist = s0 * (s0 + a01 * s1 + 2 * b0) + s1 * (a01 * s0 + s1 + 2 * b1) + c;
						}
						else {
							s1 = segExtent;
							s0 = Math.max(0, -(a01 * s1 + b0));
							sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c;
						}
					}
					else {
						s1 = -segExtent;
						s0 = Math.max(0, -(a01 * s1 + b0));
						sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c;
					}
				}
				else {
					if(s1 <= - extDet) {
						s0 = Math.max(0, -(-a01 * segExtent + b0));
						s1 = (s0 > 0) ? -segExtent : Math.min(Math.max(-segExtent, -b1), segExtent);
						sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c;
					}
					else if(s1 <= extDet) {
						s0 = 0;
						s1 = Math.min(Math.max(-segExtent, -b1), segExtent);
						sqrDist = s1 * (s1 + 2 * b1) + c;
					}
					else {
						s0 = Math.max(0, -(a01 * segExtent + b0));
						s1 = (s0 > 0) ? segExtent : Math.min(Math.max(-segExtent, -b1), segExtent);
						sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c;
					}
				}
			}
			else {
				s1 = (a01 > 0) ? -segExtent : segExtent;
				s0 = Math.max(0, -(a01 * s1 + b0));
				sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c;
			}

			Vec3d pointOnRay = new Vec3d(direction.x * s0 + origin.x, direction.y * s0 + origin.y, direction.z * s0 + origin.z);
			//pointOnRay.addVector(direction.x * s0 + origin.x, direction.y * s0 + origin.y, direction.z * s0 + origin.z);
			Vec3d pointOnSegment = new Vec3d(segDir.x * s1 + segCenter.x, segDir.y * s1 + segCenter.y, segDir.z * s1 + segCenter.z);
			//pointOnSegment.addVector(segDir.x * s1 + segCenter.x, segDir.y * s1 + segCenter.y, segDir.z * s1 + segCenter.z);
			sqrDist = pointOnRay.distanceTo(pointOnSegment);
			
			/*if ( optionalPointOnRay ) {

				optionalPointOnRay.copy( this.direction ).multiplyScalar( s0 ).add( this.origin );

			}

			if ( optionalPointOnSegment ) {

				optionalPointOnSegment.copy( _segDir ).multiplyScalar( s1 ).add( _segCenter );

			}*/

			System.out.println(sqrDist + " " + tolerance + " " + s0 + " " + s1);
			//sqrDist = Math.sqrt(Math.abs(sqrDist));
			return sqrDist < tolerance;
		}
	}
}
