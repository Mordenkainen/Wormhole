package com.mordenkainen.wormhole.tileentity;

// Minecraft
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringUtils;

// Forge
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;



// Google
import com.google.common.collect.Iterables;



// IC2
import ic2.api.energy.tile.IEnergySink;

// COFH
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;



import com.mordenkainen.wormhole.config.Config;
// Wormhole
import com.mordenkainen.wormhole.mod.IC2Helper;

@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2")
public class TileEntityPlayerLink extends TileEntity implements ISidedInventory, IEnergyHandler, ICamo, IEnergySink  {
	public Block blockCamoAs = null;
    public int blockCamoMetadata = 0;
    public EnergyStorage storage = new EnergyStorage(60000);
    public boolean silkTouched = false;
    public GameProfile owner = null;
    private boolean addedToEnet;
    
	private static final int[] armorSlots;
	private static final int[] invSlots;
	private static final int[] hotbarSlots;
	private static final int numSlots;

	static {
		numSlots = (Config.enablePlayerLinkHotbar ? 9 : 0) + (Config.enablePlayerLinkInv ? 27 : 0) + (Config.enablePlayerLinkArmor ? 4 : 0);
		if (Config.enablePlayerLinkHotbar) {
			hotbarSlots= new int[] {0,1,2,3,4,5,6,7,8};
		} else {
			hotbarSlots= new int[] {};
		}
		if (Config.enablePlayerLinkInv) {
			if (Config.enablePlayerLinkHotbar) {
				invSlots = new int[] {9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35};
			} else {
				invSlots = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
			}
		} else {
			invSlots = new int[] {};
		}
		if (Config.enablePlayerLinkArmor) {
			if (Config.enablePlayerLinkHotbar && Config.enablePlayerLinkInv) {
				armorSlots = new int[] {36,37,38,39};
			} else if (!Config.enablePlayerLinkHotbar && !Config.enablePlayerLinkInv) {
				armorSlots = new int[] {0,1,2,3};
			} else if (Config.enablePlayerLinkHotbar && !Config.enablePlayerLinkInv) {
				armorSlots = new int[] {9,10,11,12};
			} else {
				armorSlots = new int[] {27,28,29,30};
			}
		} else {
			armorSlots = new int[] {};
		}
	}
	
