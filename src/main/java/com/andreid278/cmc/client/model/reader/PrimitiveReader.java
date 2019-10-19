package com.andreid278.cmc.client.model.reader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.MaterialGroup;
import com.andreid278.cmc.utils.Vec2f;
import com.andreid278.cmc.utils.Vec3f;
import com.andreid278.cmc.utils.Vec3i;
import com.google.common.collect.Lists;

public class PrimitiveReader extends CMCModelReader {
	
	public enum PrimitiveType {
		Circle,
		Cylinder
	}
	
	PrimitiveType primitiveType;
	
	int defaultTextureWidth = 256;
	int defaultTextureHeight = 256;
	
	public PrimitiveReader(PrimitiveType primitiveType) {
		this.primitiveType = primitiveType;
	}

	@Override
	public boolean read() {
		switch(primitiveType) {
		case Circle:
			createCircle();
			break;
		case Cylinder:
			createCylinder();
		}
		return true;
	}
	
	private void createCircle() {
		List<MaterialGroup> materials = new ArrayList<MaterialGroup>();
		MaterialGroup material = new MaterialGroup();
		materials.add(material);
		
		List<Vec3f> vertices = Lists.newArrayList();
		List<Integer> colors = Lists.newArrayList();
		List<Vec2f> texCoords = Lists.newArrayList();
		List<Vec3i> indices = Lists.newArrayList();
		
		float rad = 0.5f;
		int numSeg = 32;
		int color = 0xffffff;
		
		float angle = (float) (Math.PI * 2 / numSeg);
		vertices.add(new Vec3f(0, 0, 0));
		//colors.add(color);
		colors.add(randColor());
		texCoords.add(new Vec2f(0.5f, 0.5f));
		for(int i = 0; i < numSeg; i++) {
			float u = (float)Math.cos(angle * i);
			float v = (float)Math.sin(angle * i);
			vertices.add(new Vec3f(u * rad, 0, v * rad));
			//colors.add(color);
			colors.add(randColor());
			texCoords.add(new Vec2f(u * 0.5f + 0.5f, v * 0.5f + 0.5f));
			indices.add(new Vec3i(0, i + 1, (i + 1) % numSeg + 1));
		}
		
		material.setData(indices, vertices, null, null, texCoords);
		material.setTexture(generateImage(defaultTextureWidth, defaultTextureHeight), defaultTextureWidth, defaultTextureHeight);
		
		model = new CMCModel(materials);
	}
	
	private void createCylinder() {
		List<MaterialGroup> materials = new ArrayList<MaterialGroup>();
		MaterialGroup material = new MaterialGroup();
		materials.add(material);
		
		List<Vec3f> vertices = Lists.newArrayList();
		List<Integer> colors = Lists.newArrayList();
		List<Vec3i> indices = Lists.newArrayList();
		
		float rad = 0.5f;
		float height = 1.0f;
		int numSeg = 32;
		int color = 0xffffff;
		
		float angle = (float) (Math.PI * 2 / numSeg);
		
		int offset = 0;
		
		// Bottom cap
		vertices.add(new Vec3f(0, -height * 0.5f, 0));
		//colors.add(color);
		colors.add(randColor());
		for(int i = 0; i < numSeg; i++) {
			vertices.add(new Vec3f((float)Math.cos(angle * i) * rad, -height * 0.5f, (float)Math.sin(angle * i) * rad));
			//colors.add(color);
			colors.add(randColor());
			indices.add(new Vec3i(0, i + 1, (i + 1) % numSeg + 1));
		}
		
		offset += numSeg + 1;
		
		// Top cap
		vertices.add(new Vec3f(0, height * 0.5f, 0));
		//colors.add(color);
		colors.add(randColor());
		for(int i = 0; i < numSeg; i++) {
			vertices.add(new Vec3f((float)Math.cos(angle * i) * rad, height * 0.5f, (float)Math.sin(angle * i) * rad));
			//colors.add(color);
			colors.add(randColor());
			indices.add(new Vec3i(offset, offset + i + 1, offset + (i + 1) % numSeg + 1));
		}
		
		offset += numSeg + 1;
		
		// Torso
		for(int i = 0; i < numSeg; i++) {
			vertices.add(new Vec3f((float)Math.cos(angle * i) * rad, -height * 0.5f, (float)Math.sin(angle * i) * rad));
			vertices.add(new Vec3f((float)Math.cos(angle * i) * rad, height * 0.5f, (float)Math.sin(angle * i) * rad));
			//colors.add(color);
			//colors.add(color);
			colors.add(randColor());
			colors.add(randColor());
			indices.add(new Vec3i(offset + i * 2, offset + i * 2 + 1, offset + (i * 2 + 2) % (numSeg * 2)));
			indices.add(new Vec3i(offset + (i * 2 + 2) % (numSeg * 2), offset + i * 2 + 1, offset + (i * 2 + 3) % (numSeg * 2)));
		}
		
		material.setData(indices, vertices, colors, null, null);
		
		model = new CMCModel(materials);
	}
	
	private int randColor() {
		Random rand = new Random();
		int r = (int) (rand.nextFloat() * 255);
		int g = (int) (rand.nextFloat() * 255);
		int b = (int) (rand.nextFloat() * 255);
		return r * 256 * 256 + g * 256 + b;
	}
	
	private int[] generateImage(int w, int h) {
		int[] image = new int[w * h];
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				image[i * w + j] = randColor() + 0xff000000;
			}
		}
		
		return image;
	}
}
