package com.mordenkainen.wormhole.proxy;

// Java
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.init.Blocks;
// Minecraft
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

// Forge
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;



// Wormhole
import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.blocks.BlockEnum;
import com.mordenkainen.wormhole.config.Config;
import com.mordenkainen.wormhole.items.ItemEnum;
import com.mordenkainen.wormhole.net.packet.PacketEffect;
import com.mordenkainen.wormhole.tileentity.TileEntityPlayerLink;

public class CommonProxy {
	public void RegisterTileEntities() {
		GameRegistry.registerTileEntity(TileEntityPlayerLink.class, "tileEntityplayerlink");
	}

	public void RegisterRenderers() {
		// Only Clientside
	}

	public void RegisterBlocks() {
		for (BlockEnum current : BlockEnum.values()) {
			try {
				current.setBlockInstance(current.getBlockClass().newInstance());
				GameRegistry.registerBlock(current.getBlockInstance(), current.getItemBlockClass(), current.getBlockInstance().getUnlocalizedName());
			} catch (Throwable e) {}
		}
	}
	
	public void RegisterItems()
	{
		for (ItemEnum current : ItemEnum.values()) {
			try {
				current.setItemInstance(current.getItemClass().getConstructor().newInstance());
				GameRegistry.registerItem(current.getItemInstance(), current.getItemInstance().getUnlocalizedName(), Wormhole.MODID);
			} catch (Throwable e) {}
		}
	}

	public void RegisterRecipies() {
		GameRegistry.addSmelting(Item.getItemFromBlock(BlockEnum.QUANTUMORE.getBlockInstance()), new ItemStack(ItemEnum.QUANTUMCRYSTAL.getItemInstance(),4,1), 5);
		GameRegistry.addSmelting(new ItemStack(ItemEnum.QUANTUMCRYSTAL.getItemInstance(), 1, 0), new ItemStack(ItemEnum.QUANTUMCRYSTAL.getItemInstance(),1,1), 0);
		GameRegistry.addSmelting(ItemEnum.WORMHOLEANCHOR.getItemInstance(), new ItemStack(ItemEnum.WORMHOLEANCHOR.getItemInstance(),1), 0);
		GameRegistry.addRecipe(new ItemStack(ItemEnum.WORMHOLEANCHOR.getItemInstance(),1), "CCC", "CEC", "CCC", 'C', new ItemStack(ItemEnum.QUANTUMCRYSTAL.getItemInstance(),1,1), 'E', Items.ender_pearl);
		if (Config.enablePlayerLink) {
			GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockEnum.PLAYERLINK.getBlockInstance()),1), "DWD", "WCW", "DWD", 'D', Items.diamond, 'W', new ItemStack(ItemEnum.WORMHOLEANCHOR.getItemInstance(),1), 'C', Blocks.chest);
		}
	}
	
	public void RegisterPackets() {
		int disc = 0;
		Wormhole.network.registerMessage(PacketEffect.class, PacketEffect.class, disc++, Side.CLIENT);
	}
	
	@SubscribeEvent
	public void breakBlock(BlockEvent.BreakEvent event)
	{
		TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
		if (te != null && te instanceof TileEntityPlayerLink && ((TileEntityPlayerLink)te).owner != null &&	!((TileEntityPlayerLink)te).owner.getName().equals(event.getPlayer().getCommandSenderName()) && !isOp(event.getPlayer().getCommandSenderName())) {
			event.setCanceled(true);
		}
	}
	
	private boolean isOp(String name) {
		ArrayList<String> ops = new ArrayList<String>(Arrays.asList(MinecraftServer.getServer().getConfigurationManager().func_152603_m().func_152685_a()));
		if (ops.contains(name)) return true;
		return false;
	}
}
