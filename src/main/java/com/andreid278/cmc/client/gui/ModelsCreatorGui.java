package com.andreid278.cmc.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.reader.ConstructReader;
import com.andreid278.cmc.client.model.reader.OBJReader;
import com.andreid278.cmc.client.model.reader.PrimitiveReader;
import com.andreid278.cmc.client.model.reader.PrimitiveReader.PrimitiveType;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.network.DataLoadingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ModelsCreatorGui extends GuiScreen {
	public ModelViewer modelViewer;
	
	@Override
	public void initGui() {
		super.initGui();
		
		modelViewer = new ModelViewer(40, 10, 200, 200);
		modelViewer.showPlayer(true);
		modelViewer.canAttachTransformControl = true;
		
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, 250, 10, 20, 20, "T"));
		this.buttonList.add(new GuiButton(1, 250, 30, 20, 20, "R"));
		this.buttonList.add(new GuiButton(2, 250, 50, 20, 20, "S"));
		this.buttonList.add(new GuiButton(3, 10, 10, 20, 20, "Ci"));
		this.buttonList.add(new GuiButton(4, 10, 30, 20, 20, "Cy"));
		this.buttonList.add(new GuiButton(99, 250, 70, 50, 20, "Import"));
		this.buttonList.add(new GuiButton(100, 250, 100, 50, 20, "Save"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		modelViewer.draw(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		modelViewer.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		
		modelViewer.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		
		modelViewer.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		
		modelViewer.handleMouseInput();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
		case 0:
			modelViewer.transformControl.setMode(0);
			break;
		case 1:
			modelViewer.transformControl.setMode(1);
			break;
		case 2:
			modelViewer.transformControl.setMode(2);
			break;
		case 3:
			PrimitiveReader readerCircle = new PrimitiveReader(PrimitiveType.Circle);
			readerCircle.read();
			modelViewer.addObject(readerCircle.getModel());
			break;
		case 4:
			PrimitiveReader readerCylinder = new PrimitiveReader(PrimitiveType.Cylinder);
			readerCylinder.read();
			modelViewer.addObject(readerCylinder.getModel());
			break;
		case 99:
			JFileChooser fileopen = new JFileChooser();
			fileopen.setAcceptAllFileFilterUsed(false);
			if(CMCData.instance.lastLoadedmodelPath != "") {
				File lastLoadedmodelPath = new File(CMCData.instance.lastLoadedmodelPath);
				if(lastLoadedmodelPath.exists() && lastLoadedmodelPath.isDirectory()) {
					fileopen.setCurrentDirectory(lastLoadedmodelPath);
				}
			}
			fileopen.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "Obj files";
				}
				
				@Override
				public boolean accept(File f) {
					return f != null && (f.isDirectory() ||
							f.getName().toLowerCase().endsWith(".obj"));
				}
			});
			int ret = fileopen.showDialog(null, "Load");
			if(ret == JFileChooser.APPROVE_OPTION) {
				CMCData.instance.lastLoadedmodelPath = fileopen.getSelectedFile().getParent();
				OBJReader objReader = new OBJReader(fileopen.getSelectedFile().getAbsolutePath());
				if(objReader.read()) {
					modelViewer.addObject(objReader.getModel());
				}
			}
			break;
		case 100:
			UUID uuid = UUID.randomUUID();
			ConstructReader constructReader = new ConstructReader(modelViewer.objects);
			if(constructReader.read()) {
				constructReader.getModel().saveToFile(uuid.toString());
				ModelStorage.instance.addModel(uuid, constructReader.getModel());
				DataLoadingHelper.sendDataToServer(uuid, Minecraft.getMinecraft().player.getName(), "Name", true);
			}
			break;
		}
	}
}
