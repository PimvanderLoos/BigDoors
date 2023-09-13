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
        this.items = NonNullList.a(i * j, ItemStack.EMPTY);
        this.menu = container;
        this.width = i;
        this.height = j;
    }

    @Override
    public int getSize() {
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
        return i >= this.getSize() ? ItemStack.EMPTY : (ItemStack) this.items.get(i);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack itemstack = ContainerUtil.a(this.items, i, j);

        if (!itemstack.isEmpty()) {
            this.menu.a((IInventory) this);
        }

        return itemstack;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.items.set(i, itemstack);
        this.menu.a((IInventory) this);
    }

    @Override
    public void update() {}

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    public int f() {
        return this.height;
    }

    public int g() {
        return this.width;
    }

    @Override
    public void a(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.a(itemstack);
        }

    }
}
