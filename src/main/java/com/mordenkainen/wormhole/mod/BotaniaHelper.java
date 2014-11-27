package com.mordenkainen.wormhole.mod;

import vazkii.botania.api.mana.IManaItem;
import net.minecraft.item.ItemStack;

public class BotaniaHelper {

	public static int chargeItem(ItemStack stack, int currentMana) {
		int toBeCharged = 0;
		if (stack.getItem() instanceof IManaItem) {
			IManaItem currentItem = (IManaItem)stack.getItem();
			toBeCharged = Math.min(currentItem.getMaxMana(stack) - currentItem.getMana(stack), currentMana);
			if (toBeCharged > 0) {
				currentItem.addMana(stack, toBeCharged);
			}
		}
		return toBeCharged;
	}

}
