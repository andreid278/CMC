package com.andreid278.cmc.client.model;

import java.util.List;
import java.util.UUID;

import javax.vecmath.Vector3f;

import com.andreid278.cmc.CMC;
import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ModelReader {
	private CMCModel model;
	
	public ModelReader(UUID uuid) {
		model = loadTestModel();
	}
	
	public CMCModel getModel() {
		return model;
	}
	
	private CMCModel loadTestModel() {
		List<MaterialGroup> materials = Lists.newArrayList();
		
		MaterialGroup material = new MaterialGroup(36);
		
		List<Vector3f> vertices = Lists.newArrayList();
		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(1, 0, 0));
		vertices.add(new Vector3f(1, 1, 0));

		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(1, 1, 0));
		vertices.add(new Vector3f(0, 1, 0));

		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(0, 0, 1));
		vertices.add(new Vector3f(1, 0, 1));

		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(1, 0, 1));
		vertices.add(new Vector3f(1, 0, 0));

		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(0, 1, 0));
		vertices.add(new Vector3f(0, 1, 1));

		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(0, 1, 1));
		vertices.add(new Vector3f(0, 0, 1));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(1, 0, 1));
		vertices.add(new Vector3f(0, 0, 1));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(0, 0, 1));
		vertices.add(new Vector3f(0, 1, 1));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(0, 1, 1));
		vertices.add(new Vector3f(0, 1, 0));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(0, 1, 0));
		vertices.add(new Vector3f(1, 1, 0));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(1, 0, 0));
		vertices.add(new Vector3f(1, 0, 1));

		vertices.add(new Vector3f(1, 1, 1));
		vertices.add(new Vector3f(1, 1, 0));
		vertices.add(new Vector3f(1, 0, 0));

		material.setVertices(vertices);
		
		List<Integer> colors = Lists.newArrayList();
		colors.add(0x000000);
		colors.add(0xff0000);
		colors.add(0xffff00);

		colors.add(0x000000);
		colors.add(0xffff00);
		colors.add(0x00ff00);

		colors.add(0x000000);
		colors.add(0x0000ff);
		colors.add(0xff00ff);

		colors.add(0x000000);
		colors.add(0xff00ff);
		colors.add(0xff0000);

		colors.add(0x000000);
		colors.add(0x00ff00);
		colors.add(0x00ffff);

		colors.add(0x000000);
		colors.add(0x00ffff);
		colors.add(0x0000ff);

		colors.add(0xffffff);
		colors.add(0xff00ff);
		colors.add(0x0000ff);

		colors.add(0xffffff);
		colors.add(0x0000ff);
		colors.add(0x00ffff);

		colors.add(0xffffff);
		colors.add(0x00ffff);
		colors.add(0x00ff00);

		colors.add(0xffffff);
		colors.add(0x00ff00);
		colors.add(0xffff00);

		colors.add(0xffffff);
		colors.add(0xff0000);
		colors.add(0xff00ff);

		colors.add(0xffffff);
		colors.add(0xffff00);
		colors.add(0xff0000);

		material.setColors(colors);
		
		materials.add(material);
		
		CMCModel model = new CMCModel(materials);
		
		return model;
	}
}
