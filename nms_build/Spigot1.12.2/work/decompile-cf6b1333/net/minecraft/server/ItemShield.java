package net.minecraft.server;

public class ItemShield extends Item {

    public ItemShield() {
        this.maxStackSize = 1;
        this.b(CreativeModeTab.j);
        this.setMaxDurability(336);
        this.a(new MinecraftKey("blocking"), new IDynamicTexture() {
        });
        BlockDispenser.REGISTRY.a(this, ItemArmor.b);
    }

    public String b(ItemStack itemstack) {
        if (itemstack.d("BlockEntityTag") != null) {
            EnumColor enumcolor = TileEntityBanner.d(itemstack);

            return LocaleI18n.get("item.shield." + enumcolor.d() + ".name");
        } else {
            return LocaleI18n.get("item.shield.name");
        }
    }

    public EnumAnimation f(ItemStack itemstack) {
        return EnumAnimation.BLOCK;
    }

    public int e(ItemStack itemstack) {
        return 72000;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.c(enumhand);
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.getItem() == Item.getItemOf(Blocks.PLANKS) ? true : super.a(itemstack, itemstack1);
    }
}
