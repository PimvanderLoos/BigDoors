package net.minecraft.world.damagesource;

import net.minecraft.world.phys.Vec3D;

public class PointDamageSource extends DamageSource {

    private final Vec3D damageSourcePosition;

    public PointDamageSource(String s, Vec3D vec3d) {
        super(s);
        this.damageSourcePosition = vec3d;
    }

    @Override
    public Vec3D getSourcePosition() {
        return this.damageSourcePosition;
    }
}
