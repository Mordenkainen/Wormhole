package com.mordenkainen.wormhole.items;

import net.minecraft.item.Item;

public enum ItemEnum {
	WORMHOLEANCHOR(ItemWormHoleAnchor.class),
	QUANTUMCRYSTAL(ItemQuantumCrystal.class),
	STORAGECORE(ItemStorageCore.class),
	LIFECORE(ItemLifeCore.class),
	CAMOTOOL(ItemCamoTool.class);

	private Item item;
	private Class<? extends Item> itemClass;

	ItemEnum(Class<? extends Item> itemClass) {
		this.itemClass = itemClass;
	}

	public void setItemInstance(Item item) {
		this.item = item;
	}

	public Item getItemInstance() {
		return item;
	}

	public Class<? extends Item> getItemClass() {
		return itemClass;
	}
}
