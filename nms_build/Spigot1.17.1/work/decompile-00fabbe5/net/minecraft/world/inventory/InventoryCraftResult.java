package net.minecraft.world.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;

public class InventoryCraftResult implements IInventory, RecipeHolder {

    private final NonNullList<ItemStack> itemStacks;
    @Nullable
    private IRecipe<?> recipeUsed;

    public InventoryCraftResult() {
        this.itemStacks = NonNullList.a(1, ItemStack.EMPTY);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.itemStacks.iterator();

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
        return (ItemStack) this.itemStacks.get(0);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.itemStacks, 0);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.itemStacks, 0);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.itemStacks.set(0, itemstack);
    }

    @Override
    public void update() {}

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void clear() {
        this.itemStacks.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> irecipe) {
        this.recipeUsed = irecipe;
    }

    @Nullable
    @Override
    public IRecipe<?> getRecipeUsed() {
        return this.recipeUsed;
    }
}
