package com.andreid278.cmc.utils;

public class Vec3i {
	public int x;
	public int y;
	public int z;
	
	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
}
