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
import com.andreid278.cmc.client.model.ModelReader;
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

public class TestBlock extends Block implements ITileEntityProvider {

	public TestBlock(Material materialIn) {
		super(materialIn);
		this.setCreativeTab(CMCCreativeTab.tab);
		this.setUnlocalizedName("testblock");
		this.setRegistryName(CMC.MODID + ":testblock");
		this.setDefaultState(this.blockState.getBaseState());
		this.setHardness(1.5f);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			/*UUID uuid = UUID.randomUUID();
			ModelReader reader = new ModelReader(uuid);
			CMCModel model = reader.getModel();
			model.saveToFile(uuid.toString());
			ModelStorage.instance.addModel(uuid, model);
			
			((TETest) worldIn.getTileEntity(pos)).uuid = uuid;
			
			if(!CMCData.instance.playersModels.containsKey(playerIn.getUniqueID())) {
				CMCData.instance.playersModels.put(playerIn.getUniqueID(), new ArrayList<>());
			}
			else if(CMCData.instance.playersModels.get(playerIn.getUniqueID()) == null) {
				CMCData.instance.playersModels.put(playerIn.getUniqueID(), new ArrayList<>());
			}
			
			List<CMCModelOnPlayer> modelsList = CMCData.instance.playersModels.get(playerIn.getUniqueID());
			modelsList.add(new CMCModelOnPlayer(model));*/
			
			playerIn.openGui(CMC.instance, GuiHandler.MODELS_SELECTION_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
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
	}

}
