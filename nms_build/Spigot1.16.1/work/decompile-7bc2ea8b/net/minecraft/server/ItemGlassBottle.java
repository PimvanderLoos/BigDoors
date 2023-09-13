package net.minecraft.server;

import java.util.List;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        List<EntityAreaEffectCloud> list = world.a(EntityAreaEffectCloud.class, entityhuman.getBoundingBox().g(2.0D), (entityareaeffectcloud) -> {
            return entityareaeffectcloud != null && entityareaeffectcloud.isAlive() && entityareaeffectcloud.getSource() instanceof EntityEnderDragon;
        });
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!list.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = (EntityAreaEffectCloud) list.get(0);

            entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
            world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return InteractionResultWrapper.a(this.a(itemstack, entityhuman, new ItemStack(Items.DRAGON_BREATH)), world.s_());
        } else {
            MovingObjectPositionBlock movingobjectpositionblock = a(world, entityhuman, RayTrace.FluidCollisionOption.SOURCE_ONLY);

            if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
                return InteractionResultWrapper.pass(itemstack);
            } else {
                if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                    BlockPosition blockposition = ((MovingObjectPositionBlock) movingobjectpositionblock).getBlockPosition();

                    if (!world.a(entityhuman, blockposition)) {
                        return InteractionResultWrapper.pass(itemstack);
                    }

                    if (world.getFluid(blockposition).a((Tag) TagsFluid.WATER)) {
                        world.playSound(entityhuman, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        return InteractionResultWrapper.a(this.a(itemstack, entityhuman, PotionUtil.a(new ItemStack(Items.POTION), Potions.WATER)), world.s_());
                    }
                }

                return InteractionResultWrapper.pass(itemstack);
            }
        }
    }

    protected ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        itemstack.subtract(1);
        entityhuman.b(StatisticList.ITEM_USED.b(this));
        if (itemstack.isEmpty()) {
            return itemstack1;
        } else {
            if (!entityhuman.inventory.pickup(itemstack1)) {
                entityhuman.drop(itemstack1, false);
            }

            return itemstack;
        }
    }
}
