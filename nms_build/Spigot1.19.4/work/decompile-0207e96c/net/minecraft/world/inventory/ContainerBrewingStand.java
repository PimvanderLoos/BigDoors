package net.minecraft.world.inventory;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewer;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;

public class ContainerBrewingStand extends Container {

    private static final int BOTTLE_SLOT_START = 0;
    private static final int BOTTLE_SLOT_END = 2;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int SLOT_COUNT = 5;
    private static final int DATA_COUNT = 2;
    private static final int INV_SLOT_START = 5;
    private static final int INV_SLOT_END = 32;
    private static final int USE_ROW_SLOT_START = 32;
    private static final int USE_ROW_SLOT_END = 41;
    private final IInventory brewingStand;
    private final IContainerProperties brewingStandData;
    private final Slot ingredientSlot;

    public ContainerBrewingStand(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, new InventorySubcontainer(5), new ContainerProperties(2));
    }

    public ContainerBrewingStand(int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.BREWING_STAND, i);
        checkContainerSize(iinventory, 5);
        checkContainerDataCount(icontainerproperties, 2);
        this.brewingStand = iinventory;
        this.brewingStandData = icontainerproperties;
        this.addSlot(new ContainerBrewingStand.SlotPotionBottle(iinventory, 0, 56, 51));
        this.addSlot(new ContainerBrewingStand.SlotPotionBottle(iinventory, 1, 79, 58));
        this.addSlot(new ContainerBrewingStand.SlotPotionBottle(iinventory, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new ContainerBrewingStand.SlotBrewing(iinventory, 3, 79, 17));
        this.addSlot(new ContainerBrewingStand.a(iinventory, 4, 17, 17));
        this.addDataSlots(icontainerproperties);

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.brewingStand.stillValid(entityhuman);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if ((i < 0 || i > 2) && i != 3 && i != 4) {
                if (ContainerBrewingStand.a.mayPlaceItem(itemstack)) {
                    if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ContainerBrewingStand.SlotPotionBottle.mayPlaceItem(itemstack) && itemstack.getCount() == 1) {
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 5 && i < 32) {
                    if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 32 && i < 41) {
                    if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityhuman, itemstack1);
        }

        return itemstack;
    }

    public int getFuel() {
        return this.brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return this.brewingStandData.get(0);
    }

    private static class SlotPotionBottle extends Slot {

        public SlotPotionBottle(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemstack) {
            return mayPlaceItem(itemstack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
            PotionRegistry potionregistry = PotionUtil.getPotion(itemstack);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.BREWED_POTION.trigger((EntityPlayer) entityhuman, potionregistry);
            }

            super.onTake(entityhuman, itemstack);
        }

        public static boolean mayPlaceItem(ItemStack itemstack) {
            return itemstack.is(Items.POTION) || itemstack.is(Items.SPLASH_POTION) || itemstack.is(Items.LINGERING_POTION) || itemstack.is(Items.GLASS_BOTTLE);
        }
    }

    private static class SlotBrewing extends Slot {

        public SlotBrewing(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemstack) {
            return PotionBrewer.isIngredient(itemstack);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    private static class a extends Slot {

        public a(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemstack) {
            return mayPlaceItem(itemstack);
        }

        public static boolean mayPlaceItem(ItemStack itemstack) {
            return itemstack.is(Items.BLAZE_POWDER);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}
