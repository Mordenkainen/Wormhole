package com.mordenkainen.wormhole.items;

// Java
import java.util.List;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

//Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPlayerLink extends ItemBlock {

	public ItemPlayerLink(Block block) {
		super(block);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advTip) {
	    if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("Owner")) {
	    	NBTTagCompound tags = stack.stackTagCompound;
	    	list.add(EnumChatFormatting.GREEN + "Owner: " + tags.getString("OwnerName"));
	    } else {
	    	list.add(EnumChatFormatting.GRAY + "Unlinked");
	    }
	}
}
