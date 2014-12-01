package com.mordenkainen.wormhole.tileentity;

// Minecraft
import net.minecraft.block.Block;

public interface ICamo {
    boolean canCamo();

    boolean canCamoAs(Block block, int metadata);

    Block getCamo();

    int getCamoMeta();

    void setCamo(Block block, int metadata);

    void clearCamo();
}
