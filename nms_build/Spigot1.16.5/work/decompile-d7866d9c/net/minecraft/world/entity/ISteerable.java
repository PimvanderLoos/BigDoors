package net.minecraft.world.entity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public interface ISteerable {

    boolean O_();

    void a_(Vec3D vec3d);

    float N_();

    default boolean a(EntityInsentient entityinsentient, SaddleStorage saddlestorage, Vec3D vec3d) {
        if (!entityinsentient.isAlive()) {
            return false;
        } else {
            Entity entity = entityinsentient.getPassengers().isEmpty() ? null : (Entity) entityinsentient.getPassengers().get(0);

            if (entityinsentient.isVehicle() && entityinsentient.er() && entity instanceof EntityHuman) {
                entityinsentient.yaw = entity.yaw;
                entityinsentient.lastYaw = entityinsentient.yaw;
                entityinsentient.pitch = entity.pitch * 0.5F;
                entityinsentient.setYawPitch(entityinsentient.yaw, entityinsentient.pitch);
                entityinsentient.aA = entityinsentient.yaw;
                entityinsentient.aC = entityinsentient.yaw;
                entityinsentient.G = 1.0F;
                entityinsentient.aE = entityinsentient.dN() * 0.1F;
                if (saddlestorage.boosting && saddlestorage.currentBoostTicks++ > saddlestorage.boostTicks) {
                    saddlestorage.boosting = false;
                }

                if (entityinsentient.cs()) {
                    float f = this.N_();

                    if (saddlestorage.boosting) {
                        f += f * 1.15F * MathHelper.sin((float) saddlestorage.currentBoostTicks / (float) saddlestorage.boostTicks * 3.1415927F);
                    }

                    entityinsentient.q(f);
                    this.a_(new Vec3D(0.0D, 0.0D, 1.0D));
                    entityinsentient.aU = 0;
                } else {
                    entityinsentient.a((EntityLiving) entityinsentient, false);
                    entityinsentient.setMot(Vec3D.ORIGIN);
                }

                return true;
            } else {
                entityinsentient.G = 0.5F;
                entityinsentient.aE = 0.02F;
                this.a_(vec3d);
                return false;
            }
        }
    }
}
