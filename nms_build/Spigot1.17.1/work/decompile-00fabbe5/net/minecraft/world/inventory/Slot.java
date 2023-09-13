package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class Slot {

    public final int slot;
    public final IInventory container;
    public int index;
    public final int x;
    public final int y;

    public Slot(IInventory iinventory, int i, int j, int k) {
        this.container = iinventory;
        this.slot = i;
        this.x = j;
        this.y = k;
    }

    public void a(ItemStack itemstack, ItemStack itemstack1) {
        int i = itemstack1.getCount() - itemstack.getCount();

        if (i > 0) {
            this.a(itemstack1, i);
        }

    }

    protected void a(ItemStack itemstack, int i) {}

    protected void b(int i) {}

    protected void b_(ItemStack itemstack) {}

    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.d();
    }

    public boolean isAllowed(ItemStack itemstack) {
        return true;
    }

    public ItemStack getItem() {
        return this.container.getItem(this.slot);
    }

    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    public void set(ItemStack itemstack) {
        this.container.setItem(this.slot, itemstack);
        this.d();
    }

    public void d() {
        this.container.update();
    }

    public int getMaxStackSize() {
        return this.container.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack itemstack) {
        return Math.min(this.getMaxStackSize(), itemstack.getMaxStackSize());
    }

    @Nullable
    public Pair<MinecraftKey, MinecraftKey> c() {
        return null;
    }

    public ItemStack a(int i) {
        return this.container.splitStack(this.slot, i);
    }

    public boolean isAllowed(EntityHuman entityhuman) {
        return true;
    }

    public boolean b() {
        return true;
    }

    public Optional<ItemStack> a(int i, int j, EntityHuman entityhuman) {
        if (!this.isAllowed(entityhuman)) {
            return Optional.empty();
        } else if (!this.b(entityhuman) && j < this.getItem().getCount()) {
            return Optional.empty();
        } else {
            i = Math.min(i, j);
            ItemStack itemstack = this.a(i);

            if (itemstack.isEmpty()) {
                return Optional.empty();
            } else {
                if (this.getItem().isEmpty()) {
                    this.set(ItemStack.EMPTY);
                }

                return Optional.of(itemstack);
            }
        }
    }

    public ItemStack b(int i, int j, EntityHuman entityhuman) {
        Optional<ItemStack> optional = this.a(i, j, entityhuman);

        optional.ifPresent((itemstack) -> {
            this.a(entityhuman, itemstack);
        });
        return (ItemStack) optional.orElse(ItemStack.EMPTY);
    }

    public ItemStack e(ItemStack itemstack) {
        return this.b(itemstack, itemstack.getCount());
    }

    public ItemStack b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isAllowed(itemstack)) {
            ItemStack itemstack1 = this.getItem();
            int j = Math.min(Math.min(i, itemstack.getCount()), this.getMaxStackSize(itemstack) - itemstack1.getCount());

            if (itemstack1.isEmpty()) {
                this.set(itemstack.cloneAndSubtract(j));
            } else if (ItemStack.e(itemstack1, itemstack)) {
                itemstack.subtract(j);
                itemstack1.add(j);
                this.set(itemstack1);
            }

            return itemstack;
        } else {
            return itemstack;
        }
    }

    public boolean b(EntityHuman entityhuman) {
        return this.isAllowed(entityhuman) && this.isAllowed(this.getItem());
    }

    public int g() {
        return this.slot;
    }
}
