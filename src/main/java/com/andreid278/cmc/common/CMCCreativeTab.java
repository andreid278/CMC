package com.andreid278.cmc.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CMCCreativeTab extends CreativeTabs {

	public CMCCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(Items.APPLE);
	}
	
	public static final CMCCreativeTab tab = new CMCCreativeTab("CMC");
}
