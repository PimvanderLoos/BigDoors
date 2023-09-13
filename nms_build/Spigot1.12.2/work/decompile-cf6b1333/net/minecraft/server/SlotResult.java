package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class SlotResult extends Slot {

    private final InventoryCrafting a;
    private final EntityHuman b;
    private int c;

    public SlotResult(EntityHuman entityhuman, InventoryCrafting inventorycrafting, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.b = entityhuman;
        this.a = inventorycrafting;
    }

    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.c += Math.min(i, this.getItem().getCount());
        }

        return super.a(i);
    }

    protected void a(ItemStack itemstack, int i) {
        this.c += i;
        this.c(itemstack);
    }

    protected void b(int i) {
        this.c += i;
    }

    protected void c(ItemStack itemstack) {
        if (this.c > 0) {
            itemstack.a(this.b.world, this.b, this.c);
        }

        this.c = 0;
        InventoryCraftResult inventorycraftresult = (InventoryCraftResult) this.inventory;
        IRecipe irecipe = inventorycraftresult.i();

        if (irecipe != null && !irecipe.c()) {
            this.b.a((List) Lists.newArrayList(new IRecipe[] { irecipe}));
            inventorycraftresult.a((IRecipe) null);
        }

    }

    public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
        this.c(itemstack);
        NonNullList nonnulllist = CraftingManager.c(this.a, entityhuman.world);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack1 = this.a.getItem(i);
            ItemStack itemstack2 = (ItemStack) nonnulllist.get(i);

            if (!itemstack1.isEmpty()) {
                this.a.splitStack(i, 1);
                itemstack1 = this.a.getItem(i);
            }

            if (!itemstack2.isEmpty()) {
                if (itemstack1.isEmpty()) {
                    this.a.setItem(i, itemstack2);
                } else if (ItemStack.c(itemstack1, itemstack2) && ItemStack.equals(itemstack1, itemstack2)) {
                    itemstack2.add(itemstack1.getCount());
                    this.a.setItem(i, itemstack2);
                } else if (!this.b.inventory.pickup(itemstack2)) {
                    this.b.drop(itemstack2, false);
                }
            }
        }

        return itemstack;
    }
}
