package com.mordenkainen.wormhole.mod;

import cpw.mods.fml.common.Loader;

public class ModHelper {
	public static boolean IC2Loaded = false;
	public static boolean BotaniaLoaded = false;
	public static boolean PneumaticCraftLoaded = false;
	
	public static void init() {
		if (Loader.isModLoaded("IC2")) IC2Loaded = true;
		if (Loader.isModLoaded("Botania")) BotaniaLoaded = true;
		if (Loader.isModLoaded("PneumaticCraft")) PneumaticCraftLoaded = true;
	}
}
