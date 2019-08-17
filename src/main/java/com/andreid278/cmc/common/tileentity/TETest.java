package com.andreid278.cmc.common.tileentity;

import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TETest extends TileEntity {
	public UUID uuid = null;
	
	public ITextComponent getDisplayName() {
		return new TextComponentString("test");
	}
}
