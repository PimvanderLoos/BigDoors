package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;

public class SlotShulkerBox extends Slot {

    public SlotShulkerBox(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return itemstack.getItem().canFitInsideContainerItems();
    }
}
