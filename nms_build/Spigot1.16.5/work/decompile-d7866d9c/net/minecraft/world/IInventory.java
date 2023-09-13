package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IInventory extends Clearable {

    int getSize();

    boolean isEmpty();

    ItemStack getItem(int i);

    ItemStack splitStack(int i, int j);

    ItemStack splitWithoutUpdate(int i);

    void setItem(int i, ItemStack itemstack);

    default int getMaxStackSize() {
        return 64;
    }

    void update();

    boolean a(EntityHuman entityhuman);

    default void startOpen(EntityHuman entityhuman) {}

    default void closeContainer(EntityHuman entityhuman) {}

    default boolean b(int i, ItemStack itemstack) {
        return true;
    }

    default int a(Item item) {
        int i = 0;

        for (int j = 0; j < this.getSize(); ++j) {
            ItemStack itemstack = this.getItem(j);

            if (itemstack.getItem().equals(item)) {
                i += itemstack.getCount();
            }
        }

        return i;
    }

    default boolean a(Set<Item> set) {
        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (set.contains(itemstack.getItem()) && itemstack.getCount() > 0) {
                return true;
            }
        }

        return false;
    }
}
