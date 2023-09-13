package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IInventory extends Clearable {

    int LARGE_MAX_STACK_SIZE = 64;

    int getContainerSize();

    boolean isEmpty();

    ItemStack getItem(int i);

    ItemStack removeItem(int i, int j);

    ItemStack removeItemNoUpdate(int i);

    void setItem(int i, ItemStack itemstack);

    default int getMaxStackSize() {
        return 64;
    }

    void setChanged();

    boolean stillValid(EntityHuman entityhuman);

    default void startOpen(EntityHuman entityhuman) {}

    default void stopOpen(EntityHuman entityhuman) {}

    default boolean canPlaceItem(int i, ItemStack itemstack) {
        return true;
    }

    default int countItem(Item item) {
        int i = 0;

        for (int j = 0; j < this.getContainerSize(); ++j) {
            ItemStack itemstack = this.getItem(j);

            if (itemstack.getItem().equals(item)) {
                i += itemstack.getCount();
            }
        }

        return i;
    }

    default boolean hasAnyOf(Set<Item> set) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (set.contains(itemstack.getItem()) && itemstack.getCount() > 0) {
                return true;
            }
        }

        return false;
    }
}
