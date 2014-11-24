package com.mordenkainen.wormhole.items;

import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.entity.EntityLifeCore;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLifeCore extends Item {
	public ItemLifeCore() {
		super();
		setUnlocalizedName(Wormhole.MODID + ".lifecore");
		setCreativeTab(Wormhole.ModTab);
	}
	
	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return Integer.MAX_VALUE;
    }
	
	public boolean hasCustomEntity(ItemStack stack)
    {
        return true;
    }
	
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
		EntityLifeCore lc = new EntityLifeCore(world, location.posX, location.posY, location.posZ, itemstack);
		
		lc.motionX = location.motionX;
		lc.motionY = location.motionY;
		lc.motionZ = location.motionZ;

		if ( location instanceof EntityItem )
			lc.delayBeforeCanPickup = ((EntityItem) location).delayBeforeCanPickup;

		return lc;
    }

}
