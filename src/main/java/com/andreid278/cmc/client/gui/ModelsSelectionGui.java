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
import net.minecraft.client.renderer.Matrix4f;

public class ModelsSelectionGui extends GuiScreen {
	public ModelsViewer modelsViewer;
	public ModelViewer choosenModelViewer;
	public UUID choosenModelUUID;
	
	@Override
	public void initGui() {
		super.initGui();
		
		modelsViewer = new ModelsViewer(this, 10, 10, 200, 200);
		choosenModelViewer = new ModelViewer(250, 10, 100, 100);
		choosenModelUUID = null;
		//if(!ModelStorage.instance.isEmpty()) {
		//	modelsViewer.setModel(ModelStorage.instance.getAnyModel());
		//}
		
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, 300, 100, "Test save"));
		this.buttonList.add(new GuiButton(1, 300, 200, "Assign to the player"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		modelsViewer.draw(mc, mouseX, mouseY);
		choosenModelViewer.draw(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		modelsViewer.mouseClicked(mouseX, mouseY, mouseButton);
		choosenModelViewer.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		modelsViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		choosenModelViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		modelsViewer.mouseReleased(mouseX, mouseY, state);
		choosenModelViewer.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		modelsViewer.handleMouseInput();
		choosenModelViewer.handleMouseInput();
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
		case 1:
			if(choosenModelUUID != null) {
				Matrix4f location = new Matrix4f();
				location.setIdentity();
				DataLoadingHelper.chooseModel(choosenModelUUID, location);
			}
			break;
		}
	}
	
	public void updateModelsInfo(Map<UUID, ModelInfo> info) {
		modelsViewer.updateModelsInfo(info);
	}
	
	public void updateModelsCount(int count) {
		modelsViewer.updateModelsCount(count);
	}
	
	public void viewModel(UUID uuid) {
		if(uuid == null) {
			return;
		}
		if(ModelStorage.instance.hasModel(uuid)) {
			choosenModelViewer.setModel(ModelStorage.instance.getModel(uuid));
			choosenModelUUID = uuid;
		}
		else {
			choosenModelViewer.setModel(null);
			choosenModelUUID = null;
		}
	}
}
