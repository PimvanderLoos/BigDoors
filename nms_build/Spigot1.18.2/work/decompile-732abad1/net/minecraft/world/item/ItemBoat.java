package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class ItemBoat extends Item {

    private static final Predicate<Entity> ENTITY_PREDICATE = IEntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final EntityBoat.EnumBoatType type;

    public ItemBoat(EntityBoat.EnumBoatType entityboat_enumboattype, Item.Info item_info) {
        super(item_info);
        this.type = entityboat_enumboattype;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        MovingObjectPositionBlock movingobjectpositionblock = getPlayerPOVHitResult(world, entityhuman, RayTrace.FluidCollisionOption.ANY);

        if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
            return InteractionResultWrapper.pass(itemstack);
        } else {
            Vec3D vec3d = entityhuman.getViewVector(1.0F);
            double d0 = 5.0D;
            List<Entity> list = world.getEntities((Entity) entityhuman, entityhuman.getBoundingBox().expandTowards(vec3d.scale(5.0D)).inflate(1.0D), ItemBoat.ENTITY_PREDICATE);

            if (!list.isEmpty()) {
                Vec3D vec3d1 = entityhuman.getEyePosition();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate((double) entity.getPickRadius());

                    if (axisalignedbb.contains(vec3d1)) {
                        return InteractionResultWrapper.pass(itemstack);
                    }
                }
            }

            if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                EntityBoat entityboat = new EntityBoat(world, movingobjectpositionblock.getLocation().x, movingobjectpositionblock.getLocation().y, movingobjectpositionblock.getLocation().z);

                entityboat.setType(this.type);
                entityboat.setYRot(entityhuman.getYRot());
                if (!world.noCollision(entityboat, entityboat.getBoundingBox())) {
                    return InteractionResultWrapper.fail(itemstack);
                } else {
                    if (!world.isClientSide) {
                        world.addFreshEntity(entityboat);
                        world.gameEvent(entityhuman, GameEvent.ENTITY_PLACE, new BlockPosition(movingobjectpositionblock.getLocation()));
                        if (!entityhuman.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }

                    entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
                    return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
                }
            } else {
                return InteractionResultWrapper.pass(itemstack);
            }
        }
    }
}
