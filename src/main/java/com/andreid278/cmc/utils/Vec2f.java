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
	
	static float temp = 0.0f;
	public void swap(Vec2f v) {
		temp = x;
		x = v.x;
		v.x = temp;
		temp = y;
		y = v.y;
		v.y = temp;
	}
}
