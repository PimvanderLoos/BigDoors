package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class ContainerAnvilAbstract extends Container {

    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    protected final InventoryCraftResult resultSlots = new InventoryCraftResult();
    protected final IInventory inputSlots = new InventorySubcontainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ContainerAnvilAbstract.this.slotsChanged(this);
        }
    };
    protected final ContainerAccess access;
    protected final EntityHuman player;

    protected abstract boolean mayPickup(EntityHuman entityhuman, boolean flag);

    protected abstract void onTake(EntityHuman entityhuman, ItemStack itemstack);

    protected abstract boolean isValidBlock(IBlockData iblockdata);

    public ContainerAnvilAbstract(@Nullable Containers<?> containers, int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(containers, i);
        this.access = containeraccess;
        this.player = playerinventory.player;
        this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlot(new Slot(this.resultSlots, 2, 134, 47) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return false;
            }

            @Override
            public boolean mayPickup(EntityHuman entityhuman) {
                return ContainerAnvilAbstract.this.mayPickup(entityhuman, this.hasItem());
            }

            @Override
            public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
                ContainerAnvilAbstract.this.onTake(entityhuman, itemstack);
            }
        });

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

    public abstract void createResult();

    @Override
    public void slotsChanged(IInventory iinventory) {
        super.slotsChanged(iinventory);
        if (iinventory == this.inputSlots) {
            this.createResult();
        }

    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.inputSlots);
        });
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return (Boolean) this.access.evaluate((world, blockposition) -> {
            return !this.isValidBlock(world.getBlockState(blockposition)) ? false : entityhuman.distanceToSqr((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 39) {
                    int j = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;

                    if (!this.moveItemStackTo(itemstack1, j, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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
}
