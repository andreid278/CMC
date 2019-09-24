package com.andreid278.cmc.client.gui;

import org.lwjgl.opengl.GL11;

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
}
