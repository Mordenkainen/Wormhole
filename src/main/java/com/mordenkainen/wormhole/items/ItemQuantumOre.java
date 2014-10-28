package com.mordenkainen.wormhole.items;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemQuantumOre extends ItemBlock {
	private String[] names = {"overworld", "nether", "end"};
	
	public ItemQuantumOre(Block block) {
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int meta) {
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName() + "." + names[stack.getItemDamage()];
	}

}
