package net.minecraft.world.inventory;

import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class InventoryCrafting implements IInventory, AutoRecipeOutput {

    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;
    public final Container menu;

    public InventoryCrafting(Container container, int i, int j) {
        this.items = NonNullList.withSize(i * j, ItemStack.EMPTY);
        this.menu = container;
        this.width = i;
        this.height = j;
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

    @Override
    public ItemStack getItem(int i) {
        return i >= this.getContainerSize() ? ItemStack.EMPTY : (ItemStack) this.items.get(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerUtil.takeItem(this.items, i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemstack = ContainerUtil.removeItem(this.items, i, j);

        if (!itemstack.isEmpty()) {
            this.menu.slotsChanged(this);
        }

        return itemstack;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.items.set(i, itemstack);
        this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void fillStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.accountSimpleStack(itemstack);
        }

    }
}
