package com.andreid278.cmc.client.gui;

import org.lwjgl.opengl.GL11;

import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.renderer.BufferBuilder;
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
}
