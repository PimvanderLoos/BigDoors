package net.minecraft.server;

import java.util.List;

public final class ProjectileHelper {

    public static MovingObjectPosition a(Entity entity, boolean flag, boolean flag1, Entity entity1) {
        double d0 = entity.locX;
        double d1 = entity.locY;
        double d2 = entity.locZ;
        double d3 = entity.motX;
        double d4 = entity.motY;
        double d5 = entity.motZ;
        World world = entity.world;
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        Vec3D vec3d1 = new Vec3D(d0 + d3, d1 + d4, d2 + d5);
        MovingObjectPosition movingobjectposition = world.rayTrace(vec3d, vec3d1, false, true, false);

        if (flag) {
            if (movingobjectposition != null) {
                vec3d1 = new Vec3D(movingobjectposition.pos.x, movingobjectposition.pos.y, movingobjectposition.pos.z);
            }

            Entity entity2 = null;
            List list = world.getEntities(entity, entity.getBoundingBox().b(d3, d4, d5).g(1.0D));
            double d6 = 0.0D;

            for (int i = 0; i < list.size(); ++i) {
                Entity entity3 = (Entity) list.get(i);

                if (entity3.isInteractable() && (flag1 || !entity3.s(entity1)) && !entity3.noclip) {
                    AxisAlignedBB axisalignedbb = entity3.getBoundingBox().g(0.30000001192092896D);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.b(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        double d7 = vec3d.distanceSquared(movingobjectposition1.pos);

                        if (d7 < d6 || d6 == 0.0D) {
                            entity2 = entity3;
                            d6 = d7;
                        }
                    }
                }
            }

            if (entity2 != null) {
                movingobjectposition = new MovingObjectPosition(entity2);
            }
        }

        return movingobjectposition;
    }

    public static final void a(Entity entity, float f) {
        double d0 = entity.motX;
        double d1 = entity.motY;
        double d2 = entity.motZ;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        entity.yaw = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) + 90.0F;

        for (entity.pitch = (float) (MathHelper.c((double) f1, d1) * 57.2957763671875D) - 90.0F; entity.pitch - entity.lastPitch < -180.0F; entity.lastPitch -= 360.0F) {
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

        entity.pitch = entity.lastPitch + (entity.pitch - entity.lastPitch) * f;
        entity.yaw = entity.lastYaw + (entity.yaw - entity.lastYaw) * f;
    }
}
