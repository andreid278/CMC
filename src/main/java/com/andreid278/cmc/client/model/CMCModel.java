package com.andreid278.cmc.client.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Vec3f;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class CMCModel {
	public List<MaterialGroup> materials = Lists.newArrayList();
	public Box3f bBox = new Box3f();

	public CMCModel() {
		
	}
	
	public CMCModel(List<MaterialGroup> materials) {
		this.materials = materials;
		
		calculateBBox();
	}

	public void draw() {
		boolean tex2d = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		int shadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		boolean oldCulling = GL11.glGetBoolean(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		int prevTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		for(int i = 0; i < materials.size(); i++) {
			MaterialGroup material = materials.get(i);
			
			if(!material.isValid) continue;
			
			material.rewind();
			
			boolean texEnable = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
			
			material.createTexture();
			
			if(material.textureID != 0) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			
			material.bindTexture();

			OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
			
			GlStateManager.color(material.materialColor.x, material.materialColor.y, material.materialColor.z);
			
			GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 0, material.vertices);

			if(material.colorsCount > 0) {
				GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
				GlStateManager.glColorPointer(3, GL11.GL_UNSIGNED_BYTE, 0, material.colors);
			}

			if(material.normalsCount > 0) {
				GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				GL11.glNormalPointer(GL11.GL_FLOAT, 0, material.normals);
			}

			if(material.texCoordsCount > 0) {
				GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, 0, material.texCoords);
			}
			
			GL11.glDrawElements(GL11.GL_TRIANGLES, material.indicesCount * 3, GL11.GL_UNSIGNED_INT, material.indices);

			GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);

			if(material.colors != null) {
				GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
				GlStateManager.resetColor();
			}

			if(material.normals != null) {
				GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			}

			if(material.texCoords != null) {
				GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			}
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			if(!texEnable) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
		}
		
		if(oldCulling) {
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		
		GlStateManager.shadeModel(shadeModel);

		if(tex2d) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
	}

	private void disableAll() {
		GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GlStateManager.glDisableClientState(GL11.GL_EDGE_FLAG_ARRAY);
		GlStateManager.glDisableClientState(GL11.GL_INDEX_ARRAY);
		GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}

	public void saveToFile(String name) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + name + "." + CMCData.instance.fileExt);
			writeIntToStream(stream, CMCData.version);
			writeIntToStream(stream, materials.size());
			for(int i = 0; i < materials.size(); i++) {
				MaterialGroup material = materials.get(i);
				byte[] materialData = material.toByteArray();
				writeIntToStream(stream, materialData.length);
				stream.write(materialData);
			}
			stream.flush();
			stream.close();
			System.out.println("Client : Saved to " + CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + name + "." + CMCData.instance.fileExt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Can't save file!!!");
			return;
		}
	}
	
	public boolean loadFromFile(String name) {
		FileInputStream stream;
		try {
			stream = new FileInputStream(CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + name + "." + CMCData.instance.fileExt);
			
			int version = readIntFromStream(stream);
			if(version != CMCData.version) {
				System.out.println("Trying to load wrong versioned file");
				stream.close();
				return false;
			}
			
			int s = readIntFromStream(stream);
			for(int i = 0; i < s; i++) {
				int materialDataSize = readIntFromStream(stream);
				byte[] materialData = new byte[materialDataSize];
				stream.read(materialData, 0, materialDataSize);
				
				MaterialGroup group = new MaterialGroup();
				group.fromByteArray(materialData);
				materials.add(group);
			}
			
			calculateBBox();
			
			stream.close();
			
			System.out.println("Read from " + name);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Client : Can't find file " + CMCData.instance.dataPathClient + CMCData.instance.curWorldPath + name + "." + CMCData.instance.fileExt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void calculateBBox() {
		bBox.reset();
		
		for(MaterialGroup material : materials) {
			bBox.union(material.bBox);
		}
	}
	
	private void writeIntToStream(FileOutputStream stream, int value) throws IOException {
		stream.write((value >> 24) & 255);
		stream.write((value >> 16) & 255);
		stream.write((value >> 8) & 255);
		stream.write(value & 255);
	}
	
	private int readIntFromStream(FileInputStream stream) throws IOException {
		int d = stream.read();
		int c = stream.read();
		int b = stream.read();
		int a = stream.read();
		return a + b * 256 + c * 256 * 256 + d * 256 * 256 * 256;
	}
	
	public void normalize() {
		if(!bBox.isValid) {
			return;
		}
		
		Matrix4f transformation = new Matrix4f();
		transformation.setIdentity();
		float scale = 1.0f / Math.max(bBox.getSizeX(), Math.max(bBox.getSizeY(), bBox.getSizeZ()));
		Matrix4f.scale(new Vec3f(scale, scale, scale), transformation, transformation);
		Matrix4f.translate(new Vec3f(-bBox.getCenterX(), -bBox.getCenterY(), -bBox.getCenterZ()), transformation, transformation);
		
		for(MaterialGroup material : materials) {
			material.applyTransformation(transformation);
		}
		
		calculateBBox();
	}

}
