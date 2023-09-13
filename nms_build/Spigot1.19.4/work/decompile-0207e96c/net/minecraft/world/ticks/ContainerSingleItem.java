package net.minecraft.world.ticks;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;

public interface ContainerSingleItem extends IInventory {

    @Override
    default int getContainerSize() {
        return 1;
    }

    @Override
    default boolean isEmpty() {
        return this.getFirstItem().isEmpty();
    }

    @Override
    default void clearContent() {
        this.removeFirstItem();
    }

    default ItemStack getFirstItem() {
        return this.getItem(0);
    }

    default ItemStack removeFirstItem() {
        return this.removeItemNoUpdate(0);
    }

    default void setFirstItem(ItemStack itemstack) {
        this.setItem(0, itemstack);
    }

    @Override
    default ItemStack removeItemNoUpdate(int i) {
        return this.removeItem(i, this.getMaxStackSize());
    }
}
