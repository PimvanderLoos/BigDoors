package net.minecraft.server;

public class ItemFishingRod extends Item {

    public ItemFishingRod() {
        this.setMaxDurability(64);
        this.d(1);
        this.b(CreativeModeTab.i);
        this.a(new MinecraftKey("cast"), new IDynamicTexture() {
        });
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (entityhuman.hookedFish != null) {
            int i = entityhuman.hookedFish.j();

            itemstack.damage(i, entityhuman);
            entityhuman.a(enumhand);
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.J, SoundCategory.NEUTRAL, 1.0F, 0.4F / (ItemFishingRod.j.nextFloat() * 0.4F + 0.8F));
        } else {
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.L, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemFishingRod.j.nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                EntityFishingHook entityfishinghook = new EntityFishingHook(world, entityhuman);
                int j = EnchantmentManager.c(itemstack);

                if (j > 0) {
                    entityfishinghook.a(j);
                }

                int k = EnchantmentManager.b(itemstack);

                if (k > 0) {
                    entityfishinghook.c(k);
                }

                world.addEntity(entityfishinghook);
            }

            entityhuman.a(enumhand);
            entityhuman.b(StatisticList.b((Item) this));
        }

        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
    }

    public int c() {
        return 1;
    }
}
