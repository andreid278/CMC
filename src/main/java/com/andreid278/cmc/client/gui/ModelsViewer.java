package com.andreid278.cmc.client.gui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.common.ModelsInfo;
import com.andreid278.cmc.common.ModelsInfo.ModelInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class ModelsViewer extends Gui {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public static int rowCount = 3;
	public static int colCount = 3;
	
	public int curRow = -1;
	public ModelViewer[] models = new ModelViewer[rowCount * colCount];
	public int modelWidth;
	public int modelHeight;
	public int margin = 5;
	
	public int curMouseX;
	public int curMouseY;
	
	public ModelsViewer(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		modelWidth = (w - (colCount + 1) * margin) / colCount;
		modelHeight = (h - (rowCount + 1) * margin) / rowCount;
		
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < colCount; j++) {
				models[i * colCount + j] = new ModelViewer(x + margin + (modelWidth + margin) * j, y + margin + (modelHeight + margin) * i, modelWidth, modelHeight);
			}
		}
		
		setRow(0);
	}
	
	public void draw(Minecraft mc, int mouseX, int mouseY) {
		curMouseX = mouseX;
		curMouseY = mouseY;
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableTexture2D();
		
		GuiUtils.drawFilledRectangle(x, y, x + w, y + h, 255, 255, 255);
		
		for(int i = 0; i < rowCount * colCount; i++) {
			models[i].draw(mc, mouseX, mouseY);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(!isMouseInside(mouseX, mouseY)) {
			return;
		}
		
		for(int i = 0; i < rowCount * colCount; i++) {
			models[i].mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(isMouseInside(mouseX, mouseY)) {
			for(int i = 0; i < rowCount * colCount; i++) {
				models[i].mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
			return true;
		}
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		for(int i = 0; i < rowCount * colCount; i++) {
			models[i].mouseReleased(mouseX, mouseY, state);
		}
	}
	
	public void handleMouseInput() {
		if(!isMouseInside(curMouseX, curMouseY)) {
			return;
		}
		
		for(int i = 0; i < rowCount * colCount; i++) {
			models[i].handleMouseInput();
		}
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}
	
	public void setRow(int row) {
		if(row == curRow) {
			return;
		}
		
		curRow = row;
		
		Map<UUID, ModelInfo> modelsInfo = ModelsInfo.instance.getAll();
		Set<Entry<UUID, ModelInfo>> entrySet = modelsInfo.entrySet();
		Iterator<Entry<UUID, ModelInfo>> it = entrySet.iterator();
		
		int startIndex = row * colCount;
		int finishIndex = startIndex + rowCount * colCount - 1;
		
		int i = 0;
		while(it.hasNext()) {
			if(i >= startIndex) {
				UUID uuid = it.next().getKey();
				if(ModelStorage.instance.hasModel(uuid)) {
					models[i - startIndex].setModel(ModelStorage.instance.getModel(uuid));
				}
				else {
					models[i - startIndex].setModel(null);
				}
			}
			
			i++;
			if(i > finishIndex) {
				break;
			}
		}
		
		for(int j = i; j <= finishIndex; j++) {
			models[j - startIndex].setModel(null);
		}
	}
}
