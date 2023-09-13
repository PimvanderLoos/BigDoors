package net.minecraft.server;

public class SlotShulkerBox extends Slot {

    public SlotShulkerBox(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    public boolean isAllowed(ItemStack itemstack) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }
}
