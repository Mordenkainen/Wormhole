package com.mordenkainen.wormhole.mod;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.items.wands.ItemWandCasting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public final class ThaumcraftHelper {
	private ThaumcraftHelper() {}
	
	public static void chargeWand(ItemStack stack, TileEntity tile) {
		if (stack.getItem() instanceof ItemWandCasting) {
			ItemWandCasting wand = (ItemWandCasting)stack.getItem();
			for (Aspect aspect : Aspect.getPrimalAspects()) {
				int amountNeeded = wand.getMaxVis(stack) - wand.getVis(stack, aspect);
				if (amountNeeded > 0) {
					int amountToRequest = amountNeeded/100 + amountNeeded%100 > 0 ? 1 : 0;
					int amountReceived = VisNetHandler.drainVis(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, aspect, amountToRequest);
					wand.addVis(stack, aspect, amountReceived, true);
				}
			}
		}
	}
}
