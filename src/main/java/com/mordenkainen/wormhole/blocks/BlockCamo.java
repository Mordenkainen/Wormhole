package com.mordenkainen.wormhole.blocks;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

// Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Wormhole
import com.mordenkainen.wormhole.tileentity.ICamo;

public abstract class BlockCamo extends BlockContainer {

	protected BlockCamo(Material material) {
		super(material);
	}

	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof ICamo) {
        	ICamo tile = (ICamo) tileEntity;
            Block blockCamoAs = tile.getCamo();
            if (blockCamoAs != null) {
                return blockCamoAs.getIcon(side, tile.getCamoMeta());
            }
        }
        return super.getIcon(world, x, y, z, side);
    }
	
	@Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof ICamo) {
        	ICamo tile = (ICamo) tileEntity;
            Block blockCamoAs = tile.getCamo();
            if (blockCamoAs != null) {
                return blockCamoAs.colorMultiplier(world, x, y, z);
            }
        }
        return super.colorMultiplier(world, x, y, z);
    }
}
