package com.mordenkainen.wormhole.items;

// Java
import java.util.List;


// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

// Forge
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Wormhole

import com.mordenkainen.wormhole.Wormhole;

public class ItemQuantumCrystal extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon[] unstableIcons;
	
	@SideOnly(Side.CLIENT)
	private IIcon stableIcon;
	
	private static final String[] names = {"unstable", "stable"};
	
	public ItemQuantumCrystal() {
		super();
		setHasSubtypes(true);
		setUnlocalizedName(Wormhole.MODID + ".quantumcrystal");
		setCreativeTab(Wormhole.ModTab);
	}
	
	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return super.getUnlocalizedName() + "." + names[itemstack.getItemDamage()];
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List items) {
		for (int x = 0; x < names.length; x++) {
			items.add(new ItemStack(this, 1, x));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		stableIcon = iconRegister.registerIcon(Wormhole.MODID + ":quantumcrystal." + names[1]);
		unstableIcons = new IIcon[16];
		
		for (int i = 0; i < unstableIcons.length; i++) {
			unstableIcons[i] = iconRegister.registerIcon(Wormhole.MODID + ":quantumcrystal." + names[0] + "." + i);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getIconFromDamage(int damage) {
		if (damage == 0) {
			int step = ((int) Minecraft.getSystemTime()/100) % 32;
			int icon = step > 15 ? 16 - (step - 15) : step;
	        return unstableIcons[icon];
		}
		return stableIcon;
    }
}
