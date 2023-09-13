package net.minecraft.world.inventory.tooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class BundleTooltip implements TooltipComponent {

    private final NonNullList<ItemStack> items;
    private final int weight;

    public BundleTooltip(NonNullList<ItemStack> nonnulllist, int i) {
        this.items = nonnulllist;
        this.weight = i;
    }

    public NonNullList<ItemStack> a() {
        return this.items;
    }

    public int b() {
        return this.weight;
    }
}
