package com.andreid278.cmc.utils;

public class Plane {
	public Vec3f normal = new Vec3f();
	public float constant = 0.0f;
	
	public Plane() {
		
	}
	
	public Plane(Vec3f normal, Vec3f point) {
		this.normal.copy(normal);
		constant = -Vec3f.dot(normal, point);
	}
	
	public void setFromPointNormal(Vec3f point, Vec3f normal) {
		this.normal.copy(normal);
		constant = -Vec3f.dot(normal, point);
	}
	
	public void setFromPointVectorVector(Vec3f point, Vec3f v1, Vec3f v2) {
		Vec3f.cross(v1, v2, normal);
		constant = -Vec3f.dot(normal, point);
	}
	
	public float distanceToPoint(Vec3f p) {
		return Vec3f.dot(normal, p) + constant;
	}
}
