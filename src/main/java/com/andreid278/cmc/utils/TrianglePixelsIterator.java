package com.andreid278.cmc.utils;

import java.util.ArrayList;
import java.util.List;

public class TrianglePixelsIterator {
	int w;
	int h;
	
	Vec2f p1 = new Vec2f();
	Vec2f p2 = new Vec2f();
	Vec2f p3 = new Vec2f();
	
	Vec2i curP = new Vec2i();
	
	List<Integer> yMinMax = new ArrayList<>();
	
	int xMin;
	int xMax;
	int xMiddle;
	
	int curYMin;
	int curYMax;
	int prevYMin;
	int prevYMax;
	
	public TrianglePixelsIterator(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	public void init(Vec2f point1, Vec2f point2, Vec2f point3) {
		p1.set(point1);
		p2.set(point2);
		p3.set(point3);
		
		if(p1.x > p2.x) {
			p1.swap(p2);
		}
		
		if(p1.x > p3.x) {
			p1.swap(p3);
		}
		
		if(p2.x > p3.x) {
			p2.swap(p3);
		}
		
		xMin = (int)(p1.x * w);
		if(xMin >= w) {
			xMax = 0;
			curP.x = 1;
			return;
		}
		xMax = (int)(p3.x * w);
		if(xMax >= w) {
			xMax = w - 1;
		}
		xMiddle = (int)(p2.x * w);
		if(xMiddle >= w) {
			xMiddle = w - 1;
		}
		
		yMinMax.clear();
		
		getMinMaxInPointLeft(p1.x);
		prevYMin = curYMin;
		prevYMax = curYMax;
		
		for(int i = xMin + 1; i <= xMiddle; i++) {
			getMinMaxInPointLeft((float)i / w);
			
			prevYMin = Math.min(prevYMin, curYMin);
			prevYMax = Math.max(prevYMax, curYMax);
			
			yMinMax.add((prevYMin << 16) + prevYMax);
			
			prevYMin = curYMin;
			prevYMax = curYMax;
		}
		
		if(Math.abs(p1.x - p2.x) > 0.00001f) {
			getMinMaxInPointLeft(p2.x);
		}
		else {
			getMinMaxInPointRight(p2.x);
		}
		
		prevYMin = Math.min(prevYMin, curYMin);
		prevYMax = Math.max(prevYMax, curYMax);
		
		for(int i = xMiddle + 1; i <= xMax; i++) {
			getMinMaxInPointRight((float)i / w);
			
			prevYMin = Math.min(prevYMin, curYMin);
			prevYMax = Math.max(prevYMax, curYMax);
			
			yMinMax.add((prevYMin << 16) + prevYMax);
			
			prevYMin = curYMin;
			prevYMax = curYMax;
		}
		
		getMinMaxInPointRight(p3.x);
		
		prevYMin = Math.min(prevYMin, curYMin);
		prevYMax = Math.max(prevYMax, curYMax);
		
		yMinMax.add((prevYMin << 16) + prevYMax);
		
		curP.x = xMin;
		getMinMax(curP.x);
		curP.y = curYMin;
	}
	
	public boolean more() {
		return curP.x <= xMax;
	}
	
	public void next() {
		if(curP.y < curYMax) {
			curP.y++;
		}
		else {
			curP.x++;
			if(curP.x <= xMax) {
				getMinMax(curP.x);
				curP.y = curYMin;
			}
		}
	}
	
	public Vec2i cur() {
		return curP;
	}
	
	private float getPoint(float x, Vec2f point1, Vec2f point2) {
		return (x - point1.x) * (point2.y - point1.y) / (point2.x - point1.x) + point1.y;
	}
	
	private void getMinMaxInPointLeft(float x) {
		curYMin = (int)(getPoint(x, p1, p2) * h);
		curYMax = (int)(getPoint(x, p1, p3) * h);
		
		if(curYMin > curYMax) {
			int temp = curYMin;
			curYMin = curYMax;
			curYMax = temp;
		}
		
		if(curYMin >= h) {
			curYMin = h - 1;
		}
		
		if(curYMax >= h) {
			curYMax = h - 1;
		}
	}
	
	private void getMinMaxInPointRight(float x) {
		curYMin = (int)(getPoint(x, p2, p3) * h);
		curYMax = (int)(getPoint(x, p1, p3) * h);
		
		if(curYMin > curYMax) {
			int temp = curYMin;
			curYMin = curYMax;
			curYMax = temp;
		}
		
		if(curYMin >= h) {
			curYMin = h - 1;
		}
		
		if(curYMax >= h) {
			curYMax = h - 1;
		}
	}
	
	private void getMinMax(int x) {
		int y = yMinMax.get(x - xMin);
		curYMin = y >> 16;
		curYMax = y & 0xffff;
	}
}
