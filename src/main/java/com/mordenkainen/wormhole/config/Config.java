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
	public static boolean enablePlayerLinkFood;
	public static boolean enablePlayerLinkPotions;
	public static boolean enablePlayerLinkMana;
	public static boolean enablePlayerLinkPressure;
	public static boolean enablePlayerLinkVis;
	
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
		
		Property playerLinkEnergyProperty = config.get("PlayerLink", "CanChargeEnergyItems", true);
		playerLinkEnergyProperty.comment = "Set to true to allow the PlayerLink to charge RF/EU items in the players inventory.";
		enablePlayerLinkEnergy = playerLinkEnergyProperty.getBoolean(true);
		
		Property playerLinkFoodProperty = config.get("PlayerLink", "CanFeedPlayer", true);
		playerLinkFoodProperty.comment = "Set to true to allow the PlayerLink to feed players food sent to the bottom.";
		enablePlayerLinkFood = playerLinkFoodProperty.getBoolean(true);
		
		Property playerLinkPotionProperty = config.get("PlayerLink", "CanUsePotions", true);
		playerLinkPotionProperty.comment = "Set to true to allow the PlayerLink to apply potion effects to the player using potions sent to the bottom.";
		enablePlayerLinkPotions = playerLinkPotionProperty.getBoolean(true);
		
		Property playerLinkManaProperty = config.get("PlayerLink", "CanChargeManaItems", true);
		playerLinkManaProperty.comment = "Set to true to allow the PlayerLink to charge Botania mana items in the players inventory.";
		enablePlayerLinkMana = playerLinkManaProperty.getBoolean(true);
		
		Property playerLinkPressureProperty = config.get("PlayerLink", "CanChargePressureItems", true);
		playerLinkPressureProperty.comment = "Set to true to allow the PlayerLink to charge PneumaticCraft items in the players inventory.";
		enablePlayerLinkPressure = playerLinkPressureProperty.getBoolean(true);
		
		Property playerLinkVisProperty = config.get("PlayerLink", "CanChargeVisItems", true);
		playerLinkVisProperty.comment = "Set to true to allow the PlayerLink to charge Vis storage items in the players inventory.";
		enablePlayerLinkVis = playerLinkVisProperty.getBoolean(true);
		
		config.save();
	}
}
