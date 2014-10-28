package com.mordenkainen.wormhole.blocks;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

//Wormhole
import com.mordenkainen.wormhole.items.ItemPlayerLink;
import com.mordenkainen.wormhole.items.ItemQuantumOre;

public enum BlockEnum {
	PLAYERLINK(BlockPlayerLink.class, ItemPlayerLink.class),
	QUANTUMORE(BlockQuantumOre.class, ItemQuantumOre.class),
	QUANTUMORELIT(BlockQuantumOreLit.class, ItemQuantumOre.class);

	private Block block;
	private Class<? extends Block> blockClass;
	private Class<? extends ItemBlock> itemBlockClass;

	BlockEnum(Class<? extends Block> blockClass) {
		this(blockClass, ItemBlock.class);
	}

	BlockEnum(Class<? extends Block> blockClass, Class<? extends ItemBlock> itemBlockClass) {
		this.blockClass = blockClass;
		this.itemBlockClass = itemBlockClass;
	}

	public void setBlockInstance(Block block) {
		this.block = block;
	}

	public Block getBlockInstance() {
		return block;
	}

	public Class<? extends Block> getBlockClass() {
		return blockClass;
	}

	public Class<? extends ItemBlock> getItemBlockClass() {
		return itemBlockClass;
	}
}