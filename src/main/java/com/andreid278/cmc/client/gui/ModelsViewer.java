package com.andreid278.cmc.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.andreid278.cmc.common.network.DataLoadingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class ModelsViewer extends Gui {
	ModelsSelectionGui parent;
	public int x;
	public int y;
	public int w;
	public int h;
	
	public static int rowCount = 3;
	public static int colCount = 3;
	
	public ModelViewer[] models = new ModelViewer[rowCount * colCount];
	public int modelWidth;
	public int modelHeight;
	public int margin = 5;
	
	public int bottomHeight = 10;
	
	public int curMouseX;
	public int curMouseY;
	
	public int modelsCount = 0;
	public boolean isCurPageInited = false;
	public int curPage = -1;
	public int curNumberOfInitedModels = 0;
	public boolean isModelsCountInited = false;
	
	public Map<UUID, ModelInfo> modelsInfo = new LinkedHashMap<UUID, ModelInfo>();
	
	public String pagesStr = "";
	private class Page {
		public int pageIndex;
		public int minX;
		public int minY;
		public int maxX;
		public int maxY;
		
		public Page(int pageIndex, int minX, int minY, int maxX, int maxY) {
			this.pageIndex = pageIndex;
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}
	}
	public List<Page> pagesIndices = new ArrayList<>();
	
	public ModelsViewer(ModelsSelectionGui parent, int x, int y, int w, int h) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		modelWidth = (w - (colCount + 1) * margin) / colCount;
		modelHeight = (h - bottomHeight - (rowCount + 1) * margin) / rowCount;
		
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < colCount; j++) {
				models[i * colCount + j] = new ModelViewer(x + margin + (modelWidth + margin) * j, y + margin + (modelHeight + margin) * i, modelWidth, modelHeight);
			}
		}
		
		isCurPageInited = false;
		curPage = -1;
		curNumberOfInitedModels = 0;
		isModelsCountInited = false;
	}
	
	public void afterCreation() {
		DataLoadingHelper.requestModelsCount();
	}
	
	public void draw(Minecraft mc, int mouseX, int mouseY) {
		curMouseX = mouseX;
		curMouseY = mouseY;
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableTexture2D();
		
		GuiUtils.drawFilledRectangle(x, y, x + w, y + h, 255, 255, 255);
		
		if(!isModelsCountInited || !isCurPageInited) {
			return;
		}
		
		if(curNumberOfInitedModels < modelsInfo.size()) {
			Set<Entry<UUID, ModelInfo>> entrySet = modelsInfo.entrySet();
			Iterator<Entry<UUID, ModelInfo>> it = entrySet.iterator();
			int i = 0;
			while(it.hasNext()) {
				if(i >= rowCount * colCount) {
					curNumberOfInitedModels = modelsInfo.size();
					break;
				}
				
				UUID uuid = it.next().getKey();
				
				if(i == curNumberOfInitedModels) {
					if(ModelStorage.instance.hasModel(uuid)) {
						models[i].setModel(ModelStorage.instance.getModel(uuid));
						curNumberOfInitedModels++;
					}
					else {
						DataLoadingHelper.requestData(uuid);
						break;
					}
				}
				
				i++;
			}
		}
		
		for(int i = 0; i < rowCount * colCount; i++) {
			models[i].draw(mc, mouseX, mouseY);
		}
		
		drawPagesNumbers(mc, mouseX, mouseY);
	}
	
	private void drawPagesNumbers(Minecraft mc, int mouseX, int mouseY) {
		if(curPage == -1) {
			return;
		}
		
		
		mc.fontRenderer.drawString(pagesStr, x + w / 2 - mc.fontRenderer.getStringWidth(pagesStr) / 2, y + h - bottomHeight, 0x000000);
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(!isMouseInside(mouseX, mouseY)) {
			return;
		}
		
		for(int i = 0; i < rowCount * colCount; i++) {
			if(models[i].isMouseInside(mouseX, mouseY)) {
				if(models[i].model != null) {
					Set<Entry<UUID, ModelInfo>> entrySet = modelsInfo.entrySet();
					Iterator<Entry<UUID, ModelInfo>> it = entrySet.iterator();
					int j = 0;
					while(it.hasNext()) {
						if(j == i) {
							parent.viewModel(it.next().getKey());
							break;
						}
						else {
							j++;
							it.next();
						}
					}
				}
				break;
			}
		}
		
		if(isMouseInsidePages(mouseX, mouseY)) {
			for(int i = 0; i < pagesIndices.size(); i++) {
				Page page = pagesIndices.get(i);
				if(isMouseOverlapPage(mouseX, mouseY, page)) {
					setPage(page.pageIndex);
					break;
				}
			}
		}
	}
	
	public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		/*if(isMouseInside(mouseX, mouseY)) {
			for(int i = 0; i < rowCount * colCount; i++) {
				models[i].mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
			return true;
		}*/
		return false;
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		/*for(int i = 0; i < rowCount * colCount; i++) {
			models[i].mouseReleased(mouseX, mouseY, state);
		}*/
	}
	
	public void handleMouseInput() {
		if(!isMouseInside(curMouseX, curMouseY)) {
			return;
		}
		
		/*for(int i = 0; i < rowCount * colCount; i++) {
			models[i].handleMouseInput();
		}*/
	}
	
	public boolean isMouseInside(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
	}
	
	public boolean isMouseInsidePages(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + w && mouseY >= y + h - bottomHeight && mouseY < y + h;
	}
	
	public boolean isMouseOverlapPage(int mouseX, int mouseY, Page page) {
		return mouseX >= page.minX && mouseX < page.maxX && mouseY >= page.minY && mouseY < page.maxY;
	}
	
	public void setPage(int page) {
		if(curPage == page) {
			return;
		}
		
		System.out.println("setPage");
		
		curPage = page;
		isCurPageInited = false;
		curNumberOfInitedModels = 0;
		modelsInfo.clear();
		
		DataLoadingHelper.requestModelsInfo(curPage * rowCount * colCount, rowCount * colCount);
		
		for(int j = 0; j < rowCount * colCount; j++) {
			models[j].setModel(null);
			models[j].resetTransformation();
		}
		
		pagesIndices.clear();
		
		int range = 2;
		
		int startIndex = Math.max(0, curPage - range) + 1;
		int finishIndex = Math.min((modelsCount - 1) / (rowCount * colCount), curPage + range) + 1;
		
		pagesStr = "";
		String str = "";
		
		Minecraft mc = Minecraft.getMinecraft();
		
		str = "<<";
		pagesIndices.add(new Page(0,
				mc.fontRenderer.getStringWidth(pagesStr),
				0,
				mc.fontRenderer.getStringWidth(str),
				mc.fontRenderer.FONT_HEIGHT));
		pagesStr = str + " ";
		str += " <";
		pagesIndices.add(new Page(Math.max(0, curPage - 1),
				mc.fontRenderer.getStringWidth(pagesStr),
				0,
				mc.fontRenderer.getStringWidth(str),
				mc.fontRenderer.FONT_HEIGHT));
		
		for(int i = startIndex; i <= finishIndex; i++) {
			pagesStr = str + " ";
			str += " " + i;
			pagesIndices.add(new Page(i - 1,
					mc.fontRenderer.getStringWidth(pagesStr),
					0,
					mc.fontRenderer.getStringWidth(str),
					mc.fontRenderer.FONT_HEIGHT));
		}
		
		pagesStr = str + " ";
		str += " >";
		pagesIndices.add(new Page(Math.min((modelsCount - 1) / (rowCount * colCount), curPage + 1),
				mc.fontRenderer.getStringWidth(pagesStr),
				0,
				mc.fontRenderer.getStringWidth(str),
				mc.fontRenderer.FONT_HEIGHT));
		
		pagesStr = str + " ";
		str += " >>";
		pagesIndices.add(new Page((modelsCount - 1) / (rowCount * colCount),
				mc.fontRenderer.getStringWidth(pagesStr),
				0,
				mc.fontRenderer.getStringWidth(str),
				mc.fontRenderer.FONT_HEIGHT));
		
		pagesStr = str;
		
		int xOffset = x + w / 2 - mc.fontRenderer.getStringWidth(pagesStr) / 2;
		int yOffset = y + h - bottomHeight;
		for(int i = 0; i < pagesIndices.size(); i++) {
			Page pageInfo = pagesIndices.get(i);
			pageInfo.minX += xOffset;
			pageInfo.minY += yOffset;
			pageInfo.maxX += xOffset;
			pageInfo.maxY += yOffset;
		}
	}
	
	public void updateModelsInfo(Map<UUID, ModelInfo> info) {
		System.out.println("Update models viewer");
		
		modelsInfo.putAll(info);
		
		isCurPageInited = true;
	}
	
	public void updateModelsCount(int count) {
		modelsCount = count;
		isModelsCountInited = true;
		
		setPage(0);
	}
}
