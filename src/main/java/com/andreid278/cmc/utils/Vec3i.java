package com.andreid278.cmc.utils;

public class Vec3i {
	public int x;
	public int y;
	public int z;
	
	public Vec3i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3i(Vec3i p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public Vec3i set(Vec3i p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		
		return this;
	}
	
	public Vec3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public Vec3i set(int index, int value) {
		switch(index) {
		case 0:
			x = value;
			break;
		case 1:
			y = value;
			break;
		case 2:
			z = value;
			break;
		}
		
		return this;
	}
	
	public int get(int index) {
		switch(index) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		}
		
		return 0;
	}
	
	public int max() {
		return Math.max(x, Math.max(y, z));
	}
}
