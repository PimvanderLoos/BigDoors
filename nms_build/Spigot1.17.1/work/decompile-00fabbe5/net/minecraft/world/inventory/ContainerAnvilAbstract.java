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
        public void update() {
            super.update();
            ContainerAnvilAbstract.this.a((IInventory) this);
        }
    };
    protected final ContainerAccess access;
    protected final EntityHuman player;

    protected abstract boolean a(EntityHuman entityhuman, boolean flag);

    protected abstract void a(EntityHuman entityhuman, ItemStack itemstack);

    protected abstract boolean a(IBlockData iblockdata);

    public ContainerAnvilAbstract(@Nullable Containers<?> containers, int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(containers, i);
        this.access = containeraccess;
        this.player = playerinventory.player;
        this.a(new Slot(this.inputSlots, 0, 27, 47));
        this.a(new Slot(this.inputSlots, 1, 76, 47));
        this.a(new Slot(this.resultSlots, 2, 134, 47) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public boolean isAllowed(EntityHuman entityhuman) {
                return ContainerAnvilAbstract.this.a(entityhuman, this.hasItem());
            }

            @Override
            public void a(EntityHuman entityhuman, ItemStack itemstack) {
                ContainerAnvilAbstract.this.a(entityhuman, itemstack);
            }
        });

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

    public abstract void l();

    @Override
    public void a(IInventory iinventory) {
        super.a(iinventory);
        if (iinventory == this.inputSlots) {
            this.l();
        }

    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, this.inputSlots);
        });
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return (Boolean) this.access.a((world, blockposition) -> {
            return !this.a(world.getType(blockposition)) ? false : entityhuman.h((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    protected boolean c(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 39) {
                    int j = this.c(itemstack) ? 1 : 0;

                    if (!this.a(itemstack1, j, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
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
}
