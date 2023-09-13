package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockShulkerBox;

public class SlotShulkerBox extends Slot {

    public SlotShulkerBox(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }
}
