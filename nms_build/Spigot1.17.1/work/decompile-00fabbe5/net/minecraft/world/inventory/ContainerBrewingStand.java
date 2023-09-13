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
        a(iinventory, 5);
        a(icontainerproperties, 2);
        this.brewingStand = iinventory;
        this.brewingStandData = icontainerproperties;
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(iinventory, 0, 56, 51)));
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(iinventory, 1, 79, 58)));
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(iinventory, 2, 102, 51)));
        this.ingredientSlot = this.a((Slot) (new ContainerBrewingStand.SlotBrewing(iinventory, 3, 79, 17)));
        this.a((Slot) (new ContainerBrewingStand.a(iinventory, 4, 17, 17)));
        this.a(icontainerproperties);

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return this.brewingStand.a(entityhuman);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if ((i < 0 || i > 2) && i != 3 && i != 4) {
                if (ContainerBrewingStand.a.b(itemstack)) {
                    if (this.a(itemstack1, 4, 5, false) || this.ingredientSlot.isAllowed(itemstack1) && !this.a(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.isAllowed(itemstack1)) {
                    if (!this.a(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ContainerBrewingStand.SlotPotionBottle.b(itemstack) && itemstack.getCount() == 1) {
                    if (!this.a(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 5 && i < 32) {
                    if (!this.a(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 32 && i < 41) {
                    if (!this.a(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.a(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.a(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    public int l() {
        return this.brewingStandData.getProperty(1);
    }

    public int m() {
        return this.brewingStandData.getProperty(0);
    }

    private static class SlotPotionBottle extends Slot {

        public SlotPotionBottle(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean isAllowed(ItemStack itemstack) {
            return b(itemstack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void a(EntityHuman entityhuman, ItemStack itemstack) {
            PotionRegistry potionregistry = PotionUtil.d(itemstack);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.BREWED_POTION.a((EntityPlayer) entityhuman, potionregistry);
            }

            super.a(entityhuman, itemstack);
        }

        public static boolean b(ItemStack itemstack) {
            return itemstack.a(Items.POTION) || itemstack.a(Items.SPLASH_POTION) || itemstack.a(Items.LINGERING_POTION) || itemstack.a(Items.GLASS_BOTTLE);
        }
    }

    private static class SlotBrewing extends Slot {

        public SlotBrewing(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean isAllowed(ItemStack itemstack) {
            return PotionBrewer.a(itemstack);
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
        public boolean isAllowed(ItemStack itemstack) {
            return b(itemstack);
        }

        public static boolean b(ItemStack itemstack) {
            return itemstack.a(Items.BLAZE_POWDER);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}
