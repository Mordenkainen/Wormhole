package com.mordenkainen.wormhole.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	public static boolean enablePlayerLink;
	public static boolean enablePlayerLinkArmor;
	public static boolean enablePlayerLinkInv;
	public static boolean enablePlayerLinkHotbar;
	public static boolean enablePlayerLinkEnergy;
	
	public static void load(File configFile) {
		Configuration config = new Configuration(configFile);
		config.load();
		
		Property playerLinkEnableProperty = config.get("PlayerLink", "EnableCrafting", true);
		playerLinkEnableProperty.comment = "Enable crafting of the PlayerLink block.";
		enablePlayerLink = playerLinkEnableProperty.getBoolean(true);
		
		Property playerLinkArmorProperty = config.get("PlayerLink", "CanAccessArmor", true);
		playerLinkArmorProperty.comment = "Set to true to allow the PlayerLink to interact with Armor slots.";
		enablePlayerLinkArmor = playerLinkArmorProperty.getBoolean(true);
		
		Property playerLinkInvProperty = config.get("PlayerLink", "CanAccessInventory", true);
		playerLinkInvProperty.comment = "Set to true to allow the PlayerLink to interact with the players main inventory.";
		enablePlayerLinkInv = playerLinkInvProperty.getBoolean(true);
		
		Property playerLinkHotProperty = config.get("PlayerLink", "CanAccessHotbar", true);
		playerLinkHotProperty.comment = "Set to true to allow the PlayerLink to interact with the players hotbar.";
		enablePlayerLinkHotbar = playerLinkHotProperty.getBoolean(true);
		
		Property playerLinkEnergyProperty = config.get("PlayerLink", "CanChargeItems", true);
		playerLinkEnergyProperty.comment = "Set to true to allow the PlayerLink to charge items in the players inventory.";
		enablePlayerLinkEnergy = playerLinkEnergyProperty.getBoolean(true);
		
		config.save();
	}
}
