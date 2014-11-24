package com.mordenkainen.wormhole.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityLifeCore extends EntityItem {

	public EntityLifeCore(World world, double posX, double posY, double posZ, ItemStack itemstack) {
		super(world, posX, posY, posZ, itemstack);
		isImmuneToFire = true;
		System.out.println("Here");
	}
	
	public boolean handleWaterMovement()
    {
		return false;
    }
	
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
		return false;
    }

}
