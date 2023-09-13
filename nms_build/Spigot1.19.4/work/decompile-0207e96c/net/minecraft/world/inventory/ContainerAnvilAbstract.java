package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class ContainerAnvilAbstract extends Container {

    private static final int INVENTORY_SLOTS_PER_ROW = 9;
    private static final int INVENTORY_SLOTS_PER_COLUMN = 3;
    protected final ContainerAccess access;
    protected final EntityHuman player;
    protected final IInventory inputSlots;
    private final List<Integer> inputSlotIndexes;
    protected final InventoryCraftResult resultSlots = new InventoryCraftResult();
    private final int resultSlotIndex;

    protected abstract boolean mayPickup(EntityHuman entityhuman, boolean flag);

    protected abstract void onTake(EntityHuman entityhuman, ItemStack itemstack);

    protected abstract boolean isValidBlock(IBlockData iblockdata);

    public ContainerAnvilAbstract(@Nullable Containers<?> containers, int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(containers, i);
        this.access = containeraccess;
        this.player = playerinventory.player;
        ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createInputSlotDefinitions();

        this.inputSlots = this.createContainer(itemcombinermenuslotdefinition.getNumOfInputSlots());
        this.inputSlotIndexes = itemcombinermenuslotdefinition.getInputSlotIndexes();
        this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
        this.createInputSlots(itemcombinermenuslotdefinition);
        this.createResultSlot(itemcombinermenuslotdefinition);
        this.createInventorySlots(playerinventory);
    }

    private void createInputSlots(ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition) {
        Iterator iterator = itemcombinermenuslotdefinition.getSlots().iterator();

        while (iterator.hasNext()) {
            final ItemCombinerMenuSlotDefinition.b itemcombinermenuslotdefinition_b = (ItemCombinerMenuSlotDefinition.b) iterator.next();

            this.addSlot(new Slot(this.inputSlots, itemcombinermenuslotdefinition_b.slotIndex(), itemcombinermenuslotdefinition_b.x(), itemcombinermenuslotdefinition_b.y()) {
                @Override
                public boolean mayPlace(ItemStack itemstack) {
                    return itemcombinermenuslotdefinition_b.mayPlace().test(itemstack);
                }
            });
        }

    }

    private void createResultSlot(ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition) {
        this.addSlot(new Slot(this.resultSlots, itemcombinermenuslotdefinition.getResultSlot().slotIndex(), itemcombinermenuslotdefinition.getResultSlot().x(), itemcombinermenuslotdefinition.getResultSlot().y()) {
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
    }

    private void createInventorySlots(PlayerInventory playerinventory) {
        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

    }

    public abstract void createResult();

    protected abstract ItemCombinerMenuSlotDefinition createInputSlotDefinitions();

    private InventorySubcontainer createContainer(int i) {
        return new InventorySubcontainer(i) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerAnvilAbstract.this.slotsChanged(this);
            }
        };
    }

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

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            int j = this.getInventorySlotStart();
            int k = this.getUseRowEnd();

            if (i == this.getResultSlot()) {
                if (!this.moveItemStackTo(itemstack1, j, k, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (this.inputSlotIndexes.contains(i)) {
                if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.canMoveIntoInputSlots(itemstack1) && i >= this.getInventorySlotStart() && i < this.getUseRowEnd()) {
                int l = this.getSlotToQuickMoveTo(itemstack);

                if (!this.moveItemStackTo(itemstack1, l, this.getResultSlot(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= this.getInventorySlotStart() && i < this.getInventorySlotEnd()) {
                if (!this.moveItemStackTo(itemstack1, this.getUseRowStart(), this.getUseRowEnd(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= this.getUseRowStart() && i < this.getUseRowEnd() && !this.moveItemStackTo(itemstack1, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)) {
                return ItemStack.EMPTY;
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

    protected boolean canMoveIntoInputSlots(ItemStack itemstack) {
        return true;
    }

    public int getSlotToQuickMoveTo(ItemStack itemstack) {
        return this.inputSlots.isEmpty() ? 0 : (Integer) this.inputSlotIndexes.get(0);
    }

    public int getResultSlot() {
        return this.resultSlotIndex;
    }

    private int getInventorySlotStart() {
        return this.getResultSlot() + 1;
    }

    private int getInventorySlotEnd() {
        return this.getInventorySlotStart() + 27;
    }

    private int getUseRowStart() {
        return this.getInventorySlotEnd();
    }

    private int getUseRowEnd() {
        return this.getUseRowStart() + 9;
    }
}
