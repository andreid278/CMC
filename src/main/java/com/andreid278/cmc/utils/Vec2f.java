package com.andreid278.cmc.utils;

public class Vec2f {
	public float x;
	public float y;
	
	public Vec2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2f(Vec2f v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vec2f v) {
		this.x = v.x;
		this.y = v.y;
	}
}
