package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ICrafting {

    void a(Container container, NonNullList<ItemStack> nonnulllist);

    void a(Container container, int i, ItemStack itemstack);

    void setContainerData(Container container, int i, int j);
}
