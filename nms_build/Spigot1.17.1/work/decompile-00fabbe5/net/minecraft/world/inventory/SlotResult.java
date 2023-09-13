package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;

public class SlotResult extends Slot {

    private final InventoryCrafting craftSlots;
    private final EntityHuman player;
    private int removeCount;

    public SlotResult(EntityHuman entityhuman, InventoryCrafting inventorycrafting, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.player = entityhuman;
        this.craftSlots = inventorycrafting;
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.removeCount += Math.min(i, this.getItem().getCount());
        }

        return super.a(i);
    }

    @Override
    protected void a(ItemStack itemstack, int i) {
        this.removeCount += i;
        this.b_(itemstack);
    }

    @Override
    protected void b(int i) {
        this.removeCount += i;
    }

    @Override
    protected void b_(ItemStack itemstack) {
        if (this.removeCount > 0) {
            itemstack.a(this.player.level, this.player, this.removeCount);
        }

        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder) this.container).awardUsedRecipes(this.player);
        }

        this.removeCount = 0;
    }

    @Override
    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.b_(itemstack);
        NonNullList<ItemStack> nonnulllist = entityhuman.level.getCraftingManager().c(Recipes.CRAFTING, this.craftSlots, entityhuman.level);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack1 = this.craftSlots.getItem(i);
            ItemStack itemstack2 = (ItemStack) nonnulllist.get(i);

            if (!itemstack1.isEmpty()) {
                this.craftSlots.splitStack(i, 1);
                itemstack1 = this.craftSlots.getItem(i);
            }

            if (!itemstack2.isEmpty()) {
                if (itemstack1.isEmpty()) {
                    this.craftSlots.setItem(i, itemstack2);
                } else if (ItemStack.c(itemstack1, itemstack2) && ItemStack.equals(itemstack1, itemstack2)) {
                    itemstack2.add(itemstack1.getCount());
                    this.craftSlots.setItem(i, itemstack2);
                } else if (!this.player.getInventory().pickup(itemstack2)) {
                    this.player.drop(itemstack2, false);
                }
            }
        }

    }
}
