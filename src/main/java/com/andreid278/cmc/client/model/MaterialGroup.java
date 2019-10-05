package com.andreid278.cmc.client.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Vec2f;
import com.andreid278.cmc.utils.Vec3f;
import com.andreid278.cmc.utils.Vec3i;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.renderer.Matrix4f;

public class MaterialGroup {
	public ByteBuffer indices = null;
	public int indicesCount = 0;
	public ByteBuffer vertices = null;
	public int verticesCount = 0;
	public ByteBuffer colors = null;
	public int colorsCount = 0;
	public ByteBuffer normals = null;
	public int normalsCount = 0;
	public ByteBuffer texCoords = null;
	public int texCoordsCount = 0;
	
	public Box3f bBox = new Box3f();
	
	public boolean isValid = false;

	public MaterialGroup() {
		
	}
	
	public void setData(List<Vec3i> i, List<Vec3f> v, List<Integer> c, List<Vec3f> n, List<Vec2f> t) {
		isValid = true;
		
		indicesCount = i.size();
		verticesCount = v.size();
		colorsCount = c != null ? c.size() : 0;
		normalsCount = n != null ? n.size() : 0;
		texCoordsCount = t != null ? t.size() : 0;
		
		int maxIndex = 0;
		int minIndex = Integer.MAX_VALUE;
		
		indices = ByteBuffer.allocateDirect(indicesCount * 3 * 4);
		indices.order(ByteOrder.nativeOrder());
		for(Vec3i index : i) {
			indices.putInt(index.x);
			indices.putInt(index.y);
			indices.putInt(index.z);
			
			minIndex = Math.min(minIndex, index.x);
			minIndex = Math.min(minIndex, index.y);
			minIndex = Math.min(minIndex, index.z);
			
			maxIndex = Math.max(maxIndex, index.x);
			maxIndex = Math.max(maxIndex, index.y);
			maxIndex = Math.max(maxIndex, index.z);
		}
		indices.rewind();
		
		if(minIndex >= verticesCount || maxIndex >= verticesCount) {
			isValid = false;
			return;
		}
		
		vertices = ByteBuffer.allocateDirect(verticesCount * 3 * 4);
		vertices.order(ByteOrder.nativeOrder());
		for(Vec3f vertex : v) {
			vertices.putFloat(vertex.x);
			vertices.putFloat(vertex.y);
			vertices.putFloat(vertex.z);
			
			bBox.addPoint(vertex);
		}
		vertices.rewind();
		
		if(colorsCount > 0) {
			if(minIndex >= colorsCount || maxIndex >= colorsCount) {
				isValid = false;
				return;
			}
			
			colors = ByteBuffer.allocateDirect(colorsCount * 3);
			colors.order(ByteOrder.nativeOrder());
			for(Integer color : c) {
				colors.put((byte) ((color >> 16) & 0xff));
				colors.put((byte) ((color >> 8) & 0xff));
				colors.put((byte) ((color >> 0) & 0xff));
			}
			colors.rewind();
		}
		
		if(normalsCount > 0) {
			if(minIndex >= normalsCount || maxIndex >= normalsCount) {
				isValid = false;
				return;
			}
			
			normals = ByteBuffer.allocateDirect(normalsCount * 3 * 4);
			normals.order(ByteOrder.nativeOrder());
			for(Vec3f normal : n) {
				normals.putFloat(normal.x);
				normals.putFloat(normal.y);
				normals.putFloat(normal.z);
			}
			normals.rewind();
		}
		
		if(texCoordsCount > 0) {
			if(minIndex >= texCoordsCount || maxIndex >= texCoordsCount) {
				isValid = false;
				return;
			}
			
			texCoords = ByteBuffer.allocateDirect(texCoordsCount * 2 * 4);
			texCoords.order(ByteOrder.nativeOrder());
			for(Vec2f tex : t) {
				texCoords.putFloat(tex.x);
				texCoords.putFloat(tex.y);
			}
			texCoords.rewind();
		}
	}
	
	public void rewind() {
		if(indices != null) indices.rewind();
		if(vertices != null) vertices.rewind();
		if(colors != null) colors.rewind();
		if(normals != null) normals.rewind();
		if(texCoords != null) texCoords.rewind();
	}
	
