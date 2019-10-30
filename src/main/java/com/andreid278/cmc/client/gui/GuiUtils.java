package com.andreid278.cmc.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiUtils {
	public static void drawFilledRectangle(double x1, double y1, double x2, double y2, int r, int g, int b) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x1, y2, 0).color(r, g, b, 255).endVertex();
		vertexbuffer.pos(x2, y2, 0).color(r, g, b, 255).endVertex();
		vertexbuffer.pos(x2, y1, 0).color(r, g, b, 255).endVertex();
		vertexbuffer.pos(x1, y1, 0).color(r, g, b, 255).endVertex();
		tessellator.draw();
	}

	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x1, y1, z1).color(r, g, b, 255).endVertex();
		vertexbuffer.pos(x2, y2, z2).color(r, g, b, 255).endVertex();
		tessellator.draw();
	}

	public static void drawCircle(Vec3f center, Vec3f normal, float rad, int numSeg, int r, int g, int b) {
		Vec3f v1 = new Vec3f();
		Vec3f v2 = new Vec3f();
		normal.buildSpace(v1, v2);
		v1.mul(rad);
		v2.mul(rad);

		Vec3f p = new Vec3f();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		double angle = Math.PI * 2 / numSeg;
		for(int i = 0; i < numSeg; i++) {
			p.copy(center).add(new Vec3f(v1).mul((float)Math.cos(angle * i))).add(new Vec3f(v2).mul((float)Math.sin(angle * i)));
			vertexbuffer.pos(p.x, p.y, p.z).color(r, g, b, 255).endVertex();
		}
		tessellator.draw();
	}
	
	public static void drawFilledCircle(Vec3f center, Vec3f normal, float rad, int numSeg, int r, int g, int b) {
		Vec3f v1 = new Vec3f();
		Vec3f v2 = new Vec3f();
		normal.buildSpace(v1, v2);
		v1.mul(rad);
		v2.mul(rad);

		Vec3f p = new Vec3f();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(center.x, center.y, center.z).color(r, g, b, 255).endVertex();
		double angle = Math.PI * 2 / numSeg;
		for(int i = 0; i <= numSeg; i++) {
			p.copy(center).add(new Vec3f(v1).mul((float)Math.cos(angle * i))).add(new Vec3f(v2).mul((float)Math.sin(angle * i)));
			vertexbuffer.pos(p.x, p.y, p.z).color(r, g, b, 255).endVertex();
		}
		tessellator.draw();
	}

	/*public static void drawSphere(Vec3f center, float radius, int r, int g, int b) {
		
	}

	public static List<Vec3f> calculateSphere(int numSegX, int numSegY) {
		List<Vec3f> res = new ArrayList<>();
		float angleX = (float) (Math.PI * 2 / numSegX);
		float angleY = (float) (Math.PI / numSegY);
		for(int i = 0; i <= numSegY; i++) {
			if(i == 0) {
				res.add(new Vec3f(0.0f, 0.0f, -1.0f));
				continue;
			}
			else if(i == numSegY) {
				res.add(new Vec3f(0.0f, 0.0f, 1.0f));
				continue;
			}
			float cosPhi = (float) Math.cos(i * angleY * 2 - Math.PI);
			float sinPhi = (float) Math.sin(i * angleY * 2 - Math.PI);
			for(int j = 0; j < numSegX; j++) {
				float cosTheta = (float) Math.cos(j * angleX);
				float sinTheta = (float) Math.sin(j * angleX);
				res.add(new Vec3f(cosPhi * cosTheta, cosPhi * sinTheta, sinPhi));
			}
		}

		return res;
	}
	
	public static List<Vec3f> generateSpherePointsToDraw(List<Vec3f> points, int numSegX, int numSegY) {
		List<Vec3f> res = new ArrayList<>();
		
		for(int i = 1; i < numSegX + 1; i++) {
			res.add(points.get(0));
			res.add(points.get(i));
			res.add(points.get(i % numSegX + 1));
		}
		
		if(numSegY > 2) {
			
		}
		
		for(int i = numSegX; i > 0; i--) {
			res.add(points.get(points.size() - i));
			res.add(points.get(i == 1 ? points.size() - numSegX : points.size() - i + 1));
			res.add(points.get(points.size()));
		}
		
		return res;
	}
	
	public static List<Vec3f> transformSphere(List<Vec3f> points, Vec3f center, float radius) {
		List<Vec3f> res = new ArrayList<>();
		
		Matrix4f transformation = new Matrix4f();
		transformation.setIdentity();
	}*/
}
