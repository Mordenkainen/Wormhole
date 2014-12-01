// TODO test OPs breaking this block
package com.mordenkainen.wormhole.blocks;

// Java
import java.util.ArrayList;
import java.util.UUID;

// Minecraft
import com.mojang.authlib.GameProfile;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Wormhole
import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.tileentity.TileEntityPlayerLink;

public class BlockPlayerLink extends BlockCamo {
	
	public BlockPlayerLink() {
		super(Material.iron);
		setBlockName(Wormhole.MODID + ".playerlink");
		setHardness(2.0F);
		setResistance(10.0F);
		setHarvestLevel("pickaxe", 2);
		setStepSound(soundTypeMetal);
		setBlockTextureName(Wormhole.MODID + ":playerlink");
		setCreativeTab(Wormhole.modTab);
	}
	
	// BlockContainer
	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityPlayerLink();
	}

	// Block
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntityPlayerLink te = (TileEntityPlayerLink)world.getTileEntity(x, y, z);
		if (te != null) {
			te.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
		}
    }
	
	@Override
	public boolean onBlockActivated(World world, int xPos, int yPos, int zPos, EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntityPlayerLink te = getTE(world, xPos, yPos, zPos);
		if (te != null) {
			if (te.owner == null) {
				if (!player.capabilities.isCreativeMode) {
					player.attackEntityFrom(DamageSource.magic, 5.0F);
				}
				if (!world.isRemote) {
					te.setOwner(new GameProfile((UUID)null, player.getCommandSenderName()));
	            }
			} else {
				te.setOwner(null);
			}
			world.markBlockForUpdate(xPos, yPos, zPos);
			te.markDirty();
			return true;
        }
		return false;
    }
	
	@Override
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }
	
	@Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
    	TileEntityPlayerLink te = getTE(world, x, y, z);
    	if (te != null && te.silkTouched != true) {
    		te.silkTouched = EnchantmentHelper.getSilkTouchModifier(player);
    	}
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        TileEntityPlayerLink te = getTE(world, x, y, z);
        ItemStack item = new ItemStack(Item.getItemFromBlock(BlockEnum.PLAYERLINK.getBlockInstance()), 1);
        if (te != null && te.silkTouched) {
        	NBTTagCompound tags = new NBTTagCompound();
        	te.writeData(tags);
        	item.stackTagCompound = tags;
        }
        ret.add(item);
        return ret;
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    	super.onBlockPlacedBy(world, x, y, z, player, stack);
    	if (stack.hasTagCompound()) {
	    	TileEntityPlayerLink te = this.getTE(world, x, y, z);
	        if (te != null) {
	        	te.readData(stack.getTagCompound());
	        }
        }
    }
    
    // End of Overrides
    private TileEntityPlayerLink getTE(World world, int x, int y, int z)
    {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity instanceof TileEntityPlayerLink ? (TileEntityPlayerLink)tileentity : null;
    }
}
