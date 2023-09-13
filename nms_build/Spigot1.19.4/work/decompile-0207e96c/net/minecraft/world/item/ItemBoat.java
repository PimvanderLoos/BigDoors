package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.ChestBoat;
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
    private final boolean hasChest;

    public ItemBoat(boolean flag, EntityBoat.EnumBoatType entityboat_enumboattype, Item.Info item_info) {
        super(item_info);
        this.hasChest = flag;
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
                EntityBoat entityboat = this.getBoat(world, movingobjectpositionblock);

                entityboat.setVariant(this.type);
                entityboat.setYRot(entityhuman.getYRot());
                if (!world.noCollision(entityboat, entityboat.getBoundingBox())) {
                    return InteractionResultWrapper.fail(itemstack);
                } else {
                    if (!world.isClientSide) {
                        world.addFreshEntity(entityboat);
                        world.gameEvent((Entity) entityhuman, GameEvent.ENTITY_PLACE, movingobjectpositionblock.getLocation());
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

    private EntityBoat getBoat(World world, MovingObjectPosition movingobjectposition) {
        return (EntityBoat) (this.hasChest ? new ChestBoat(world, movingobjectposition.getLocation().x, movingobjectposition.getLocation().y, movingobjectposition.getLocation().z) : new EntityBoat(world, movingobjectposition.getLocation().x, movingobjectposition.getLocation().y, movingobjectposition.getLocation().z));
    }
}
