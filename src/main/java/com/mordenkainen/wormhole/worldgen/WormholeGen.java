// TODO gen random # of ores in each vein?
package com.mordenkainen.wormhole.worldgen;

// Java
import java.util.Random;

// Minecraft
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

// Forge
import cpw.mods.fml.common.IWorldGenerator;

// Wormhole
import com.mordenkainen.wormhole.blocks.BlockEnum;

public class WormholeGen implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch(world.provider.dimensionId) {
		case -1:
		    generateNether(world, random, chunkX * 16, chunkZ * 16);
		    break;
		case 0:
		    generateSurface(world, random, chunkX * 16, chunkZ * 16);
		    break;
		case 1:
		    generateEnd(world, random, chunkX * 16, chunkZ * 16);
		    break;
		}
	}

	private void generateSurface(World world, Random random, int x, int z) {
		for(int k = 0; k < 15; k++) {
        	int firstBlockXCoord = x + random.nextInt(16);
        	int firstBlockYCoord = random.nextInt(10);
        	int firstBlockZCoord = z + random.nextInt(16);
        	
        	(new WorldGenMinable(BlockEnum.QUANTUMORE.getBlockInstance(), 0, 6, Blocks.stone)).generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        }
	}

	private void generateNether(World world, Random random, int x, int z) {
		for(int k = 0; k < 20; k++) {
        	int firstBlockXCoord = x + random.nextInt(16);
        	int firstBlockYCoord = random.nextInt(128);
        	int firstBlockZCoord = z + random.nextInt(16);
        	
        	(new WorldGenMinable(BlockEnum.QUANTUMORE.getBlockInstance(), 1, 6, Blocks.netherrack)).generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        }
	}
	
	private void generateEnd(World world, Random random, int x, int z) {
		for(int k = 0; k < 40; k++) {
        	int firstBlockXCoord = x + random.nextInt(16);
        	int firstBlockYCoord = random.nextInt(60);
        	int firstBlockZCoord = z + random.nextInt(16);
        	
        	(new WorldGenMinable(BlockEnum.QUANTUMORE.getBlockInstance(), 2, 10, Blocks.end_stone)).generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        }
	}
}
