package net.minecraft.world.item;

public class ItemToolMaterial extends Item {

    private final ToolMaterial tier;

    public ItemToolMaterial(ToolMaterial toolmaterial, Item.Info item_info) {
        super(item_info.b(toolmaterial.a()));
        this.tier = toolmaterial;
    }

    public ToolMaterial j() {
        return this.tier;
    }

    @Override
    public int c() {
        return this.tier.e();
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.tier.f().test(itemstack1) || super.a(itemstack, itemstack1);
    }
}
