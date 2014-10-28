package com.mordenkainen.wormhole.blocks;

// Java
import java.util.Random;

// Minecraft
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

//Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Wormhole
import com.mordenkainen.wormhole.Wormhole;

public class BlockQuantumOreLit extends BlockQuantumOre {
	
	public BlockQuantumOreLit() {
		super();
		setLightLevel(0.625F);
		setTickRandomly(true);
		setBlockName(Wormhole.MODID + ".quantumorelit");
		setCreativeTab(null);
	}
	
	@Override
	public void onBlockAdded(World world, int xPos, int yPos, int zPos) {
		world.scheduleBlockUpdate(xPos, yPos, zPos, BlockEnum.QUANTUMORELIT.getBlockInstance(), tickRate(world));
	}
	
	@Override
	public int tickRate(World world) {
        return 80;
    }
	
	@Override
	protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(BlockEnum.QUANTUMORE.getBlockInstance(), 1, meta);
    }
	
	@Override
	public void updateTick(World world, int xPos, int yPos, int zPos, Random rand) {
        world.setBlock(xPos, yPos, zPos, BlockEnum.QUANTUMORE.getBlockInstance(), world.getBlockMetadata(xPos, yPos, zPos) , 3);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int xPos, int yPos, int zPos, Random rand) {
        renderParticles(world, xPos, yPos, zPos);
    }
	
	private void renderParticles(World world, int xPos, int yPos, int zPos) {
        Random random = world.rand;
        double d0 = 0.0625D;

        for (int l = 0; l < 6; ++l) {
            double d1 = (double)((float)xPos + random.nextFloat());
            double d2 = (double)((float)yPos + random.nextFloat());
            double d3 = (double)((float)zPos + random.nextFloat());

            if (l == 0 && !world.getBlock(xPos, yPos + 1, zPos).isOpaqueCube()) {
                d2 = (double)(yPos + 1) + d0;
            }

            if (l == 1 && !world.getBlock(xPos, yPos - 1, zPos).isOpaqueCube()) {
                d2 = (double)(yPos + 0) - d0;
            }

            if (l == 2 && !world.getBlock(xPos, yPos, zPos + 1).isOpaqueCube()) {
                d3 = (double)(zPos + 1) + d0;
            }

            if (l == 3 && !world.getBlock(xPos, yPos, zPos - 1).isOpaqueCube()) {
                d3 = (double)(zPos + 0) - d0;
            }

            if (l == 4 && !world.getBlock(xPos + 1, yPos, zPos).isOpaqueCube()) {
                d1 = (double)(xPos + 1) + d0;
            }

            if (l == 5 && !world.getBlock(xPos - 1, yPos, zPos).isOpaqueCube()) {
                d1 = (double)(xPos + 0) - d0;
            }

            if (d1 < (double)xPos || d1 > (double)(xPos + 1) || d2 < 0.0D || d2 > (double)(yPos + 1) || d3 < (double)zPos || d3 > (double)(zPos + 1)) {
                world.spawnParticle("portal", d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
