package net.minecraft.world.item;

public class ItemToolMaterial extends Item {

    private final ToolMaterial tier;

    public ItemToolMaterial(ToolMaterial toolmaterial, Item.Info item_info) {
        super(item_info.defaultDurability(toolmaterial.getUses()));
        this.tier = toolmaterial;
    }

    public ToolMaterial getTier() {
        return this.tier;
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack itemstack1) {
        return this.tier.getRepairIngredient().test(itemstack1) || super.isValidRepairItem(itemstack, itemstack1);
    }
}
