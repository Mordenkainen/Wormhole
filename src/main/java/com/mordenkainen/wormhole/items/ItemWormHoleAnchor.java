package com.mordenkainen.wormhole.items;

import java.util.Iterator;
import java.util.List;

import com.mordenkainen.wormhole.Wormhole;
import com.mordenkainen.wormhole.net.packet.PacketEffect;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class ItemWormHoleAnchor extends Item {
	public ItemWormHoleAnchor() {
		super();
		setUnlocalizedName(Wormhole.MODID + ".wormholeanchor");
		setCreativeTab(Wormhole.ModTab);
		setTextureName(Wormhole.MODID + ":wormholeanchor");
		setMaxStackSize(1);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int xPos, int yPos, int zPos, int side, float px, float py, float pz) {
		if (stack.stackTagCompound == null) {
			TileEntity te = world.getTileEntity(xPos, yPos, zPos);
			
			if (te != null) {
				stack.stackTagCompound = new NBTTagCompound();
				stack.stackTagCompound.setString("type", "Block");
				stack.stackTagCompound.setInteger("xpos", xPos);
				stack.stackTagCompound.setInteger("ypos", yPos);
				stack.stackTagCompound.setInteger("zpos", zPos);
				stack.stackTagCompound.setInteger("dim", player.dimension);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("type")) {
			if (stack.stackTagCompound == null) {
				stack.stackTagCompound = new NBTTagCompound();
			}
			stack.stackTagCompound.setString("type", "Location");
			stack.stackTagCompound.setDouble("xpos", player.posX);
			stack.stackTagCompound.setDouble("ypos", player.posY);
			stack.stackTagCompound.setDouble("zpos", player.posZ);
			stack.stackTagCompound.setFloat("yaw", player.rotationYaw);
			stack.stackTagCompound.setFloat("pitch", player.rotationPitch);
			stack.stackTagCompound.setInteger("dim", player.dimension);
			return stack;
		}
		NBTTagCompound tags = stack.stackTagCompound;
		if (tags.getString("type").equals("Location")) {
			boolean crossDim = player.dimension != tags.getInteger("dim");
			PacketEffect oldDim = new PacketEffect(true, (int)player.posX, (int)player.posY, (int)player.posZ);
		    PacketEffect newDim = new PacketEffect(false, (int)tags.getDouble("xpos"), (int)tags.getDouble("ypos"), (int)tags.getDouble("zpos"));
		    NetworkRegistry.TargetPoint oldPoint = new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64);
		    NetworkRegistry.TargetPoint newPoint = new NetworkRegistry.TargetPoint(tags.getInteger("dim"), (int)tags.getDouble("xpos"), (int)tags.getDouble("ypos"), (int)tags.getDouble("zpos"), 64);
			if (!crossDim) {
				Chunk chunk = player.worldObj.getChunkFromBlockCoords((int)tags.getDouble("xpos"), (int)tags.getDouble("ypos"));
			    if (!(chunk.isChunkLoaded)) {
			    	player.worldObj.getChunkProvider().loadChunk(chunk.xPosition, chunk.zPosition);
			    }
			}
			player.rotationYaw = tags.getFloat("yaw");
			player.rotationPitch = tags.getFloat("pitch");
			if (crossDim && !player.worldObj.isRemote) transferPlayerToDimension((EntityPlayerMP)player, tags.getInteger("dim"), ((EntityPlayerMP)player).mcServer.getConfigurationManager());
			player.setPositionAndUpdate(tags.getDouble("xpos"), tags.getDouble("ypos"), tags.getDouble("zpos"));
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
				player.attackEntityFrom(DamageSource.fall, 5.0F);
				player.worldObj.playSoundAtEntity(player, "dig.glass", 1.0f, 1.0f);
			}
			if (!player.worldObj.isRemote) {
		      Wormhole.network.sendToAllAround(oldDim, oldPoint);
		      Wormhole.network.sendToAllAround(newDim, newPoint);
		    }
		}
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.hasTagCompound() && stack.stackTagCompound.hasKey("type");
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advTip) {
	    if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("type")) {
	    	NBTTagCompound tags = stack.stackTagCompound;
	    	list.add(EnumChatFormatting.GREEN + "Link Type: " + tags.getString("type"));
	    	list.add(EnumChatFormatting.GREEN + "Dimension: " + tags.getInteger("dim"));
	    	if (tags.getString("type").equals("Block")) {
	            list.add(EnumChatFormatting.GREEN + "X: " + tags.getInteger("xpos"));
	            list.add(EnumChatFormatting.GREEN + "Y: " + tags.getInteger("ypos"));
	            list.add(EnumChatFormatting.GREEN + "Z: " + tags.getInteger("zpos"));
	    	} else {
	    		list.add(EnumChatFormatting.GREEN + "X: " + (int)tags.getDouble("xpos"));
	            list.add(EnumChatFormatting.GREEN + "Y: " + (int)tags.getDouble("ypos"));
	            list.add(EnumChatFormatting.GREEN + "Z: " + (int)tags.getDouble("zpos"));
	    	}
	    } else {
	    	list.add(EnumChatFormatting.GRAY + "Unlinked");
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void transferPlayerToDimension(EntityPlayerMP player,	int dimension, ServerConfigurationManager manager) {
		int oldDim = player.dimension;
		WorldServer worldserver = manager.getServerInstance().worldServerForDimension(player.dimension);
		player.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().worldServerForDimension(player.dimension);
		player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
		worldserver.removePlayerEntityDangerously(player);
		player.isDead = false;
		transferEntityToWorld(player, worldserver, worldserver1);
		manager.func_72375_a(player, worldserver);
		player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.theItemInWorldManager.setWorld(worldserver1);
		manager.updateTimeAndWeatherForPlayer(player, worldserver1);
		manager.syncPlayerInventory(player);
		Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();
		while (iterator.hasNext()) {
			PotionEffect potioneffect = iterator.next();
			player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}
	
	public static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
		WorldProvider pOld = oldWorld.provider;
		WorldProvider pNew = newWorld.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double x = entity.posX * moveFactor;
		double z = entity.posZ * moveFactor;
		oldWorld.theProfiler.startSection("placing");
		x = MathHelper.clamp_double(x, -29999872, 29999872);
		z = MathHelper.clamp_double(z, -29999872, 29999872);
		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntityInWorld(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		oldWorld.theProfiler.endSection();
		entity.setWorld(newWorld);
	}
}
