package com.andreid278.cmc.client.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.andreid278.cmc.utils.MathUtils;
import com.andreid278.cmc.utils.MathUtils.Box3f;
import com.andreid278.cmc.utils.MathUtils.Vec2f;
import com.andreid278.cmc.utils.MathUtils.Vec3f;
import com.andreid278.cmc.utils.MathUtils.Vec3i;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
	
	public Box3f bBox = MathUtils.instance.new Box3f();
	
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
}
