package com.andreid278.cmc.client.gui;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.ModelReader;
import com.andreid278.cmc.common.ModelsInfo.ModelInfo;
import com.andreid278.cmc.common.network.DataLoadingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ModelsSelectionGui extends GuiScreen {
	public ModelsViewer modelsViewer;
	
	@Override
	public void initGui() {
		super.initGui();
		
		modelsViewer = new ModelsViewer(10, 10, 200, 200);
		//if(!ModelStorage.instance.isEmpty()) {
		//	modelsViewer.setModel(ModelStorage.instance.getAnyModel());
		//}
		
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, 300, 100, "Test save"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		modelsViewer.draw(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		modelsViewer.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		modelsViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		modelsViewer.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		modelsViewer.handleMouseInput();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
		case 0:
			UUID uuid = UUID.randomUUID();
			ModelReader reader = new ModelReader(uuid, true);
			CMCModel model = reader.getModel();
			model.saveToFile(uuid.toString());
			ModelStorage.instance.addModel(uuid, model);
			DataLoadingHelper.sendDataToServer(uuid, Minecraft.getMinecraft().player.getName(), "Name", true);
			break;
		}
	}
	
	public void updateModelsInfo(Map<UUID, ModelInfo> info) {
		modelsViewer.updateModelsInfo(info);
	}
	
	public void updateModelsCount(int count) {
		modelsViewer.updateModelsCount(count);
	}
}
