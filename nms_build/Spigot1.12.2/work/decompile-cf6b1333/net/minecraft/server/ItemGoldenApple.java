package net.minecraft.server;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple(int i, float f, boolean flag) {
        super(i, f, flag);
        this.a(true);
    }

    public EnumItemRarity g(ItemStack itemstack) {
        return itemstack.getData() == 0 ? EnumItemRarity.RARE : EnumItemRarity.EPIC;
    }

    protected void a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            if (itemstack.getData() > 0) {
                entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, 400, 1));
                entityhuman.addEffect(new MobEffect(MobEffects.RESISTANCE, 6000, 0));
                entityhuman.addEffect(new MobEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                entityhuman.addEffect(new MobEffect(MobEffects.ABSORBTION, 2400, 3));
            } else {
                entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, 100, 1));
                entityhuman.addEffect(new MobEffect(MobEffects.ABSORBTION, 2400, 0));
            }
        }

    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            nonnulllist.add(new ItemStack(this));
            nonnulllist.add(new ItemStack(this, 1, 1));
        }

    }
}
