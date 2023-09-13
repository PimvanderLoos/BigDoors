package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerBrewingStand;
import net.minecraft.world.inventory.IContainerProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBrewingStand;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBrewingStand extends TileEntityContainer implements IWorldInventory {

    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int[] SLOTS_FOR_UP = new int[]{3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
    public static final int FUEL_USES = 20;
    public static final int DATA_BREW_TIME = 0;
    public static final int DATA_FUEL_USES = 1;
    public static final int NUM_DATA_VALUES = 2;
    private NonNullList<ItemStack> items;
    public int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    public int fuel;
    protected final IContainerProperties dataAccess;

    public TileEntityBrewingStand(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BREWING_STAND, blockposition, iblockdata);
        this.items = NonNullList.withSize(5, ItemStack.EMPTY);
        this.dataAccess = new IContainerProperties() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0:
                        return TileEntityBrewingStand.this.brewTime;
                    case 1:
                        return TileEntityBrewingStand.this.fuel;
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int i, int j) {
                switch (i) {
                    case 0:
                        TileEntityBrewingStand.this.brewTime = j;
                        break;
                    case 1:
                        TileEntityBrewingStand.this.fuel = j;
                }

            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.brewing");
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBrewingStand tileentitybrewingstand) {
        ItemStack itemstack = (ItemStack) tileentitybrewingstand.items.get(4);

        if (tileentitybrewingstand.fuel <= 0 && itemstack.is(Items.BLAZE_POWDER)) {
            tileentitybrewingstand.fuel = 20;
            itemstack.shrink(1);
            setChanged(world, blockposition, iblockdata);
        }

        boolean flag = isBrewable(tileentitybrewingstand.items);
        boolean flag1 = tileentitybrewingstand.brewTime > 0;
        ItemStack itemstack1 = (ItemStack) tileentitybrewingstand.items.get(3);

        if (flag1) {
            --tileentitybrewingstand.brewTime;
            boolean flag2 = tileentitybrewingstand.brewTime == 0;

            if (flag2 && flag) {
                doBrew(world, blockposition, tileentitybrewingstand.items);
                setChanged(world, blockposition, iblockdata);
            } else if (!flag || !itemstack1.is(tileentitybrewingstand.ingredient)) {
                tileentitybrewingstand.brewTime = 0;
                setChanged(world, blockposition, iblockdata);
            }
        } else if (flag && tileentitybrewingstand.fuel > 0) {
            --tileentitybrewingstand.fuel;
            tileentitybrewingstand.brewTime = 400;
            tileentitybrewingstand.ingredient = itemstack1.getItem();
            setChanged(world, blockposition, iblockdata);
        }

        boolean[] aboolean = tileentitybrewingstand.getPotionBits();

        if (!Arrays.equals(aboolean, tileentitybrewingstand.lastPotionCount)) {
            tileentitybrewingstand.lastPotionCount = aboolean;
            IBlockData iblockdata1 = iblockdata;

            if (!(iblockdata.getBlock() instanceof BlockBrewingStand)) {
                return;
            }

            for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
                iblockdata1 = (IBlockData) iblockdata1.setValue(BlockBrewingStand.HAS_BOTTLE[i], aboolean[i]);
            }

            world.setBlock(blockposition, iblockdata1, 2);
        }

    }

    private boolean[] getPotionBits() {
        boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private static boolean isBrewable(NonNullList<ItemStack> nonnulllist) {
        ItemStack itemstack = (ItemStack) nonnulllist.get(3);

        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewer.isIngredient(itemstack)) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = (ItemStack) nonnulllist.get(i);

                if (!itemstack1.isEmpty() && PotionBrewer.hasMix(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static void doBrew(World world, BlockPosition blockposition, NonNullList<ItemStack> nonnulllist) {
        ItemStack itemstack = (ItemStack) nonnulllist.get(3);

        for (int i = 0; i < 3; ++i) {
            nonnulllist.set(i, PotionBrewer.mix(itemstack, (ItemStack) nonnulllist.get(i)));
        }

        itemstack.shrink(1);
        if (itemstack.getItem().hasCraftingRemainingItem()) {
            ItemStack itemstack1 = new ItemStack(itemstack.getItem().getCraftingRemainingItem());

            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else {
                InventoryUtils.dropItemStack(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack1);
            }
        }

        nonnulllist.set(3, itemstack);
        world.levelEvent(1035, blockposition, 0);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerUtil.loadAllItems(nbttagcompound, this.items);
        this.brewTime = nbttagcompound.getShort("BrewTime");
        this.fuel = nbttagcompound.getByte("Fuel");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putShort("BrewTime", (short) this.brewTime);
        ContainerUtil.saveAllItems(nbttagcompound, this.items);
        nbttagcompound.putByte("Fuel", (byte) this.fuel);
    }

    @Override
    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.size() ? (ItemStack) this.items.get(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return ContainerUtil.removeItem(this.items, i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerUtil.takeItem(this.items, i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.items.size()) {
            this.items.set(i, itemstack);
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return IInventory.stillValidBlockEntity(this, entityhuman);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return i == 3 ? PotionBrewer.isIngredient(itemstack) : (i == 4 ? itemstack.is(Items.BLAZE_POWDER) : (itemstack.is(Items.POTION) || itemstack.is(Items.SPLASH_POTION) || itemstack.is(Items.LINGERING_POTION) || itemstack.is(Items.GLASS_BOTTLE)) && this.getItem(i).isEmpty());
    }

    @Override
    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? TileEntityBrewingStand.SLOTS_FOR_UP : (enumdirection == EnumDirection.DOWN ? TileEntityBrewingStand.SLOTS_FOR_DOWN : TileEntityBrewingStand.SLOTS_FOR_SIDES);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return this.canPlaceItem(i, itemstack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return i == 3 ? itemstack.is(Items.GLASS_BOTTLE) : true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerBrewingStand(i, playerinventory, this, this.dataAccess);
    }
}
