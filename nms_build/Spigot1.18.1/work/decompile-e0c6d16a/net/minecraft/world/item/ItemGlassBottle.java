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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        List<EntityAreaEffectCloud> list = world.getEntitiesOfClass(EntityAreaEffectCloud.class, entityhuman.getBoundingBox().inflate(2.0D), (entityareaeffectcloud) -> {
            return entityareaeffectcloud != null && entityareaeffectcloud.isAlive() && entityareaeffectcloud.getOwner() instanceof EntityEnderDragon;
        });
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!list.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = (EntityAreaEffectCloud) list.get(0);

            entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
            world.playSound((EntityHuman) null, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            world.gameEvent(entityhuman, GameEvent.FLUID_PICKUP, entityhuman.blockPosition());
            return InteractionResultWrapper.sidedSuccess(this.turnBottleIntoItem(itemstack, entityhuman, new ItemStack(Items.DRAGON_BREATH)), world.isClientSide());
        } else {
            MovingObjectPositionBlock movingobjectpositionblock = getPlayerPOVHitResult(world, entityhuman, RayTrace.FluidCollisionOption.SOURCE_ONLY);

            if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
                return InteractionResultWrapper.pass(itemstack);
            } else {
                if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                    BlockPosition blockposition = ((MovingObjectPositionBlock) movingobjectpositionblock).getBlockPos();

                    if (!world.mayInteract(entityhuman, blockposition)) {
                        return InteractionResultWrapper.pass(itemstack);
                    }

                    if (world.getFluidState(blockposition).is((Tag) TagsFluid.WATER)) {
                        world.playSound(entityhuman, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        world.gameEvent(entityhuman, GameEvent.FLUID_PICKUP, blockposition);
                        return InteractionResultWrapper.sidedSuccess(this.turnBottleIntoItem(itemstack, entityhuman, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)), world.isClientSide());
                    }
                }

                return InteractionResultWrapper.pass(itemstack);
            }
        }
    }

    protected ItemStack turnBottleIntoItem(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        return ItemLiquidUtil.createFilledResult(itemstack, entityhuman, itemstack1);
    }
}
