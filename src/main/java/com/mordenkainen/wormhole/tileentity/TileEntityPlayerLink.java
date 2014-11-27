package com.mordenkainen.wormhole.tileentity;

// Minecraft
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
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
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;

// Google
import com.google.common.collect.Iterables;

// IC2
import ic2.api.energy.tile.IEnergySink;

// COFH
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;

// Botania
import vazkii.botania.api.mana.IManaReceiver;

// Wormhole
import com.mordenkainen.wormhole.mod.IC2Helper;
import com.mordenkainen.wormhole.mod.ModHelper;
import com.mordenkainen.wormhole.config.Config;
import com.mordenkainen.wormhole.mod.BotaniaHelper;

@Optional.InterfaceList({
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
	@Optional.Interface(iface = "vazkii.botania.api.mana.IManaReceiver", modid = "Botania")
})
public class TileEntityPlayerLink extends TileEntity implements ISidedInventory, IEnergyHandler, ICamo, IEnergySink, IManaReceiver  {
	public Block blockCamoAs = null;
    public int blockCamoMetadata = 0;
    public EnergyStorage storage = new EnergyStorage(60000);
    public boolean silkTouched = false;
    public GameProfile owner = null;
    private boolean addedToEnet;
    private int currentMana = 0;
    
	private static final int[] armorSlots;
	private static final int[] invSlots;
	private static final int[] hotbarSlots;
	private static final int numSlots;
	private static final int MAXMANA = 100000;

