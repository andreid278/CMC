package com.andreid278.cmc.client.gui;

import java.io.IOException;
import java.util.UUID;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.ModelReader;
import com.andreid278.cmc.common.network.DataLoadingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ModelsCreatorGui extends GuiScreen {
	@Override
	public void initGui() {
		super.initGui();
		
		//modelViewer = new ModelViewer(10, 10, 200, 200);
		//if(!ModelStorage.instance.isEmpty()) {
		//	modelViewer.setModel(ModelStorage.instance.getAnyModel());
		//}
		
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, 300, 100, "Test save"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		//modelViewer.draw(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		//modelViewer.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		//modelViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		//modelViewer.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		//modelViewer.handleMouseInput();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
		case 0:
			UUID uuid = UUID.randomUUID();
			ModelReader reader = new ModelReader(uuid, true);
			CMCModel model = reader.getModel();
			model.saveToFile(uuid.toString());
			DataLoadingHelper.sendDataToServer(uuid, Minecraft.getMinecraft().player.getName(), "Name", true);
			break;
		}
	}
}
