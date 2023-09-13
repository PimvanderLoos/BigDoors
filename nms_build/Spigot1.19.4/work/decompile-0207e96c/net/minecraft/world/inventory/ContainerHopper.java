package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;

public class ContainerHopper extends Container {

    public static final int CONTAINER_SIZE = 5;
    private final IInventory hopper;

    public ContainerHopper(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, new InventorySubcontainer(5));
    }

    public ContainerHopper(int i, PlayerInventory playerinventory, IInventory iinventory) {
        super(Containers.HOPPER, i);
        this.hopper = iinventory;
        checkContainerSize(iinventory, 5);
        iinventory.startOpen(playerinventory.player);
        boolean flag = true;

        int j;

        for (j = 0; j < 5; ++j) {
            this.addSlot(new Slot(iinventory, j, 44 + j * 18, 20));
        }

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, j * 18 + 51));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 109));
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.hopper.stillValid(entityhuman);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i < this.hopper.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.hopper.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.hopper.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.hopper.stopOpen(entityhuman);
    }
}
