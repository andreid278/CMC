package com.andreid278.cmc.utils;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
	public static MathUtils instance = new MathUtils();
	
	private static Vec3f dir1 = new Vec3f();
	private static Vec3f dir2 = new Vec3f();
	private static Vec3f normal = new Vec3f();
	private static Vec3f dirToSphere = new Vec3f();
	public static boolean intersectTriangleSphere(Vec3f p1, Vec3f p2, Vec3f p3, Vec3f center, float rad, Vector4f intersectionRes) {
		dir1.copy(p2).sub(p1);
		dir2.copy(p3).sub(p1);
		Vec3f.cross(dir1, dir2, normal);
		normal.normalise();
		dirToSphere.copy(center).sub(p1);
		
		float dist = Vec3f.dot(dirToSphere, normal);
		
		if(Math.abs(dist) > rad) {
			return false;
		}
		
		intersectionRes.x = center.x - normal.x * dist;
		intersectionRes.y = center.y - normal.y * dist;
		intersectionRes.z = center.z - normal.z * dist;
		intersectionRes.w = (float) Math.sqrt(rad * rad - dist * dist);
		
		return true;
	}
}
