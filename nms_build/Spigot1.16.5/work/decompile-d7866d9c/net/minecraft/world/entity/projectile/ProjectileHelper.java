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

    public static MovingObjectPosition a(Entity entity, Predicate<Entity> predicate) {
        Vec3D vec3d = entity.getMot();
        World world = entity.world;
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
    public static MovingObjectPositionEntity a(World world, Entity entity, Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        double d0 = Double.MAX_VALUE;
        Entity entity1 = null;
        Iterator iterator = world.getEntities(entity, axisalignedbb, predicate).iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            AxisAlignedBB axisalignedbb1 = entity2.getBoundingBox().g(0.30000001192092896D);
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

    public static final void a(Entity entity, float f) {
        Vec3D vec3d = entity.getMot();

        if (vec3d.g() != 0.0D) {
            float f1 = MathHelper.sqrt(Entity.c(vec3d));

            entity.yaw = (float) (MathHelper.d(vec3d.z, vec3d.x) * 57.2957763671875D) + 90.0F;

            for (entity.pitch = (float) (MathHelper.d((double) f1, vec3d.y) * 57.2957763671875D) - 90.0F; entity.pitch - entity.lastPitch < -180.0F; entity.lastPitch -= 360.0F) {
                ;
            }

            while (entity.pitch - entity.lastPitch >= 180.0F) {
                entity.lastPitch += 360.0F;
            }

            while (entity.yaw - entity.lastYaw < -180.0F) {
                entity.lastYaw -= 360.0F;
            }

            while (entity.yaw - entity.lastYaw >= 180.0F) {
                entity.lastYaw += 360.0F;
            }

            entity.pitch = MathHelper.g(f, entity.lastPitch, entity.pitch);
            entity.yaw = MathHelper.g(f, entity.lastYaw, entity.yaw);
        }
    }

    public static EnumHand a(EntityLiving entityliving, Item item) {
        return entityliving.getItemInMainHand().getItem() == item ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    public static EntityArrow a(EntityLiving entityliving, ItemStack itemstack, float f) {
        ItemArrow itemarrow = (ItemArrow) ((ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
        EntityArrow entityarrow = itemarrow.a(entityliving.world, itemstack, entityliving);

        entityarrow.a(entityliving, f);
        if (itemstack.getItem() == Items.TIPPED_ARROW && entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).b(itemstack);
        }

        return entityarrow;
    }
}
