// TODO Crafting recipes
package com.mordenkainen.wormhole;

// Minecraft
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

// Forge
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Wormhole
import com.mordenkainen.wormhole.blocks.BlockEnum;
import com.mordenkainen.wormhole.proxy.CommonProxy;
import com.mordenkainen.wormhole.worldgen.WormholeGen;

@Mod(modid = Wormhole.MODID, name = Wormhole.MODNAME, version = Wormhole.VERSION)
public class Wormhole {
	public static final String MODID = "wormhole";
	public static final String MODNAME = "Wormhole";
	public static final String VERSION = "1.0";
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	
	@Instance(Wormhole.MODID)
	public static Wormhole instance;

	public static CreativeTabs ModTab = new CreativeTabs(MODID) {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock(BlockEnum.PLAYERLINK.getBlockInstance());
		}
	};

	@SidedProxy(clientSide = "com.mordenkainen.wormhole.proxy.ClientProxy", serverSide = "com.mordenkainen.wormhole.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.RegisterBlocks();
		proxy.RegisterItems();
		proxy.RegisterRecipies();
		proxy.RegisterRenderers();
		proxy.RegisterTileEntities();
		GameRegistry.registerWorldGenerator(new WormholeGen(), 1);
		MinecraftForge.EVENT_BUS.register(proxy);
		FMLInterModComms.sendMessage("Waila", "register", "com.mordenkainen.wormhole.mod.WailaProvider.callbackRegister");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.RegisterPackets();
	}
}
