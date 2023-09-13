package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomStrollLand extends PathfinderGoalRandomStroll {

    protected final float h;

    public PathfinderGoalRandomStrollLand(EntityCreature entitycreature, double d0) {
        this(entitycreature, d0, 0.001F);
    }

    public PathfinderGoalRandomStrollLand(EntityCreature entitycreature, double d0, float f) {
        super(entitycreature, d0);
        this.h = f;
    }

    @Nullable
    @Override
    protected Vec3D g() {
        if (this.a.aH()) {
            Vec3D vec3d = RandomPositionGenerator.b(this.a, 15, 7);

            return vec3d == null ? super.g() : vec3d;
        } else {
            return this.a.getRandom().nextFloat() >= this.h ? RandomPositionGenerator.b(this.a, 10, 7) : super.g();
        }
    }
}