	static {
		int maxSlot = 0;
		int[] slots;
		int i;
		numSlots = (Config.enablePlayerLinkHotbar ? 9 : 0) + (Config.enablePlayerLinkInv ? 27 : 0) + (Config.enablePlayerLinkArmor ? 4 : 0) + (Config.enablePlayerLinkFood || Config.enablePlayerLinkPotions ? 1 : 0);
		if (Config.enablePlayerLinkHotbar) {
			hotbarSlots= new int[] {0,1,2,3,4,5,6,7,8};
			maxSlot = 9;
		} else {
			hotbarSlots= new int[] {};
		}
		if (Config.enablePlayerLinkInv) {
			slots = new int[27];
			for (i = 0; i < slots.length; i++) slots[i] = i + maxSlot;
			maxSlot += i;
			invSlots = slots.clone();
		} else {
			invSlots = new int[] {};
		}
		if (Config.enablePlayerLinkArmor) {
			slots = Config.enablePlayerLinkFood || Config.enablePlayerLinkPotions ? new int[5] : new int [4];
			for (i = 0; i < slots.length; i++) slots[i] = i + maxSlot;
			armorSlots = slots.clone();
		} else {
			armorSlots = Config.enablePlayerLinkFood || Config.enablePlayerLinkPotions ? new int[] {maxSlot} : new int [] {};
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
		if (ModHelper.IC2Loaded) {
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
								if (ModHelper.IC2Loaded) {
									moved += IC2Helper.chargeItem(stack, toSend - moved);
								}
							}
						}
						if (moved >= toSend) break;
					}
					if (moved > 0) {
						storage.extractEnergy(moved, false);
					}
				}
			}
			if (ModHelper.BotaniaLoaded) {
				IInventory playerInv = getPlayer().inventory;
				for (int i = 0; i < playerInv.getSizeInventory() && currentMana > 0; i++) {
					ItemStack stack = playerInv.getStackInSlot(i);
					if (stack != null) {
						currentMana -= BotaniaHelper.chargeItem(stack, currentMana);
					}
				}
			}
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if (ModHelper.IC2Loaded && addedToEnet) {
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
		return isActive() ? getAdjustedSlot(slot) < 40 ? getPlayer().inventory.getStackInSlot(getAdjustedSlot(slot)) : null : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int numItems) {
		return isActive() ? getAdjustedSlot(slot) < 40 ? getPlayer().inventory.decrStackSize(getAdjustedSlot(slot), numItems) : null : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return isActive() ? getAdjustedSlot(slot) < 40 ? getPlayer().inventory.getStackInSlotOnClosing(getAdjustedSlot(slot)) : null : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isActive()) {
			EntityPlayer player = getPlayer();
			int adjSlot = getAdjustedSlot(slot);
			if (adjSlot == 40) {
				if(Config.enablePlayerLinkPotions && stack != null && stack.getItem() instanceof ItemPotion){
                    ItemStack remainingItem = stack.onFoodEaten(player.worldObj, player);
                    remainingItem = ForgeEventFactory.onItemUseFinish(player, stack, 0, remainingItem);
                    if(remainingItem != null && remainingItem.stackSize > 0 && remainingItem != stack) {
                        if(!player.inventory.addItemStackToInventory(remainingItem)) {
                            player.dropPlayerItemWithRandomChoice(remainingItem, false);
                        }
                    }
				} else if(Config.enablePlayerLinkFood && stack != null){
					int startValue = stack.stackSize;
                    while(stack.stackSize > 0 && player.getFoodStats().getFoodLevel() < 20) {
                        ItemStack remainingItem = stack.onFoodEaten(player.worldObj, player);
                        remainingItem = ForgeEventFactory.onItemUseFinish(player, stack, 0, remainingItem);
                        if(remainingItem != null && remainingItem.stackSize > 0 && remainingItem != stack) {
                            if(!player.inventory.addItemStackToInventory(remainingItem)) {
                                player.dropPlayerItemWithRandomChoice(remainingItem, false);
                            }
                        }
                        if(stack.stackSize == startValue) break;
                    }
                    if (stack.stackSize > 0) {
                    	if(!player.inventory.addItemStackToInventory(stack)) {
                            player.dropPlayerItemWithRandomChoice(stack, false);
                        }
                    }
				}
			} else {
				player.inventory.setInventorySlotContents(getAdjustedSlot(slot), stack);
			}
		} else if (worldObj != null && !worldObj.isRemote && stack != null) {
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
		return isItemValid(slot, stack);
	}

	// ISidedInventory
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isItemValid(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return getAdjustedSlot(slot) < 40 ? isActive() : false;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		switch (side) {
			case 0:
				return hotbarSlots;
			case 1:
				return armorSlots;
			default:
				return invSlots;
		}
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
	
	// IManaReceiver
	@Optional.Method(modid = "Botania")
	@Override
	public int getCurrentMana() {
		return currentMana;
	}

	@Optional.Method(modid = "Botania")
	@Override
	public boolean canRecieveManaFromBursts() {
		return true;
	}

	@Optional.Method(modid = "Botania")
	@Override
	public boolean isFull() {
		return currentMana >= MAXMANA;
	}

	@Optional.Method(modid = "Botania")
	@Override
	public void recieveMana(int mana) {
		currentMana += mana;
		System.out.println(currentMana);
		
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
		tags.setInteger("currentMana", currentMana);
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
        if (tags.hasKey("currentMana")) {
            currentMana = tags.getInteger("currentMana");
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
		if ((Config.enablePlayerLinkFood || Config.enablePlayerLinkPotions )&& slot == armorSlots[armorSlots.length - 1]) return 40;
		if (Config.enablePlayerLinkHotbar && (Config.enablePlayerLinkInv || (!Config.enablePlayerLinkInv && !Config.enablePlayerLinkArmor))) return slot;
		if (Config.enablePlayerLinkInv) return slot + 9;
		if (!Config.enablePlayerLinkHotbar && Config.enablePlayerLinkArmor) return slot + 36;
		return slot < 9 ? slot : slot + 27;
	}
	
	private int getFoodValue(ItemStack item){
        return item != null && item.getItem() instanceof ItemFood ? ((ItemFood)item.getItem()).func_150905_g(item) : 0;
    }
	
	private boolean isItemValid(int slot, ItemStack stack) {
		if (!isActive() && stack == null) return false;
		int adjSlot = getAdjustedSlot(slot);
		if (Config.enablePlayerLinkArmor && adjSlot >= 36 && adjSlot <= 39) {
			int armorTypeForSlot = 39 - adjSlot;
			return stack.getItem().isValidArmor(stack, armorTypeForSlot, getPlayer());
		}
		if (adjSlot == 40) {
			if (Config.enablePlayerLinkFood && getFoodValue(stack) > 0) {
				if(20 - getPlayer().getFoodStats().getFoodLevel() >= getFoodValue(stack)) {
	                return true;
	            }
			}
			if (Config.enablePlayerLinkPotions) {
		        return stack.getItem() instanceof ItemPotion;
			}
			return false;
		}
		
		return getPlayer().inventory.isItemValidForSlot(adjSlot, stack);
	}
}
