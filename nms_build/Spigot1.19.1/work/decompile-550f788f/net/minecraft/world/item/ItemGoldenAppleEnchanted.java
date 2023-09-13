package net.minecraft.world.item;

public class ItemGoldenAppleEnchanted extends Item {

    public ItemGoldenAppleEnchanted(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }
}