	// TileEntity
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		readData(tags);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
		writeData(tags);
	}
	
	@Override
	public void updateEntity() {
		if (Loader.isModLoaded("IC2")) {
			if (!addedToEnet) onLoaded();
		}
		if (!worldObj.isRemote) {
			if (!isActive()) return;
			if (Config.enablePlayerLinkEnergy) {
				if (storage.getEnergyStored() > 0) {
					int toSend = storage.extractEnergy(10000, true);
					int moved = 0;
					IInventory playerInv = getPlayer().inventory;
					for (int i = 0; i < playerInv.getSizeInventory(); i++) {
						ItemStack stack = playerInv.getStackInSlot(i);
						if (stack != null) {
							if (stack.getItem() instanceof IEnergyContainerItem && 
									((IEnergyContainerItem)stack.getItem()).getEnergyStored(stack) < ((IEnergyContainerItem)stack.getItem()).getMaxEnergyStored(stack)) {
								moved += ((IEnergyContainerItem)stack.getItem()).receiveEnergy(stack, toSend - moved, false);
								
							} else {
								if (Loader.isModLoaded("IC2")) {
									moved += IC2Helper.chargeItem(stack, toSend - moved);
								}
							}
						}
						if (moved >= toSend) break;
					}
					System.out.println("Total amount to remove from storage: " + moved);
					if (moved > 0) {
						storage.extractEnergy(moved, false);
						System.out.println("Stored Amount: " + storage.getEnergyStored());
					}
				}
			}
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if (Loader.isModLoaded("IC2") && addedToEnet) {
			IC2Helper.deregisterTile(this);
			addedToEnet = false;
		}
	}

	@Override
	public void onChunkUnload() {
		invalidate();
	}
	
	// IInventory
	@Override
	public int getSizeInventory() {
		return isActive() ? numSlots : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return isActive() ? getPlayer().inventory.getStackInSlot(getAdjustedSlot(slot)) : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int numItems) {
		return isActive() ? getPlayer().inventory.decrStackSize(getAdjustedSlot(slot), numItems) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return isActive() ? getPlayer().inventory.getStackInSlotOnClosing(getAdjustedSlot(slot)) : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isActive()) {
			getPlayer().inventory.setInventorySlotContents(getAdjustedSlot(slot), stack);
		} else if (worldObj != null && !worldObj.isRemote) {
			EntityItem toDrop = new EntityItem(worldObj, xCoord, yCoord, zCoord, stack);
            worldObj.spawnEntityInWorld(toDrop);
		}
	}

	@Override
	public String getInventoryName() {
		return isActive() ? getPlayer().inventory.getInventoryName() : "";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return isActive() ? getPlayer().inventory.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (isActive()) {
			int adjSlot = getAdjustedSlot(slot);
			if (adjSlot > 35) {
				int armorTypeForSlot = 39 - adjSlot;
				return stack != null ? stack.getItem().isValidArmor(stack, armorTypeForSlot, getPlayer()) :  false;
			}
			return getPlayer().inventory.isItemValidForSlot(adjSlot, stack);
		}
		return false;
	}

	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (isActive()) {
			switch (side) {
				case 0:
					return hotbarSlots;
				case 1:
					return armorSlots;
				default:
					return invSlots;
			}
		}
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isActive();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return isActive();
	}
	
	// IEnergyHandler
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return Config.enablePlayerLinkEnergy;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (Config.enablePlayerLinkEnergy) {
			if (!worldObj.isRemote) {
				return storage.receiveEnergy(maxReceive, simulate);
			}
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return Config.enablePlayerLinkEnergy ? storage.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Config.enablePlayerLinkEnergy ? storage.getMaxEnergyStored() : 0;
	}
	
	// ICamo
	@Override
	public boolean canCamo() {
		return true;
	}

	@Override
	public boolean canCamoAs(Block block, int metadata) {
		return block.isOpaqueCube();
	}

	@Override
	public Block getCamo() {
		return blockCamoAs;
	}

	@Override
	public int getCamoMeta() {
		return blockCamoMetadata;
	}

	@Override
	public void setCamo(Block block, int metadata) {
		blockCamoAs = block;
		blockCamoMetadata = metadata;
        if (worldObj != null)
        {
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
	}

	@Override
	public void clearCamo() {
		setCamo(null, 0);
	}	

	// IEnergySink	
	@Optional.Method(modid = "IC2")
	@Override
	public boolean acceptsEnergyFrom(TileEntity arg0, ForgeDirection arg1) {
		return Config.enablePlayerLinkEnergy;
	}

	@Optional.Method(modid = "IC2")
	@Override
	public double getDemandedEnergy() {
		return Config.enablePlayerLinkEnergy ? (storage.getMaxEnergyStored() - storage.getEnergyStored()) / 4 : 0;
	}

	@Optional.Method(modid = "IC2")
	@Override
	public int getSinkTier() {
		return 4;
	}

	@Optional.Method(modid = "IC2")
	@Override
	public double injectEnergy(ForgeDirection side, double arg1, double arg2) {
		if (!Config.enablePlayerLinkEnergy) return arg1;
		int stored = storage.receiveEnergy((int)arg1 * 4, false);
		return arg1 - (stored / 4);
	}
	
	// End of Overrides
	public void writeData(NBTTagCompound tags) {
		if (owner != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTUtil.func_152460_a(nbttagcompound1, owner);
            tags.setTag("Owner", nbttagcompound1);
            tags.setString("OwnerName", owner.getName());
        }
		if (blockCamoAs != null) {
            tags.setInteger("camoId", Block.getIdFromBlock(blockCamoAs));
            tags.setInteger("camoMeta", blockCamoMetadata);
        }
		if (silkTouched == true) {
            tags.setBoolean("silktouched", silkTouched);
        }
		storage.writeToNBT(tags);
	}

	public void readData(NBTTagCompound tags) {
		if (tags.hasKey("Owner")) {
            owner = NBTUtil.func_152459_a(tags.getCompoundTag("Owner"));
        }
		int camoBlockId = tags.getInteger("camoId");
        if (camoBlockId != 0) {
            setCamo(Block.getBlockById(camoBlockId), tags.getInteger("camoMeta"));
        }
        if (tags.hasKey("silktouched")) {
        	silkTouched = tags.getBoolean("silktouched");
        }
        storage.readFromNBT(tags);
	}
	
	private boolean isActive() {
		return playerOnline() && !hasRedstone();
	}
	
	public boolean hasRedstone() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	public boolean playerOnline() {
		return getPlayer() != null;
	}
	
	private EntityPlayer getPlayer() {
		if (owner != null) {
			for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if (playerObj instanceof EntityPlayer) {
					if (((EntityPlayer)playerObj).getGameProfile().getId().equals(owner.getId())) {
						return (EntityPlayer)playerObj;
					}
				}
			}
		}
		return null;
	}
	
	public void setOwner(GameProfile profile) {
		owner = profile;
		loadProfile();
		if (owner == null) silkTouched = false;
	}
	
	private void loadProfile() {
        if (this.owner != null && !StringUtils.isNullOrEmpty(this.owner.getName())) {
            if (!owner.isComplete() || !owner.getProperties().containsKey("textures")) {
                GameProfile gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(owner.getName());

                if (gameprofile != null) {
                    Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);

                    if (property == null) {
                        gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
                    }

                    owner = gameprofile;
                }
            }
        }
    }
	
	public void onLoaded() {
		if (!addedToEnet && !FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			IC2Helper.registerTile(this);
			addedToEnet = true;
		}
	}
	
	public int getAdjustedSlot(int slot) {
		if (Config.enablePlayerLinkHotbar && (Config.enablePlayerLinkInv || (!Config.enablePlayerLinkInv && !Config.enablePlayerLinkArmor))) return slot;
		if (Config.enablePlayerLinkInv) return slot + 9;
		if (!Config.enablePlayerLinkHotbar && Config.enablePlayerLinkArmor) return slot + 36;
		return slot < 9 ? slot : slot + 27;
	}
}
