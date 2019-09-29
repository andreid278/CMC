package com.andreid278.cmc.client.gui;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.ModelReader;
import com.andreid278.cmc.client.model.CMCModelOnPlayer.BodyPart;
import com.andreid278.cmc.common.ModelsInfo.ModelInfo;
import com.andreid278.cmc.common.network.DataLoadingHelper;
import com.andreid278.cmc.utils.Vec3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Matrix4f;

public class ModelsSelectionGui extends GuiScreen {
	public ModelsViewer modelsViewer;
	public ModelViewer selectedModelViewer;
	public UUID selectedModelUUID;
	public BodyPart bodyPart = BodyPart.Torso;
	
	@Override
	public void initGui() {
		super.initGui();
		
		modelsViewer = new ModelsViewer(this, 10, 10, 200, 200);
		modelsViewer.afterCreation();
		selectedModelViewer = new ModelViewer(220, 10, 200, 200);
		selectedModelViewer.showPlayer(true);
		selectedModelViewer.canAttachTransformControl = true;
		selectedModelUUID = null;
		//if(!ModelStorage.instance.isEmpty()) {
		//	modelsViewer.setModel(ModelStorage.instance.getAnyModel());
		//}
		
		this.buttonList.clear();
		
		this.buttonList.add(new GuiButton(0, 10, 210, "Test save"));
		
		this.buttonList.add(new GuiButton(1, 220, 210, "Assign to the player"));
		
		this.buttonList.add(new GuiButton(2, 420, 10, 40, 20, "Head"));
		this.buttonList.add(new GuiButton(3, 420, 30, 40, 20, "Torso"));
		this.buttonList.add(new GuiButton(4, 420, 50, 40, 20, "LArm"));
		this.buttonList.add(new GuiButton(5, 420, 70, 40, 20, "RArm"));
		this.buttonList.add(new GuiButton(6, 420, 90, 40, 20, "LLeg"));
		this.buttonList.add(new GuiButton(7, 420, 110, 40, 20, "RLeg"));
		
		this.buttonList.add(new GuiButton(8, 420, 140, 20, 20, "T"));
		this.buttonList.add(new GuiButton(9, 420, 160, 20, 20, "R"));
		this.buttonList.add(new GuiButton(10, 420, 180, 20, 20, "S"));
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		modelsViewer.draw(mc, mouseX, mouseY);
		selectedModelViewer.draw(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		modelsViewer.mouseClicked(mouseX, mouseY, mouseButton);
		selectedModelViewer.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		modelsViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		selectedModelViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		modelsViewer.mouseReleased(mouseX, mouseY, state);
		selectedModelViewer.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		modelsViewer.handleMouseInput();
		selectedModelViewer.handleMouseInput();
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
			if(selectedModelUUID != null && selectedModelViewer.modelObject.isModelNotNull()) {
				Matrix4f location = selectedModelViewer.modelObject.transformation;
				Matrix4f rot = new Matrix4f();
				rot.setIdentity();
				//Matrix4f.rotate((float)Math.PI, new Vec3f(1, 0, 0), rot, rot);
				//Matrix4f.rotate((float)Math.PI, new Vec3f(0, 1, 0), rot, rot);
				Matrix4f.mul(rot, location, rot);
				DataLoadingHelper.chooseModel(selectedModelUUID, rot, bodyPart);
			}
			break;
		case 2:
			bodyPart = BodyPart.Head;
			break;
		case 3:
			bodyPart = BodyPart.Torso;
			break;
		case 4:
			bodyPart = BodyPart.LeftArm;
			break;
		case 5:
			bodyPart = BodyPart.RightArm;
			break;
		case 6:
			bodyPart = BodyPart.LeftLeg;
			break;
		case 7:
			bodyPart = BodyPart.RightLeg;
			break;
		case 8:
			selectedModelViewer.transformControl.setMode(0);
			break;
		case 9:
			selectedModelViewer.transformControl.setMode(1);
			break;
		case 10:
			selectedModelViewer.transformControl.setMode(2);
			break;
		}
	}
	
	public void updateModelsInfo(Map<UUID, ModelInfo> info) {
		modelsViewer.updateModelsInfo(info);
	}
	
	public void updateModelsCount(int count) {
		if(modelsViewer == null) System.out.println("1");
		modelsViewer.updateModelsCount(count);
	}
	
	public void viewModel(UUID uuid) {
		if(uuid == null) {
			return;
		}
		if(ModelStorage.instance.hasModel(uuid)) {
			selectedModelViewer.setModel(ModelStorage.instance.getModel(uuid));
			selectedModelUUID = uuid;
		}
		else {
			selectedModelViewer.setModel(null);
			selectedModelUUID = null;
		}
	}
}
