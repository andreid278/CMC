package com.andreid278.cmc.utils;

import com.andreid278.cmc.client.gui.viewer.MovableObject;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.MaterialGroup;

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
	
	public float intersectCircle(Vec3f center, Vec3f normal, float rad, float tolerance, boolean toCheckOnlyBoundaries) {
		Plane plane = new Plane(normal, center);
		
		float t = intersectPlane(plane);
		
		if(t == Float.MAX_VALUE) {
			return t;
		}
		
		Vec3f p = new Vec3f(direction).mul(t).add(origin);
		
		float distToCenter = p.distTo(center);
		
		if(toCheckOnlyBoundaries && distToCenter > rad - tolerance && distToCenter < rad + tolerance || !toCheckOnlyBoundaries && distToCenter < rad) {
			return t;
		}
		
		return Float.MAX_VALUE;
	}
	
	public float intersectTriangle(Vec3f p1, Vec3f p2, Vec3f p3) {
		Vec3f v1 = new Vec3f(p2).sub(p1);
		Vec3f v2 = new Vec3f(p3).sub(p1);
		Vec3f pVec = new Vec3f();
		Vec3f.cross(direction, v2, pVec);
		float det = Vec3f.dot(v1, pVec);
		
		if (det < 1e-8 && det > -1e-8) {
			return Float.MAX_VALUE;
		}
		
		float invDet = 1.0f / det;
		Vec3f tVec = new Vec3f(origin).sub(p1);
		float u = Vec3f.dot(pVec, tVec) * invDet;
		if(u < 0 || u > 1) {
			return Float.MAX_VALUE;
		}
		
		Vec3f qVec = new Vec3f();
		Vec3f.cross(tVec, v1, qVec);
		float v = Vec3f.dot(direction, qVec) * invDet;
		if(v < 0 || v + u > 1) {
			return Float.MAX_VALUE;
		}
		
		return Vec3f.dot(v2, qVec) * invDet;
	}
	
	public float intersectCMCModel(CMCModel model, IntersectionData intersectionData) {
		float res = Float.MAX_VALUE;
		
		Vec3i triangle = new Vec3i(0, 0, 0);
		Vec3f p1 = new Vec3f();
		Vec3f p2 = new Vec3f();
		Vec3f p3 = new Vec3f();
		
		intersectionData.triangle = new Vec3i(triangle);
		
		for(MaterialGroup material : model.materials) {
			int trianglesNum = material.trianglesNum();
			for(int i = 0; i < trianglesNum; i++) {
				material.getTriangle(i, triangle);
				material.getVertex(triangle.x, p1);
				material.getVertex(triangle.y, p2);
				material.getVertex(triangle.z, p3);
				
				float triangleIntersection = intersectTriangle(p1, p2, p3);
				
				if(triangleIntersection < res) {
					res = triangleIntersection;
					intersectionData.triangle.set(triangle);
					intersectionData.material = material;
				}
			}
		}
		
		return res;
	}
}
