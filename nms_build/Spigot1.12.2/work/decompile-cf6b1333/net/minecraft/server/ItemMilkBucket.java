package net.minecraft.server;

public class ItemMilkBucket extends Item {

    public ItemMilkBucket() {
        this.d(1);
        this.b(CreativeModeTab.f);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;

            CriterionTriggers.y.a(entityplayer, itemstack);
            entityplayer.b(StatisticList.b((Item) this));
        }

        if (entityliving instanceof EntityHuman && !((EntityHuman) entityliving).abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        if (!world.isClientSide) {
            entityliving.removeAllEffects();
        }

        return itemstack.isEmpty() ? new ItemStack(Items.BUCKET) : itemstack;
    }

    public int e(ItemStack itemstack) {
        return 32;
    }

    public EnumAnimation f(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.c(enumhand);
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, entityhuman.b(enumhand));
    }
}
