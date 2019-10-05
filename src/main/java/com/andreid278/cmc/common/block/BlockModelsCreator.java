package com.andreid278.cmc.common.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.andreid278.cmc.CMC;
import com.andreid278.cmc.client.ModelStorage;
import com.andreid278.cmc.client.gui.GuiHandler;
import com.andreid278.cmc.client.model.CMCModel;
import com.andreid278.cmc.client.model.CMCModelOnPlayer;
import com.andreid278.cmc.common.CMCCreativeTab;
import com.andreid278.cmc.common.CMCData;
import com.andreid278.cmc.common.tileentity.TETest;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;

public class BlockModelsCreator extends Block {

	public BlockModelsCreator(Material materialIn) {
		super(materialIn);
		this.setCreativeTab(CMCCreativeTab.tab);
		this.setUnlocalizedName("creatorblock");
		this.setRegistryName(CMC.MODID + ":creatorblock");
		this.setDefaultState(this.blockState.getBaseState());
		this.setHardness(1.5f);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			playerIn.openGui(CMC.instance, GuiHandler.MODELS_CREATOR_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	/*@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TETest();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return false;
	}*/

}
