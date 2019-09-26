package com.andreid278.cmc.client.model;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.vecmath.Vector3f;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.utils.Vec3f;
import com.andreid278.cmc.utils.Vec3i;
import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ModelReader {
	private CMCModel model;
	
	public ModelReader(UUID uuid, boolean toCreate) {
		if(toCreate) {
			model = loadTestModel();
		}
		else {
			model = new CMCModel();
			model.loadFromFile(uuid.toString());
		}
	}
	
	public CMCModel getModel() {
		return model;
	}
	
	private CMCModel loadTestModel() {
		List<MaterialGroup> materials = Lists.newArrayList();
		
		MaterialGroup material = new MaterialGroup();
		
		List<Vec3f> vertices = Lists.newArrayList();
		vertices.add(new Vec3f(-0.5f, -0.5f, -0.5f));
		vertices.add(new Vec3f(0.5f, -0.5f, -0.5f));
		vertices.add(new Vec3f(-0.5f, 0.5f, -0.5f));
		vertices.add(new Vec3f(0.5f, 0.5f, -0.5f));
		vertices.add(new Vec3f(-0.5f, -0.5f, 0.5f));
		vertices.add(new Vec3f(0.5f, -0.5f, 0.5f));
		vertices.add(new Vec3f(-0.5f, 0.5f, 0.5f));
		vertices.add(new Vec3f(0.5f, 0.5f, 0.5f));
		
		List<Integer> colors = Lists.newArrayList();
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		colors.add(randColor());
		
		List<Vec3i> indices = Lists.newArrayList();
		indices.add(new Vec3i(0, 1, 3));
		indices.add(new Vec3i(0, 3, 2));
		indices.add(new Vec3i(4, 6, 7));
		indices.add(new Vec3i(4, 7, 5));
		indices.add(new Vec3i(0, 2, 6));
		indices.add(new Vec3i(0, 6, 4));
		indices.add(new Vec3i(1, 5, 7));
		indices.add(new Vec3i(1, 7, 3));
		indices.add(new Vec3i(0, 4, 5));
		indices.add(new Vec3i(0, 5, 1));
		indices.add(new Vec3i(2, 3, 7));
		indices.add(new Vec3i(2, 7, 6));
		
		material.setData(indices, vertices, colors, null, null);
		
		/*MaterialGroup material = new MaterialGroup();
		
		List<Vec3f> vertices = Lists.newArrayList();
		vertices.add(MathUtils.instance.new Vec3f(0, 0, 0));
		vertices.add(MathUtils.instance.new Vec3f(1, 0, 0));
		vertices.add(MathUtils.instance.new Vec3f(0, 1, 0));
		
		List<Vec3i> indices = Lists.newArrayList();
		indices.add(MathUtils.instance.new Vec3i(0, 1, 2));
		
		material.setData(indices, vertices, null, null, null);*/
		
		materials.add(material);
		
		CMCModel model = new CMCModel(materials);
		
		return model;
	}
	
	public int randColor() {
		Random rand = new Random();
		int r = (int) (rand.nextFloat() * 255);
		int g = (int) (rand.nextFloat() * 255);
		int b = (int) (rand.nextFloat() * 255);
		
		return r * 256 * 256 + g * 256 + b;
	}
}
