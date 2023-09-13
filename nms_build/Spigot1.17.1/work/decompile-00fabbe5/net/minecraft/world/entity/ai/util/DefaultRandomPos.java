package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class DefaultRandomPos {

    public DefaultRandomPos() {}

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j);

            return a(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX(), entitycreature.locY(), entitycreature.locZ());
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j, 0, vec3d1.x, vec3d1.z, d0);

            return blockposition == null ? null : a(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = entitycreature.getPositionVector().d(vec3d);
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j, 0, vec3d1.x, vec3d1.z, 1.5707963705062866D);

            return blockposition == null ? null : a(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    private static BlockPosition a(EntityCreature entitycreature, int i, boolean flag, BlockPosition blockposition) {
        BlockPosition blockposition1 = RandomPositionGenerator.a(entitycreature, i, entitycreature.getRandom(), blockposition);

        return !PathfinderGoalUtil.a(blockposition1, entitycreature) && !PathfinderGoalUtil.a(flag, entitycreature, blockposition1) && !PathfinderGoalUtil.a(entitycreature.getNavigation(), blockposition1) && !PathfinderGoalUtil.b(entitycreature, blockposition1) ? blockposition1 : null;
    }
}
