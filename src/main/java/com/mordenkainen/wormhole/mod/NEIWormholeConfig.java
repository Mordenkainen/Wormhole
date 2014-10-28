package com.mordenkainen.wormhole.mod;

// Forge
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

// NEI
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.API;

// Wormhole

import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.blocks.BlockEnum;

public class NEIWormholeConfig implements IConfigureNEI {
	@Override
	public String getName() {
		return Wormhole.MODNAME;
	}

	@Override
	public String getVersion() {
		return Wormhole.VERSION;
	}

	@Override
	public void loadConfig() {
		API.hideItem(new ItemStack(BlockEnum.QUANTUMORELIT.getBlockInstance(), 1, OreDictionary.WILDCARD_VALUE));
	}

}
