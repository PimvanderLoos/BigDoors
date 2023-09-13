package net.minecraft.server;

public class ItemExpBottle extends Item {

    public ItemExpBottle() {
        this.b(CreativeModeTab.f);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.bz, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemExpBottle.j.nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            EntityThrownExpBottle entitythrownexpbottle = new EntityThrownExpBottle(world, entityhuman);

            entitythrownexpbottle.a(entityhuman, entityhuman.pitch, entityhuman.yaw, -20.0F, 0.7F, 1.0F);
            world.addEntity(entitythrownexpbottle);
        }

        entityhuman.b(StatisticList.b((Item) this));
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
    }
}
