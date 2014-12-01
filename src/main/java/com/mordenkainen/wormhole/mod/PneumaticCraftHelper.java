package com.mordenkainen.wormhole.mod;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pneumaticCraft.api.item.IPressurizable;
import pneumaticCraft.api.tileentity.AirHandlerSupplier;
import pneumaticCraft.api.tileentity.IAirHandler;

public class PneumaticCraftHelper {
	private static final int MAX_CHARGE = 10;
	
	public static Object getNewAirHandler() {
		return AirHandlerSupplier.getTierTwoAirHandler(10000);
	}

	public static void updateEntity(Object airHandler) {
		((IAirHandler)airHandler).updateEntityI();
	}

	public static void writeToNBT(Object airHandler, NBTTagCompound tags) {
		((IAirHandler)airHandler).writeToNBTI(tags);
	}

	public static void readFromNBT(Object airHandler, NBTTagCompound tags) {
		((IAirHandler)airHandler).readFromNBTI(tags);
	}

	public static void validate(Object airHandler, TileEntity tile) {
		((IAirHandler)airHandler).validateI(tile);
	}

	public static void onNeighborChange(Object airHandler) {
		((IAirHandler)airHandler).onNeighborChange();
	}

	public static void chargeItem(ItemStack stack, Object airHandler) {
		if (stack.getItem() instanceof IPressurizable) {
			IAirHandler handler = (IAirHandler)airHandler;
			IPressurizable chargeItem = (IPressurizable)stack.getItem();
			for (int i = 0; i < MAX_CHARGE; i++) {
				if(chargeItem.getPressure(stack) < handler.getPressure(ForgeDirection.UNKNOWN) - 0.01F &&
						chargeItem.getPressure(stack) < chargeItem.maxPressure(stack)) {
					chargeItem.addAir(stack, 1);
					handler.addAir(-1, ForgeDirection.UNKNOWN);
				}
			}
		}
	}
}
