package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ContainerBeacon extends Container {

    private static final int PAYMENT_SLOT = 0;
    private static final int SLOT_COUNT = 1;
    private static final int DATA_COUNT = 3;
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = 28;
    private static final int USE_ROW_SLOT_START = 28;
    private static final int USE_ROW_SLOT_END = 37;
    private final IInventory beacon;
    private final ContainerBeacon.SlotBeacon paymentSlot;
    private final ContainerAccess access;
    private final IContainerProperties beaconData;

    public ContainerBeacon(int i, IInventory iinventory) {
        this(i, iinventory, new ContainerProperties(3), ContainerAccess.NULL);
    }

    public ContainerBeacon(int i, IInventory iinventory, IContainerProperties icontainerproperties, ContainerAccess containeraccess) {
        super(Containers.BEACON, i);
        this.beacon = new InventorySubcontainer(1) {
            @Override
            public boolean b(int j, ItemStack itemstack) {
                return itemstack.a((Tag) TagsItem.BEACON_PAYMENT_ITEMS);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
        a(icontainerproperties, 3);
        this.beaconData = icontainerproperties;
        this.access = containeraccess;
        this.paymentSlot = new ContainerBeacon.SlotBeacon(this.beacon, 0, 136, 110);
        this.a((Slot) this.paymentSlot);
        this.a(icontainerproperties);
        boolean flag = true;
        boolean flag1 = true;

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(iinventory, k + j * 9 + 9, 36 + k * 18, 137 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(iinventory, j, 36 + j * 18, 195));
        }

    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        if (!entityhuman.level.isClientSide) {
            ItemStack itemstack = this.paymentSlot.a(this.paymentSlot.getMaxStackSize());

            if (!itemstack.isEmpty()) {
                entityhuman.drop(itemstack, false);
            }

        }
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.BEACON);
    }

    @Override
    public void setContainerData(int i, int j) {
        super.setContainerData(i, j);
        this.d();
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (!this.paymentSlot.hasItem() && this.paymentSlot.isAllowed(itemstack1) && itemstack1.getCount() == 1) {
                if (!this.a(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 1 && i < 28) {
                if (!this.a(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 28 && i < 37) {
                if (!this.a(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY;
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
        return this.beaconData.getProperty(0);
    }

    @Nullable
    public MobEffectList m() {
        return MobEffectList.fromId(this.beaconData.getProperty(1));
    }

    @Nullable
    public MobEffectList n() {
        return MobEffectList.fromId(this.beaconData.getProperty(2));
    }

    public void c(int i, int j) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.setProperty(1, i);
            this.beaconData.setProperty(2, j);
            this.paymentSlot.a(1);
        }

    }

    public boolean o() {
        return !this.beacon.getItem(0).isEmpty();
    }

    private class SlotBeacon extends Slot {

        public SlotBeacon(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean isAllowed(ItemStack itemstack) {
            return itemstack.a((Tag) TagsItem.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
