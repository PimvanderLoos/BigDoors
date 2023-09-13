package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class AirRandomPos {

    public AirRandomPos() {}

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, int k, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX(), entitycreature.locY(), entitycreature.locZ());
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = AirAndWaterRandomPos.a(entitycreature, i, j, k, vec3d1.x, vec3d1.z, d0, flag);

            return blockposition != null && !PathfinderGoalUtil.a(entitycreature, blockposition) ? blockposition : null;
        });
    }
}
