package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class MovingObjectPositionEntity extends MovingObjectPosition {

    private final Entity entity;

    public MovingObjectPositionEntity(Entity entity) {
        this(entity, entity.position());
    }

    public MovingObjectPositionEntity(Entity entity, Vec3D vec3d) {
        super(vec3d);
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return MovingObjectPosition.EnumMovingObjectType.ENTITY;
    }
}
