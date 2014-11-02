package com.mordenkainen.wormhole.items;

import java.util.ArrayList;
import java.util.List;

import com.mordenkainen.wormhole.Wormhole;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemStorageCore extends Item implements IEntitySelector {
	public ItemStorageCore() {
		super();
		setUnlocalizedName(Wormhole.MODID + ".storagecore");
		setCreativeTab(Wormhole.ModTab);
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return Integer.MAX_VALUE;
    }
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
    {
		if (!entityItem.getEntityItem().hasTagCompound()) {
			entityItem.getEntityItem().stackTagCompound = new NBTTagCompound();
		}
		int ticksRemaining = entityItem.getEntityItem().stackTagCompound.getInteger("ticksUntilAction");
		if (ticksRemaining > 0) {
			ticksRemaining--;
			entityItem.getEntityItem().stackTagCompound.setInteger("ticksUntilAction", ticksRemaining);
		} else {
			entityItem.getEntityItem().stackTagCompound.setInteger("ticksUntilAction", 10);
			eatLayer(entityItem);
		}
        return false;
    }
	
	@Override
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
		if (p_77648_1_.hasTagCompound() && p_77648_1_.stackTagCompound.getInteger("numItems") > 0) {
			TileEntity te = p_77648_3_.getTileEntity(p_77648_4_, p_77648_5_, p_77648_6_);
			if (te != null && te instanceof IInventory) {
				IInventory inv = (IInventory)te;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					if(inv.getStackInSlot(i) == null) {
						if (p_77648_1_.stackTagCompound.getInteger("numItems") == 0) break;
						NBTTagCompound itemtag = (NBTTagCompound)p_77648_1_.stackTagCompound.getTag(Integer.toString(p_77648_1_.stackTagCompound.getInteger("numItems") - 1));
						inv.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(itemtag));
						p_77648_1_.stackTagCompound.removeTag(Integer.toString(p_77648_1_.stackTagCompound.getInteger("numItems") - 1));
						p_77648_1_.stackTagCompound.setInteger("numItems", p_77648_1_.stackTagCompound.getInteger("numItems") - 1);
					}
				}
				return true;
			}
		}
        return false;
    }
	
	private void eatLayer(EntityItem entityItem) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		@SuppressWarnings("unchecked")
		List<Entity> interestingItems = entityItem.worldObj.selectEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((int)Math.floor(entityItem.posX), (int)Math.floor(entityItem.posY), (int)Math.floor(entityItem.posZ), (int)Math.floor(entityItem.posX) + 1, (int)Math.floor(entityItem.posY) + 1, (int)Math.floor(entityItem.posZ) + 1).expand(3, 3, 3), this);
		for (Entity entity : interestingItems) {
			if (entity != entityItem) {
				list.add(((EntityItem)entity).getEntityItem());
				entity.setDead();
			}
		}
		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				if ((Math.abs(i) == 2 && j == 0) || (Math.abs(j) == 2 && i == 0) || (Math.abs(i) != 2 && Math.abs(j) != 2)) {
					Block targetBlock = entityItem.worldObj.getBlock((int)Math.floor(entityItem.posX + i), ((int)Math.floor(entityItem.posY) - 1), (int)Math.floor(entityItem.posZ + j));
					if (targetBlock != Blocks.bedrock) {
						int meta = entityItem.worldObj.getBlockMetadata((int)Math.floor(entityItem.posX + i), ((int)Math.floor(entityItem.posY) - 1), (int)Math.floor(entityItem.posZ + j));
						list.addAll(targetBlock.getDrops(entityItem.worldObj, (int)Math.floor(entityItem.posX + i), ((int)Math.floor(entityItem.posY) - 1), (int)Math.floor(entityItem.posZ + j), meta, 0));
						entityItem.worldObj.setBlock((int)Math.floor(entityItem.posX + i), ((int)Math.floor(entityItem.posY) - 1), (int)Math.floor(entityItem.posZ + j), Blocks.air);
					}
				}
			}
		}
		if (!entityItem.worldObj.isRemote) {
			for (ItemStack item : list) {
				int i;
				for (i = 0; i <= entityItem.getEntityItem().stackTagCompound.getInteger("numItems") - 1; i++) {
					ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)entityItem.getEntityItem().stackTagCompound.getTag(Integer.toString(i)));
					if (stack.stackSize < 64) {
						if(stack.isItemEqual(item)) {
							int toMove = Math.min(64 - stack.stackSize, item.stackSize);
							stack.stackSize += toMove;
							item.stackSize -= toMove;
							NBTTagCompound nbttagcompound = new NBTTagCompound();
				            stack.writeToNBT(nbttagcompound);
				            entityItem.getEntityItem().stackTagCompound.setTag(Integer.toString(i), nbttagcompound);
				            if (item.stackSize == 0) break;
						}
					}
				}
				if (i == entityItem.getEntityItem().stackTagCompound.getInteger("numItems")) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
		            item.writeToNBT(nbttagcompound);
		            entityItem.getEntityItem().stackTagCompound.setTag(Integer.toString(i), nbttagcompound);
		            entityItem.getEntityItem().stackTagCompound.setInteger("numItems", i + 1);
				}
			}
		}
	}
	
	@Override
	public boolean isEntityApplicable(Entity entity) {
		if (entity.isDead) return false;

		if (entity instanceof EntityItem) {
			return true;
		}

		return false;
	}
}
