package com.mordenkainen.wormhole.mod;

// Java
import java.util.List;


// Minecraft
import net.minecraft.item.ItemStack;

// Waila
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;

// Wormhole

import com.mordenkainen.wormhole.tileentity.TileEntityPlayerLink;

public class PlayerLinkHUDHandler implements IWailaDataProvider {
	@Override
	public List<String> getWailaBody(ItemStack itemStatck, List<String> strings, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntityPlayerLink tileEntity = (TileEntityPlayerLink) accessor.getTileEntity();
        if (tileEntity.owner != null) {
        	strings.add("Linked to: " + tileEntity.owner.getName());
        	strings.add(tileEntity.playerOnline() ? SpecialChars.GREEN + "Online" : SpecialChars.RED + "Offline");
        	strings.add(!tileEntity.hasRedstone() ? SpecialChars.GREEN + "Enabled" : SpecialChars.RED + "Disabled");
        } else {
        	strings.add("Unlinked");
        }
        strings.add(SpecialChars.RESET + tileEntity.currentMana + " / " + TileEntityPlayerLink.MAX_MANA + " Mana");
		return strings;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStatck, List<String> strings, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStatck, List<String> strings, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}
}
