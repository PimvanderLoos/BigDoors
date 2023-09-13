package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this.b(CreativeModeTab.k);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        List list = world.a(EntityAreaEffectCloud.class, entityhuman.getBoundingBox().g(2.0D), new Predicate() {
            public boolean a(@Nullable EntityAreaEffectCloud entityareaeffectcloud) {
                return entityareaeffectcloud != null && entityareaeffectcloud.isAlive() && entityareaeffectcloud.getSource() instanceof EntityEnderDragon;
            }

            public boolean apply(@Nullable Object object) {
                return this.a((EntityAreaEffectCloud) object);
            }
        });
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!list.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = (EntityAreaEffectCloud) list.get(0);

            entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.O, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, this.a(itemstack, entityhuman, new ItemStack(Items.DRAGON_BREATH)));
        } else {
            MovingObjectPosition movingobjectposition = this.a(world, entityhuman, true);

            if (movingobjectposition == null) {
                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            } else {
                if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                    BlockPosition blockposition = movingobjectposition.a();

                    if (!world.a(entityhuman, blockposition) || !entityhuman.a(blockposition.shift(movingobjectposition.direction), movingobjectposition.direction, itemstack)) {
                        return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                    }

                    if (world.getType(blockposition).getMaterial() == Material.WATER) {
                        world.a(entityhuman, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.N, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, this.a(itemstack, entityhuman, PotionUtil.a(new ItemStack(Items.POTION), Potions.b)));
                    }
                }

                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            }
        }
    }

    protected ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        itemstack.subtract(1);
        entityhuman.b(StatisticList.b((Item) this));
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
