package com.mordenkainen.wormhole.blocks;

// Java
import java.util.List;
import java.util.Random;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Wormhole
import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.items.ItemEnum;

public class BlockQuantumOre extends Block {
	private Random rand = new Random();
	private String[] names = {"overworld", "nether", "end"};
	
	@SideOnly(Side.CLIENT)
	private IIcon icons[];
	
	public BlockQuantumOre() {
		super(Material.rock);
		setBlockName(Wormhole.MODID + ".quantumore");
		setHardness(3.0F);
		setResistance(5.0F);
		setHarvestLevel("pickaxe", 3);
		setStepSound(soundTypePiston);
		setCreativeTab(Wormhole.ModTab);
	}
	
	@Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return ItemEnum.QUANTUMCRYSTAL.getItemInstance();
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random rand) {
        return this.quantityDropped(rand) + rand.nextInt(fortune + 1);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1 + rand.nextInt(2);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int xPos, int yPos, int zPoz, int meta, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, xPos, yPos, zPoz, meta, chance, fortune);
    }
    
    @Override
    public int getExpDrop(IBlockAccess blockAccess, int meta, int fortune) {
        if (this.getItemDropped(meta, rand, fortune) != Item.getItemFromBlock(this)) {
            return 1 + rand.nextInt(5);
        }
        return 0;
    }
    
    @Override
    public void onBlockClicked(World world, int xPos, int yPos, int zPos, EntityPlayer player) {
        replaceBlock(world, xPos, yPos, zPos);
        super.onBlockClicked(world, xPos, yPos, zPos, player);
    }

    @Override
    public void onEntityWalking(World world, int xPos, int yPos, int zPoz, Entity entity) {
        replaceBlock(world, xPos, yPos, zPoz);
        super.onEntityWalking(world, xPos, yPos, zPoz, entity);
    }

    @Override
    public boolean onBlockActivated(World world, int xPos, int yPos, int zPos, EntityPlayer player, int par6, float par7, float par8, float par9) {
        replaceBlock(world, xPos, yPos, zPos);
        return super.onBlockActivated(world, xPos, yPos, zPos, player, par6, par7, par8, par9);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List items) {
    	for (int i = 0; i < 3; ++i) {
    		items.add(new ItemStack(item, 1, i));
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	icons = new IIcon[3];
    	for (int i = 0; i < icons.length; i++) {
    		icons[i] = iconRegister.registerIcon(Wormhole.MODID + ":quantum_ore_" + names[i]);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
    	return icons[meta];
    }
    
    private void replaceBlock(World world, int xPos, int yPos, int zPos) {
        world.setBlock(xPos, yPos, zPos, BlockEnum.QUANTUMORELIT.getBlockInstance(), world.getBlockMetadata(xPos, yPos, zPos) , 3);
    }
    
}
