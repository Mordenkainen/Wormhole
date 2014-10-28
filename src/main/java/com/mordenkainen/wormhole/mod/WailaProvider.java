package com.mordenkainen.wormhole.mod;

// Waila
import mcp.mobius.waila.api.IWailaRegistrar;

// Wormhole

import com.mordenkainen.wormhole.blocks.BlockPlayerLink;

public class WailaProvider {
	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new PlayerLinkHUDHandler(), BlockPlayerLink.class);
    }
}
