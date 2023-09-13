package net.minecraft.world;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class InventoryLargeChest implements IInventory {

    public final IInventory container1;
    public final IInventory container2;

    public InventoryLargeChest(IInventory iinventory, IInventory iinventory1) {
        if (iinventory == null) {
            iinventory = iinventory1;
        }

        if (iinventory1 == null) {
            iinventory1 = iinventory;
        }

        this.container1 = iinventory;
        this.container2 = iinventory1;
    }

    @Override
    public int getSize() {
        return this.container1.getSize() + this.container2.getSize();
    }

    @Override
    public boolean isEmpty() {
        return this.container1.isEmpty() && this.container2.isEmpty();
    }

    public boolean a(IInventory iinventory) {
        return this.container1 == iinventory || this.container2 == iinventory;
    }

    @Override
    public ItemStack getItem(int i) {
        return i >= this.container1.getSize() ? this.container2.getItem(i - this.container1.getSize()) : this.container1.getItem(i);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        return i >= this.container1.getSize() ? this.container2.splitStack(i - this.container1.getSize(), j) : this.container1.splitStack(i, j);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return i >= this.container1.getSize() ? this.container2.splitWithoutUpdate(i - this.container1.getSize()) : this.container1.splitWithoutUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (i >= this.container1.getSize()) {
            this.container2.setItem(i - this.container1.getSize(), itemstack);
        } else {
            this.container1.setItem(i, itemstack);
        }

    }

    @Override
    public int getMaxStackSize() {
        return this.container1.getMaxStackSize();
    }

    @Override
    public void update() {
        this.container1.update();
        this.container2.update();
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return this.container1.a(entityhuman) && this.container2.a(entityhuman);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        this.container1.startOpen(entityhuman);
        this.container2.startOpen(entityhuman);
    }

    @Override
    public void closeContainer(EntityHuman entityhuman) {
        this.container1.closeContainer(entityhuman);
        this.container2.closeContainer(entityhuman);
    }

    @Override
    public boolean b(int i, ItemStack itemstack) {
        return i >= this.container1.getSize() ? this.container2.b(i - this.container1.getSize(), itemstack) : this.container1.b(i, itemstack);
    }

    @Override
    public void clear() {
        this.container1.clear();
        this.container2.clear();
    }
}
