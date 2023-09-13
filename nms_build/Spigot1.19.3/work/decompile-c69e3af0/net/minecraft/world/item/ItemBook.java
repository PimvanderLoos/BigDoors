package net.minecraft.world.item;

public class ItemBook extends Item {

    public ItemBook(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean isEnchantable(ItemStack itemstack) {
        return itemstack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}
