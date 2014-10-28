package com.mordenkainen.wormhole.tileentity;

// Minecraft
import net.minecraft.block.Block;

public interface ICamo {
    public boolean canCamo();

    public boolean canCamoAs(Block block, int metadata);

    public Block getCamo();

    public int getCamoMeta();

    public void setCamo(Block block, int metadata);

    public void clearCamo();
}
