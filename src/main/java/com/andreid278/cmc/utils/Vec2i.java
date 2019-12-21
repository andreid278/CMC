package com.andreid278.cmc.utils;

public class Vec2i {
	public int x;
	public int y;
	
	public Vec2i() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2i(Vec2i v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vec2i v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void swap(Vec2i v) {
		x = x + v.x;
		v.x = x - v.x;
		x = x - v.x;
		y = y + v.y;
		v.y = y - v.y;
		y = y - v.y;
	}
}