	public byte[] toByteArray() {
		ByteBuf buffer = Unpooled.buffer();
		
		rewind();
		
		buffer.writeInt(indicesCount);
		if(indicesCount > 0) {
			buffer.writeBytes(indices);
		}
		
		buffer.writeInt(verticesCount);
		if(verticesCount > 0) {
			buffer.writeBytes(vertices);
		}
		
		buffer.writeInt(colorsCount);
		if(colorsCount > 0) {
			buffer.writeBytes(colors);
		}
		
		buffer.writeInt(normalsCount);
		if(normalsCount > 0) {
			buffer.writeBytes(normals);
		}
		
		buffer.writeInt(texCoordsCount);
		if(texCoordsCount > 0) {
			buffer.writeBytes(texCoords);
		}
		
		bBox.writeTo(buffer);
		
		rewind();
		
		byte[] byteArray = new byte[buffer.readableBytes()];
		buffer.readBytes(byteArray);
		
		return byteArray;
	}
	
	public void fromByteArray(byte[] data) {
		ByteBuf buffer = Unpooled.wrappedBuffer(data);
		
		indicesCount = buffer.readInt();
		if(indicesCount > 0) {
			indices = ByteBuffer.allocateDirect(indicesCount * 3 * 4);
			indices.order(ByteOrder.nativeOrder());
			buffer.readBytes(indices);
		}
		
		verticesCount = buffer.readInt();
		if(verticesCount > 0) {
			vertices = ByteBuffer.allocateDirect(verticesCount * 3 * 4);
			vertices.order(ByteOrder.nativeOrder());
			buffer.readBytes(vertices);
		}
		
		colorsCount = buffer.readInt();
		if(colorsCount > 0) {
			colors = ByteBuffer.allocateDirect(colorsCount * 3);
			colors.order(ByteOrder.nativeOrder());
			buffer.readBytes(colors);
		}
		
		normalsCount = buffer.readInt();
		if(normalsCount > 0) {
			normals = ByteBuffer.allocateDirect(normalsCount * 3 * 4);
			normals.order(ByteOrder.nativeOrder());
			buffer.readBytes(normals);
		}
		
		texCoordsCount = buffer.readInt();
		if(texCoordsCount > 0) {
			texCoords = ByteBuffer.allocateDirect(texCoordsCount * 2 * 4);
			texCoords.order(ByteOrder.nativeOrder());
			buffer.readBytes(texCoords);
		}
		
		bBox.readFrom(buffer);
		
		rewind();
		
		isValid = indicesCount > 0 && verticesCount > 0;
	}
	
	public int trianglesNum() {
		return indicesCount;
	}
	
	public void getTriangle(int index, Vec3i res) {
		res.set(indices.getInt(index * 3 * 4), indices.getInt(index * 3 * 4 + 4), indices.getInt(index * 3 * 4 + 4 + 4));
	}
	
	public void getVertex(int index, Vec3f res) {
		res.set(vertices.getFloat(index * 3 * 4), vertices.getFloat(index * 3 * 4 + 4), vertices.getFloat(index * 3 * 4 + 4 + 4));
	}
	
	public MaterialGroup copyWithTransformation(Matrix4f transformation) {
		MaterialGroup copiedMaterialGroup = new MaterialGroup();
		
		if(!isValid) {
			return copiedMaterialGroup;
		}
		
		List<Vec3i> i = new ArrayList<>();
		List<Vec3f> v = new ArrayList<>();
		List<Integer> c = null;
		List<Vec3f> n = null;
		List<Vec2f> t = null;
		
		indices.rewind();
		for(int k = 0; k < indicesCount; k++) {
			int x = indices.getInt();
			int y = indices.getInt();
			int z = indices.getInt();
			i.add(new Vec3i(x, y, z));
		}
		
		vertices.rewind();
		for(int k = 0; k < verticesCount; k++) {
			float x = vertices.getFloat();
			float y = vertices.getFloat();
			float z = vertices.getFloat();
			Vec3f pos = new Vec3f(x, y, z);
			pos.applyMatrix(transformation);
			v.add(pos);
		}
		
		if(colorsCount > 0) {
			c = new ArrayList<>();
			colors.rewind();
			for(int k = 0; k < colorsCount; k++) {
				int x = colors.get();
				int y = colors.get();
				int z = colors.get();
				c.add(x * 256 * 256 + y * 256 + z);
			}
		}
		
		if(normalsCount > 0) {
			n = new ArrayList<>();
			normals.rewind();
			for(int k = 0; k < normalsCount; k++) {
				float x = normals.getFloat();
				float y = normals.getFloat();
				float z = normals.getFloat();
				Vec3f normal = new Vec3f(x, y, z);
				normal.transformDirection(transformation);
				n.add(normal);
			}
		}
		
		if(texCoordsCount > 0) {
			t = new ArrayList<>();
			texCoords.rewind();
			for(int k = 0; k < texCoordsCount; k++) {
				float x = texCoords.getFloat();
				float y = texCoords.getFloat();
				t.add(new Vec2f(x, y));
			}
		}
		
		copiedMaterialGroup.setData(i, v, c, n, t);
		
		return copiedMaterialGroup;
	}
}
