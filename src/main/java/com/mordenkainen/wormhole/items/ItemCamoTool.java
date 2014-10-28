package com.mordenkainen.wormhole.items;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

// Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Wormhole
import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.tileentity.ICamo;

public class ItemCamoTool extends Item {
	private IIcon unlinkedIcon;
    private IIcon linkedIcon;
	
	public ItemCamoTool() {
		super();
		setUnlocalizedName(Wormhole.MODID + ".camotool");
		setCreativeTab(Wormhole.ModTab);
		setTextureName(Wormhole.MODID + ":camotool");
		setMaxStackSize(1);
	}
	
    @Override
    public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!entityPlayer.isSneaking()) return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof ICamo) {
        	ICamo camo = (ICamo) tile;

            if (!camo.canCamo()) return false;

            if (isLinked(itemStack)) {
                int linkedBlockMetadata = itemStack.getTagCompound().getInteger("camoMeta");
                Block linkedBlock = Block.getBlockById(itemStack.getTagCompound().getInteger("camoBlock"));
                if (linkedBlock != null && camo.canCamoAs(linkedBlock, linkedBlockMetadata)) {
                	camo.setCamo(linkedBlock, linkedBlockMetadata);
                }
            } else if (camo.getCamo() != null) {
                camo.clearCamo();
            } else {
                return false;
            }

            // if the client returns true here, the server doesn't call onItemUseFirst
            if (!world.isRemote)
                return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if ((tile == null || !(tile instanceof ICamo)) && entityPlayer.isSneaking()) {
            linkTileAtPosition(itemStack, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer entityPlayer) {
        if (isLinked(stack)) {
            entityPlayer.swingItem();
            unlink(stack);
            return stack;
        }
        return super.onItemRightClick(stack, world, entityPlayer);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        itemIcon = linkedIcon = par1IconRegister.registerIcon(Wormhole.MODID + ":camotool");
        unlinkedIcon = par1IconRegister.registerIcon(Wormhole.MODID + ":camotool.unlinked");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return getIconIndex(stack);
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return isLinked(stack) ? linkedIcon : unlinkedIcon;
    }
    
    private void linkTileAtPosition(ItemStack stack, World world, int x, int y, int z) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        Block linkedBlock = world.getBlock(x, y, z);
        int linkedBlockMetadata = world.getBlockMetadata(x, y, z);
        TileEntity linkedTile = world.getTileEntity(x, y, z);
        if (linkedTile != null && linkedTile instanceof ICamo) {
            linkedBlock = ((ICamo) linkedTile).getCamo();
            linkedBlockMetadata = ((ICamo) linkedTile).getCamoMeta();
        }
        stack.getTagCompound().setInteger("camoBlock", Block.getIdFromBlock(linkedBlock));
        stack.getTagCompound().setInteger("camoMeta", linkedBlockMetadata);
    }

    private void unlink(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.setTagCompound(null);
        }
    }

    public boolean isLinked(ItemStack stack) {
        return stack.hasTagCompound();
    }
}
