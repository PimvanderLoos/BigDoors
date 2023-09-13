package net.minecraft.server;

public class ItemHoneyBottle extends Item {

    public ItemHoneyBottle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        super.a(itemstack, world, entityliving);
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;

            CriterionTriggers.z.a(entityplayer, itemstack);
            entityplayer.b(StatisticList.ITEM_USED.b(this));
        }

        if (!world.isClientSide) {
            entityliving.removeEffect(MobEffects.POISON);
        }

        if (itemstack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entityliving instanceof EntityHuman && !((EntityHuman) entityliving).abilities.canInstantlyBuild) {
                ItemStack itemstack1 = new ItemStack(Items.GLASS_BOTTLE);
                EntityHuman entityhuman = (EntityHuman) entityliving;

                if (!entityhuman.inventory.pickup(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }
            }

            return itemstack;
        }
    }

    @Override
    public int e_(ItemStack itemstack) {
        return 40;
    }

    @Override
    public EnumAnimation d_(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    @Override
    public SoundEffect ag_() {
        return SoundEffects.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public SoundEffect af_() {
        return SoundEffects.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return ItemLiquidUtil.a(world, entityhuman, enumhand);
    }
}
