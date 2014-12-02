package com.mordenkainen.wormhole.proxy;

// Forge
import cpw.mods.fml.client.registry.ClientRegistry;

// Wormhole

import com.mordenkainen.wormhole.render.tileentity.TileEntityRendererPlayerLink;
import com.mordenkainen.wormhole.tileentity.TileEntityPlayerLink;

public class ClientProxy extends CommonProxy {	
	@Override
	public void registerRenderers() {
		try {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerLink.class,new TileEntityRendererPlayerLink());
		} catch (NullPointerException e) {}
	}
}
