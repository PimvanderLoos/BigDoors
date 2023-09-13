package net.minecraft.server;

public interface ISteerable {

    boolean P_();

    void a_(Vec3D vec3d);

    float O_();

    default boolean a(EntityInsentient entityinsentient, SaddleStorage saddlestorage, Vec3D vec3d) {
        if (!entityinsentient.isAlive()) {
            return false;
        } else {
            Entity entity = entityinsentient.getPassengers().isEmpty() ? null : (Entity) entityinsentient.getPassengers().get(0);

            if (entityinsentient.isVehicle() && entityinsentient.es() && entity instanceof EntityHuman) {
                entityinsentient.yaw = entity.yaw;
                entityinsentient.lastYaw = entityinsentient.yaw;
                entityinsentient.pitch = entity.pitch * 0.5F;
                entityinsentient.setYawPitch(entityinsentient.yaw, entityinsentient.pitch);
                entityinsentient.aH = entityinsentient.yaw;
                entityinsentient.aJ = entityinsentient.yaw;
                entityinsentient.G = 1.0F;
                entityinsentient.aL = entityinsentient.dM() * 0.1F;
                if (saddlestorage.a && saddlestorage.b++ > saddlestorage.c) {
                    saddlestorage.a = false;
                }

                if (entityinsentient.cr()) {
                    float f = this.O_();

                    if (saddlestorage.a) {
                        f += f * 1.15F * MathHelper.sin((float) saddlestorage.b / (float) saddlestorage.c * 3.1415927F);
                    }

                    entityinsentient.n(f);
                    this.a_(new Vec3D(0.0D, 0.0D, 1.0D));
                    entityinsentient.bb = 0;
                } else {
                    entityinsentient.a((EntityLiving) entityinsentient, false);
                    entityinsentient.setMot(Vec3D.a);
                }

                return true;
            } else {
                entityinsentient.G = 0.5F;
                entityinsentient.aL = 0.02F;
                this.a_(vec3d);
                return false;
            }
        }
    }
}
