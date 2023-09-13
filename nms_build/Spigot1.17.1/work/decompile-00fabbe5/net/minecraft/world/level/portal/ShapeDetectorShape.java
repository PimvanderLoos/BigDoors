package net.minecraft.world.level.portal;

import net.minecraft.world.phys.Vec3D;

public class ShapeDetectorShape {

    public final Vec3D pos;
    public final Vec3D speed;
    public final float yRot;
    public final float xRot;

    public ShapeDetectorShape(Vec3D vec3d, Vec3D vec3d1, float f, float f1) {
        this.pos = vec3d;
        this.speed = vec3d1;
        this.yRot = f;
        this.xRot = f1;
    }
}
