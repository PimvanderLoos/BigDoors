package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;

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
        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return ItemLiquidUtil.a(itemstack, entityhuman, itemstack1);
    }
}
