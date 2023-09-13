package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public final class ProjectileHelper {

    public ProjectileHelper() {}

    public static MovingObjectPosition a(Entity entity, Predicate<Entity> predicate) {
        Vec3D vec3d = entity.getMot();
        World world = entity.level;
        Vec3D vec3d1 = entity.getPositionVector();
        Vec3D vec3d2 = vec3d1.e(vec3d);
        Object object = world.rayTrace(new RayTrace(vec3d1, vec3d2, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, entity));

        if (((MovingObjectPosition) object).getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
            vec3d2 = ((MovingObjectPosition) object).getPos();
        }

        MovingObjectPositionEntity movingobjectpositionentity = a(world, entity, vec3d1, vec3d2, entity.getBoundingBox().b(entity.getMot()).g(1.0D), predicate);

        if (movingobjectpositionentity != null) {
            object = movingobjectpositionentity;
        }

        return (MovingObjectPosition) object;
    }

    @Nullable
    public static MovingObjectPositionEntity a(Entity entity, Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate, double d0) {
        World world = entity.level;
        double d1 = d0;
        Entity entity1 = null;
        Vec3D vec3d2 = null;
        Iterator iterator = world.getEntities(entity, axisalignedbb, predicate).iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            AxisAlignedBB axisalignedbb1 = entity2.getBoundingBox().g((double) entity2.bp());
            Optional<Vec3D> optional = axisalignedbb1.b(vec3d, vec3d1);

            if (axisalignedbb1.d(vec3d)) {
                if (d1 >= 0.0D) {
                    entity1 = entity2;
                    vec3d2 = (Vec3D) optional.orElse(vec3d);
                    d1 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3D vec3d3 = (Vec3D) optional.get();
                double d2 = vec3d.distanceSquared(vec3d3);

                if (d2 < d1 || d1 == 0.0D) {
                    if (entity2.getRootVehicle() == entity.getRootVehicle()) {
                        if (d1 == 0.0D) {
                            entity1 = entity2;
                            vec3d2 = vec3d3;
                        }
                    } else {
                        entity1 = entity2;
                        vec3d2 = vec3d3;
                        d1 = d2;
                    }
                }
            }
        }

        if (entity1 == null) {
            return null;
        } else {
            return new MovingObjectPositionEntity(entity1, vec3d2);
        }
    }

    @Nullable
    public static MovingObjectPositionEntity a(World world, Entity entity, Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return a(world, entity, vec3d, vec3d1, axisalignedbb, predicate, 0.3F);
    }

    @Nullable
    public static MovingObjectPositionEntity a(World world, Entity entity, Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate, float f) {
        double d0 = Double.MAX_VALUE;
        Entity entity1 = null;
        Iterator iterator = world.getEntities(entity, axisalignedbb, predicate).iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            AxisAlignedBB axisalignedbb1 = entity2.getBoundingBox().g((double) f);
            Optional<Vec3D> optional = axisalignedbb1.b(vec3d, vec3d1);

            if (optional.isPresent()) {
                double d1 = vec3d.distanceSquared((Vec3D) optional.get());

                if (d1 < d0) {
                    entity1 = entity2;
                    d0 = d1;
                }
            }
        }

        if (entity1 == null) {
            return null;
        } else {
            return new MovingObjectPositionEntity(entity1);
        }
    }

    public static void a(Entity entity, float f) {
        Vec3D vec3d = entity.getMot();

        if (vec3d.g() != 0.0D) {
            double d0 = vec3d.h();

            entity.setYRot((float) (MathHelper.d(vec3d.z, vec3d.x) * 57.2957763671875D) + 90.0F);
            entity.setXRot((float) (MathHelper.d(d0, vec3d.y) * 57.2957763671875D) - 90.0F);

            while (entity.getXRot() - entity.xRotO < -180.0F) {
                entity.xRotO -= 360.0F;
            }

            while (entity.getXRot() - entity.xRotO >= 180.0F) {
                entity.xRotO += 360.0F;
            }

            while (entity.getYRot() - entity.yRotO < -180.0F) {
                entity.yRotO -= 360.0F;
            }

            while (entity.getYRot() - entity.yRotO >= 180.0F) {
                entity.yRotO += 360.0F;
            }

            entity.setXRot(MathHelper.h(f, entity.xRotO, entity.getXRot()));
            entity.setYRot(MathHelper.h(f, entity.yRotO, entity.getYRot()));
        }
    }

    public static EnumHand a(EntityLiving entityliving, Item item) {
        return entityliving.getItemInMainHand().a(item) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    public static EntityArrow a(EntityLiving entityliving, ItemStack itemstack, float f) {
        ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
        EntityArrow entityarrow = itemarrow.a(entityliving.level, itemstack, entityliving);

        entityarrow.a(entityliving, f);
        if (itemstack.a(Items.TIPPED_ARROW) && entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).a(itemstack);
        }

        return entityarrow;
    }
}
