package com.andreid278.cmc.client.model.reader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;

import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.MaterialGroup;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.utils.Box3f;
import com.andreid278.cmc.utils.Vec2f;
import com.andreid278.cmc.utils.Vec3f;
import com.andreid278.cmc.utils.Vec3i;

public class OBJReader extends CMCModelReader {
	
	String path;
	
	private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
	
	private class Face {
		public Vec3i pointIndices;
		public Vec3i normalIndices;
		public Vec3i texCoordsIndices;
		
		public Face() {
			
		}
		
		public Face(Vec3i pointIndices, Vec3i normalIndices, Vec3i texCoordsIndices) {
			this.pointIndices = new Vec3i(pointIndices);
			if(normalIndices != null) {
				this.normalIndices = new Vec3i(normalIndices);
			}
			if(texCoordsIndices != null) {
				this.texCoordsIndices = new Vec3i(texCoordsIndices);
			}
		}
	}
	
	private class Material {
		public Vec3f Kd = new Vec3f(1.0f, 1.0f, 1.0f);
		public String texturePath = "";
		public int w = 0;
		public int h = 0;
		public int[] texture = null;
		
		public Material() {
			
		}
		
		public Material(Material other) {
			Kd.set(other.Kd);
			texturePath = other.texturePath;
			w = other.w;
			h = other.h;
			if(other.texture != null) {
				texture = new int[w * h];
				System.arraycopy(other.texture, 0, texture, 0, w * h);
			}
		}
		
		public void clear() {
			Kd.set(1.0f, 1.0f, 1.0f);
			texturePath = "";
			w = 0;
			h = 0;
			texture = null;
		}
	}
	
	private Map<String, Material> mapOfMaterials = new HashMap<>();
	
	public OBJReader(String path) {
		this.path = path;
	}

