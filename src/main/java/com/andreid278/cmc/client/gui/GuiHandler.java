package com.andreid278.cmc.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int MODELS_SELECTION_GUI = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case MODELS_SELECTION_GUI:
			break;
		//case PRINTER_GUI:
		//	return new PrinterContainer(player.inventory, (TEPrinter)world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case MODELS_SELECTION_GUI:
			return new ModelsSelectionGui();
		}
		return null;
	}
}
