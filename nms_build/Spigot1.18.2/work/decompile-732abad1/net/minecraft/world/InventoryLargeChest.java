package net.minecraft.world;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class InventoryLargeChest implements IInventory {

    public final IInventory container1;
    public final IInventory container2;

    public InventoryLargeChest(IInventory iinventory, IInventory iinventory1) {
        this.container1 = iinventory;
        this.container2 = iinventory1;
    }

    @Override
    public int getContainerSize() {
        return this.container1.getContainerSize() + this.container2.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.container1.isEmpty() && this.container2.isEmpty();
    }

    public boolean contains(IInventory iinventory) {
        return this.container1 == iinventory || this.container2 == iinventory;
    }

    @Override
    public ItemStack getItem(int i) {
        return i >= this.container1.getContainerSize() ? this.container2.getItem(i - this.container1.getContainerSize()) : this.container1.getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return i >= this.container1.getContainerSize() ? this.container2.removeItem(i - this.container1.getContainerSize(), j) : this.container1.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return i >= this.container1.getContainerSize() ? this.container2.removeItemNoUpdate(i - this.container1.getContainerSize()) : this.container1.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (i >= this.container1.getContainerSize()) {
            this.container2.setItem(i - this.container1.getContainerSize(), itemstack);
        } else {
            this.container1.setItem(i, itemstack);
        }

    }

    @Override
    public int getMaxStackSize() {
        return this.container1.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.container1.setChanged();
        this.container2.setChanged();
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.container1.stillValid(entityhuman) && this.container2.stillValid(entityhuman);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        this.container1.startOpen(entityhuman);
        this.container2.startOpen(entityhuman);
    }

    @Override
    public void stopOpen(EntityHuman entityhuman) {
        this.container1.stopOpen(entityhuman);
        this.container2.stopOpen(entityhuman);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return i >= this.container1.getContainerSize() ? this.container2.canPlaceItem(i - this.container1.getContainerSize(), itemstack) : this.container1.canPlaceItem(i, itemstack);
    }

    @Override
    public void clearContent() {
        this.container1.clearContent();
        this.container2.clearContent();
    }
}
