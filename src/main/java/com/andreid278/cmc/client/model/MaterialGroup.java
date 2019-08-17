package com.andreid278.cmc.client.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.http.util.ByteArrayBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MaterialGroup {
	public ByteBuffer vertices = null;
	public ByteBuffer normals = null;
	public ByteBuffer texCoords = null;
	public ByteBuffer colors = null;
	
	public int vertexSize = 0;
	public int count = 0;
	
	public Vector3f boxMin = new Vector3f();
	public Vector3f boxMax = new Vector3f();

	public MaterialGroup(int count) {
		this.count = count;
	}
	
	public void setVertices(List<Vector3f> v) {
		boxMin.x = Float.MAX_VALUE;
		boxMin.y = Float.MAX_VALUE;
		boxMin.z = Float.MAX_VALUE;
		boxMax.x = -Float.MAX_VALUE;
		boxMax.y = -Float.MAX_VALUE;
		boxMax.z = -Float.MAX_VALUE;
		vertices = ByteBuffer.allocateDirect(count * 3 * 4);
		vertices.order(ByteOrder.nativeOrder());
		
		for(Vector3f vertex : v) {
			vertices.putFloat(vertex.x);
			vertices.putFloat(vertex.y);
			vertices.putFloat(vertex.z);
			
			if(vertex.x < boxMin.x) boxMin.x = vertex.x;
			if(vertex.y < boxMin.y) boxMin.y = vertex.y;
			if(vertex.z < boxMin.z) boxMin.z = vertex.z;
			if(vertex.x > boxMax.x) boxMax.x = vertex.x;
			if(vertex.y > boxMax.y) boxMax.y = vertex.y;
			if(vertex.z > boxMax.z) boxMax.z = vertex.z;
		}
		
		vertices.flip();
	}
	
	public void setColors(List<Integer> c) {
		colors = ByteBuffer.allocateDirect(count * 4);
		colors.order(ByteOrder.nativeOrder());
		
		for(int color : c) {
			//colors.putInt(color);
			colors.put((byte) ((color >> 16) & 0xff));
			colors.put((byte) ((color >> 8) & 0xff));
			colors.put((byte) ((color >> 0) & 0xff));
			//colors.put((byte) ((color >> 24) & 0xff));
		}
		
		colors.flip();
	}
	
	public byte[] toByteArray() {
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeInt(count);
		
		buffer.writeBoolean(vertices != null);
		if(vertices != null) {
			buffer.writeBytes(vertices);
			vertices.rewind();
		}
		
		buffer.writeBoolean(normals != null);
		if(normals != null) {
			buffer.writeBytes(normals);
			normals.rewind();
		}
		
		buffer.writeBoolean(texCoords != null);
		if(texCoords != null) {
			buffer.writeBytes(texCoords);
			texCoords.rewind();
		}
		
		buffer.writeBoolean(colors != null);
		if(colors != null) {
			buffer.writeBytes(colors);
			colors.rewind();
		}
		
		byte[] byteArray = new byte[buffer.readableBytes()];
		buffer.readBytes(byteArray);
		
		return byteArray;
	}
}
