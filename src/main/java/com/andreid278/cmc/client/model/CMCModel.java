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

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.andreid278.cmc.common.CMCData;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class CMCModel {
	private List<MaterialGroup> materials = Lists.newArrayList();
	public Vector3f boxMin = new Vector3f();
	public Vector3f boxMax = new Vector3f();

	public CMCModel() {
		boxMin.x = Float.MAX_VALUE;
		boxMin.y = Float.MAX_VALUE;
		boxMin.z = Float.MAX_VALUE;
		boxMax.x = -Float.MAX_VALUE;
		boxMax.y = -Float.MAX_VALUE;
		boxMax.z = -Float.MAX_VALUE;
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

		for(int i = 0; i < materials.size(); i++) {
			MaterialGroup material = materials.get(i);

			int offset = 0;

			OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);

			GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 0, material.vertices);
			offset += 12;

			if(material.colors != null) {
				GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
				GlStateManager.glColorPointer(3, GL11.GL_UNSIGNED_BYTE, 0, material.colors);
				offset += 4;
			}

			if(material.normals != null) {
				GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				GL11.glNormalPointer(GL11.GL_FLOAT, material.vertexSize, material.normals);
				offset += 12;
			}

			if(material.texCoords != null) {
				GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, material.vertexSize, material.texCoords);
				offset += 8;
			}

			GlStateManager.glDrawArrays(GL11.GL_TRIANGLES, 0, material.count);

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
		}

		/*ByteBuffer bBuff = ByteBuffer.allocateDirect(9 * 4);
	    bBuff.order(ByteOrder.nativeOrder());
	    FloatBuffer f = bBuff.asFloatBuffer();
		f.put(0.0f);
		f.put(0.0f);
		f.put(0.0f);
		f.put(1.0f);
		f.put(1.0f);
		f.put(1.0f);
		f.put(1.0f);
		f.put(0.0f);
		f.put(0.0f);

		f.flip();
		GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, 0, f);
		GlStateManager.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
		GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);*/

		//GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_LIGHT0);
		/*GlStateManager.disableLighting();
		GlStateManager.disableLight(0);
		GlStateManager.disableColorMaterial();
		GlStateManager.disableBlend();
		GlStateManager.disableColorLogic();
		GlStateManager.disableTexture2D();*/
		/*GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(0, 0, 0);
		GL11.glColor3f(1.0f, 0.0f, 1.0f);
		GL11.glVertex3f(1, 1, 1);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex3f(1, 0, 0);
		GL11.glEnd();*/

		GlStateManager.shadeModel(shadeModel);

		if(tex2d) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
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
				
				MaterialGroup group = new MaterialGroup(0);
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
	
	public float getCenterX() {
		return (boxMin.x + boxMax.x) * 0.5f;
	}
	
	public float getCenterY() {
		return (boxMin.y + boxMax.y) * 0.5f;
	}
	
	public float getCenterZ() {
		return (boxMin.z + boxMax.z) * 0.5f;
	}
	
	public float getSizeX() {
		return boxMax.x - boxMin.x;
	}
	
	public float getSizeY() {
		return boxMax.y - boxMin.y;
	}
	
	public float getSizeZ() {
		return boxMax.z - boxMin.z;
	}
	
	public float getSize() {
		return (float) Math.sqrt(getSizeX() * getSizeX() + getSizeY() * getSizeY() + getSizeZ() * getSizeZ());
	}
	
	public void calculateBBox() {
		boxMin.x = Float.MAX_VALUE;
		boxMin.y = Float.MAX_VALUE;
		boxMin.z = Float.MAX_VALUE;
		boxMax.x = -Float.MAX_VALUE;
		boxMax.y = -Float.MAX_VALUE;
		boxMax.z = -Float.MAX_VALUE;
		for(MaterialGroup material : materials) {
			if(material.boxMin.x < boxMin.x) boxMin.x = material.boxMin.x;
			if(material.boxMin.y < boxMin.y) boxMin.y = material.boxMin.y;
			if(material.boxMin.z < boxMin.z) boxMin.z = material.boxMin.z;
			if(material.boxMax.x > boxMax.x) boxMax.x = material.boxMax.x;
			if(material.boxMax.y > boxMax.y) boxMax.y = material.boxMax.y;
			if(material.boxMax.z > boxMax.z) boxMax.z = material.boxMax.z;
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

}
