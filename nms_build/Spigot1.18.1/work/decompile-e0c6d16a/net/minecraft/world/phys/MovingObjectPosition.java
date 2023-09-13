package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class MovingObjectPosition {

    protected final Vec3D location;

    protected MovingObjectPosition(Vec3D vec3d) {
        this.location = vec3d;
    }

    public double distanceTo(Entity entity) {
        double d0 = this.location.x - entity.getX();
        double d1 = this.location.y - entity.getY();
        double d2 = this.location.z - entity.getZ();

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract MovingObjectPosition.EnumMovingObjectType getType();

    public Vec3D getLocation() {
        return this.location;
    }

    public static enum EnumMovingObjectType {

        MISS, BLOCK, ENTITY;

        private EnumMovingObjectType() {}
    }
}
