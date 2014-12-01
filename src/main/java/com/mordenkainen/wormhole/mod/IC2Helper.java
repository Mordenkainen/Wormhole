package com.mordenkainen.wormhole.mod;

// Minecraft
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

// IC2
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

public class IC2Helper {
	public static final int chargeItem(ItemStack stack, int maxCharge) {
		double amountCharged = 0;
		if (stack.getItem() instanceof IElectricItem) {
			double curCharge = ElectricItem.manager.getCharge(stack);
			if (curCharge < ((IElectricItem)stack.getItem()).getMaxCharge(stack)) {
				amountCharged = ElectricItem.manager.charge(stack, maxCharge / 4, 4, false, false);
			}
		}
		return (int)amountCharged*4;
	}
	
	public static final void registerTile(TileEntity tile) {
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
	}
	
	public static final void deregisterTile(TileEntity tile) {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tile));
	}
}