	@Override
	public boolean read() {
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader streamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			
			List<Vec3f> points = new ArrayList<>();
			List<Vec3f> normals = new ArrayList<>();
			List<Vec2f> texCoords = new ArrayList<>();
			List<Face> faces = new ArrayList<>();
			
			int maxPointIndex = 0;
			int maxNormalIndex = 0;
			int maxTexCoordsIndex = 0;
			
			List<MaterialGroup> materials = new ArrayList<>();
			String curMaterial = "";
			
			for(;;) {
				String line = bufferedReader.readLine();
				
				if(line == null) {
					Material mat = null;
					if(!curMaterial.isEmpty()) {
						if(mapOfMaterials.containsKey(curMaterial)) {
							mat = mapOfMaterials.get(curMaterial);
						}
					}
					dumpMaterialGroup(points, normals, texCoords, faces, materials, mat, maxPointIndex, maxNormalIndex, maxTexCoordsIndex);
					break;
				}
				
				line.trim();
				
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				
				String[] fields = WHITE_SPACE.split(line, 2);
				String key = fields[0];
				String data = fields[1];
				String[] splitData = WHITE_SPACE.split(data);
				
				if(key.equalsIgnoreCase("mtllib")) {
					parseMaterials(data);
				}
				else if(key.equalsIgnoreCase("usemtl")) {
					Material mat = null;
					if(!curMaterial.isEmpty()) {
						if(mapOfMaterials.containsKey(curMaterial)) {
							mat = mapOfMaterials.get(curMaterial);
						}
					}
					dumpMaterialGroup(points, normals, texCoords, faces, materials, mat, maxPointIndex, maxNormalIndex, maxTexCoordsIndex);
					curMaterial = data;
				}
				else if(key.equalsIgnoreCase("v")) {
					float[] vec = parseFloats(splitData);
					if(vec.length < 3) {
						return false;
					}
					
					points.add(new Vec3f(vec[0], vec[1], vec[2]));
				}
				else if(key.equalsIgnoreCase("vn")) {
					float[] vec = parseFloats(splitData);
					if(vec.length < 3) {
						return false;
					}
					Vec3f n = new Vec3f(vec[0], vec[1], vec[2]);
					n.normalise();
					
					normals.add(n);
				}
				else if(key.equalsIgnoreCase("vt")) {
					float[] vec = parseFloats(splitData);
					if(vec.length < 2) {
						return false;
					}
					
					texCoords.add(new Vec2f(vec[0], vec[1]));
				}
				else if(key.equalsIgnoreCase("f")) {
					if(splitData.length < 3) {
						System.out.println("Face with less than 3 vertices");
						return false;
					}
					
					List<Integer> pointIndices = new ArrayList<>();
					List<Integer> normalIndices = null;
					List<Integer> texCoordsIndices = null;
					
					for(int i = 0; i < splitData.length; i++) {
						String[] pts = splitData[i].split("/");
						if(pts.length > 0) {
							int vert = Integer.parseInt(pts[0]);
							vert = vert < 0 ? points.size() + vert : vert - 1;
							pointIndices.add(vert);
							maxPointIndex = Math.max(maxPointIndex, vert);
						}
						if(pts.length > 1 && !pts[1].equals("")) {
							if(texCoordsIndices == null) {
								texCoordsIndices = new ArrayList<>();
							}
							int text = Integer.parseInt(pts[1]);
							text = text < 0 ? texCoords.size() + text : text - 1;
							texCoordsIndices.add(text);
							maxTexCoordsIndex = Math.max(maxTexCoordsIndex, text);
						}
						if(pts.length > 2 && !pts[2].equals("")) {
							if(normalIndices == null) {
								normalIndices = new ArrayList<>();
							}
							int norm = Integer.parseInt(pts[2]);
							norm = norm < 0 ? normals.size() + norm : norm - 1;
							normalIndices.add(norm);
							maxNormalIndex = Math.max(maxNormalIndex, norm);
						}
					}
					
					triangulateFace(pointIndices, normalIndices, texCoordsIndices, faces);
				}
				else if(key.equalsIgnoreCase("g") || key.equalsIgnoreCase("o")) {
					Material mat = null;
					if(!curMaterial.isEmpty()) {
						if(mapOfMaterials.containsKey(curMaterial)) {
							mat = mapOfMaterials.get(curMaterial);
						}
					}
					dumpMaterialGroup(points, normals, texCoords, faces, materials, mat, maxPointIndex, maxNormalIndex, maxTexCoordsIndex);
				}
				else {
					continue;
				}
			}
			
			if(materials.size() > 0) {
				model = new CMCModel(materials);
				model.normalize();
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	private float[] parseFloats(String[] data)
    {
        float[] ret = new float[data.length];
        for (int i = 0; i < data.length; i++)
            ret[i] = Float.parseFloat(data[i]);
        return ret;
    }
	
	private void dumpMaterialGroup(List<Vec3f> points,
			List<Vec3f> normals,
			List<Vec2f> texCoords,
			List<Face> faces,
			List<MaterialGroup> materialGroups,
			Material material,
			int maxPointIndex,
			int maxNormalIndex,
			int maxTexCoordsIndex) {
		if(points.size() == 0 || faces.size() == 0) {
			return;
		}
		
		List<Vec3f> pointsToWrite = new ArrayList<>();
		
		if(maxPointIndex >= points.size()) {
			System.out.println("Error2");
			return;
		}
		
		List<Vec3f> normalsToWrite = null;
		if(maxNormalIndex > 0) {
			normalsToWrite = new ArrayList<>();
			
			if(maxNormalIndex >= normals.size()) {
				System.out.println("Error3");
				return;
			}
		}
		
		List<Vec2f> texCoordsToWrite = null;
		if(maxTexCoordsIndex > 0) {
			texCoordsToWrite = new ArrayList<>();
			
			if(maxTexCoordsIndex >= texCoords.size()) {
				System.out.println("Error4");
				return;
			}
		}
		
		List<Vec3i> faceIndicesToWrite = new ArrayList<>();
		Map<Integer, List<Vec3i>> mapOfProcessedPoints = new HashMap<>();
		int maxIndex = 0;

		for(Face face : faces) {
			Vec3i faceIndixes = new Vec3i();
			for(int i = 0; i < 3; i++) {
				int pointIndex = 0;

				boolean isNewVertex = false;
				if(!mapOfProcessedPoints.containsKey(face.pointIndices.get(i))) {
					isNewVertex = true;
					mapOfProcessedPoints.put(face.pointIndices.get(i), new ArrayList<>());
				}
				if(!isNewVertex) {
					isNewVertex = true;
					List<Vec3i> l = mapOfProcessedPoints.get(face.pointIndices.get(i));
					int normalI = face.normalIndices != null ? face.normalIndices.get(i) : 0;
					int texCoordsI = face.texCoordsIndices != null ? face.texCoordsIndices.get(i) : 0;
					for(Vec3i li : l) {
						if(li.y == normalI && li.z == texCoordsI) {
							pointIndex = li.x;
							isNewVertex = false;
							break;
						}
					}
				}
				
				if(isNewVertex) {
					List<Vec3i> l = mapOfProcessedPoints.get(face.pointIndices.get(i));
					Vec3i pToAdd = new Vec3i();
					pToAdd.x = maxIndex;
					pointsToWrite.add(points.get(face.pointIndices.get(i)));
					if(face.normalIndices != null) {
						pToAdd.y = face.normalIndices.get(i);
						normalsToWrite.add(normals.get(face.normalIndices.get(i)));
					}
					if(face.texCoordsIndices != null) {
						pToAdd.z = face.texCoordsIndices.get(i);
						texCoordsToWrite.add(texCoords.get(face.texCoordsIndices.get(i)));
					}
					l.add(pToAdd);
					
					pointIndex = maxIndex;

					maxIndex++;
				}
				
				faceIndixes.set(i, pointIndex);
			}

			faceIndicesToWrite.add(faceIndixes);
		}

		MaterialGroup matToAdd = new MaterialGroup();
		matToAdd.setData(faceIndicesToWrite, pointsToWrite, null, normalsToWrite, texCoordsToWrite);
		//matToAdd.setData(faceIndicesToWrite, pointsToWrite, null, null, texCoordsToWrite);
		//matToAdd.setData(faceIndicesToWrite, pointsToWrite, null, null, null);
		if(material != null) {
			if(material.w != 0 && material.h != 0 && material.texture != null) {
				matToAdd.setTexture(material.texture, material.w, material.h);
			}
		}
		materialGroups.add(matToAdd);
		
		faces.clear();
	}
	
	private void triangulateFace(List<Integer> pointIndices, List<Integer> normalIndices, List<Integer> texCoordsIntegers, List<Face> faces) {
		Vec3i pi = new Vec3i();
		Vec3i ni = normalIndices == null ? null : new Vec3i();
		Vec3i ti = texCoordsIntegers == null ? null : new Vec3i();
		for(int i = 0; i < pointIndices.size() - 2; i++) {
			pi.set(pointIndices.get(0), pointIndices.get(i + 1), pointIndices.get(i + 2));
			if(ni != null) {
				ni.set(normalIndices.get(0), normalIndices.get(i + 1), normalIndices.get(i + 2));
			}
			if(ti != null) {
				ti.set(texCoordsIntegers.get(0), texCoordsIntegers.get(i + 1), texCoordsIntegers.get(i + 2));
			}
			Face face = new Face(pi, ni, ti);
			faces.add(face);
		}
	}
	
	private Path getAbsolutePath(String path, String parent) {
		Path filePath = Paths.get(path);
		if(!filePath.isAbsolute()) {
			Path parentPath = Paths.get(parent);
			filePath = parentPath.resolve(path);
		}
		
		return filePath;
	}
	
	private void parseMaterials(String path) {
		Path filePath = getAbsolutePath(path, CMCData.instance.lastLoadedmodelPath);
		
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
			InputStreamReader streamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			
			String curMaterialName = "";
			Material curMaterial = new Material();
			
			for(;;) {
				String line = bufferedReader.readLine();
				
				if(line == null) {
					addMaterial(curMaterialName, curMaterial);
					break;
				}
				
				line.trim();
				
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				
				String[] fields = WHITE_SPACE.split(line, 2);
				String key = fields[0];
				String data = fields[1];
				String[] splitData = WHITE_SPACE.split(data);
				
				if(key.equalsIgnoreCase("newmtl")) {
					addMaterial(curMaterialName, curMaterial);
					curMaterialName = data;
				}
				else if(key.equalsIgnoreCase("Kd")) {
					float[] vec = parseFloats(splitData);
					curMaterial.Kd.set(vec[0], vec[1], vec[2]);
				}
				else if(key.equalsIgnoreCase("map_Kd")) {
					curMaterial.texturePath = getAbsolutePath(data, filePath.getParent().toString()).toString();
					loadImage(curMaterial);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addMaterial(String name, Material material) {
		if(name.isEmpty()) {
			return;
		}
		
		mapOfMaterials.put(name, new Material(material));
		material.clear();
	}
	
	private void loadImage(Material material) {
		if(material.texturePath.isEmpty()) {
			return;
		}
		File imageFile = new File(material.texturePath);
		try {
			BufferedImage image = ImageIO.read(imageFile);
			int w = image.getWidth();
			int h = image.getHeight();
			final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			int chNum = image.getAlphaRaster() == null ? 3 : 4;
			material.texture = new int[w * h];
			material.w = w;
			material.h = h;
			for(int i = 0; i < h; i++) {
				for(int j = 0; j < w; j++) {
					int a = chNum == 4 ? pixels[((h - i - 1) * w + j) * chNum] & 0xff : 255;
					int b = pixels[((h - i - 1) * w + j) * chNum + chNum - 3] & 0xff;
					int g = pixels[((h - i - 1) * w + j) * chNum + chNum - 3 + 1] & 0xff;
					int r = pixels[((h - i - 1) * w + j) * chNum + chNum - 3 + 2] & 0xff;
					material.texture[i * w + j] = a * 256 * 256 * 256 + r * 256 * 256 + g * 256 + b;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
