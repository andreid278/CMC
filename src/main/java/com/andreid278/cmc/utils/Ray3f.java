package com.andreid278.cmc.utils;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.Vec3d;

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
	
	public float intersectLine(Vec3f p1, Vec3f p2, float tolerance) {
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

		//System.out.println(sqrDist + " " + tolerance + " " + s0 + " " + s1);
		//sqrDist = Math.sqrt(Math.abs(sqrDist));
		if(sqrDist < tolerance) {
			return (float) s0;
		}
		else {
			return Float.MAX_VALUE;
		}
	}
	
	public float intersectPlane(Plane plane) {
		float denominator = Vec3f.dot(plane.normal, direction);
		if(Math.abs(denominator) < 1e-4) {
			if(Math.abs(plane.distanceToPoint(origin)) < 1e-4) {
				return 0;
			}
			return Float.MAX_VALUE;
		}

		float t = -(Vec3f.dot(origin, plane.normal) + plane.constant) / denominator;
		
		return t >= 0 ? t : Float.MAX_VALUE;
	}
}
