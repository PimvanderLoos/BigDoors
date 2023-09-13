package net.minecraft.world.entity.ai.util;

import java.util.Objects;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class LandRandomPos {

    public LandRandomPos() {}

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        Objects.requireNonNull(entitycreature);
        return a(entitycreature, i, j, entitycreature::f);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, ToDoubleFunction<BlockPosition> todoublefunction) {
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(() -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j);
            BlockPosition blockposition1 = a(entitycreature, i, flag, blockposition);

            return blockposition1 == null ? null : a(entitycreature, blockposition1);
        }, todoublefunction);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX(), entitycreature.locY(), entitycreature.locZ());
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return a(entitycreature, i, j, vec3d1, flag);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = entitycreature.getPositionVector().d(vec3d);
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return a(entitycreature, i, j, vec3d1, flag);
    }

    @Nullable
    private static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d, boolean flag) {
        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j, 0, vec3d.x, vec3d.z, 1.5707963705062866D);

            if (blockposition == null) {
                return null;
            } else {
                BlockPosition blockposition1 = a(entitycreature, i, flag, blockposition);

                return blockposition1 == null ? null : a(entitycreature, blockposition1);
            }
        });
    }

    @Nullable
    public static BlockPosition a(EntityCreature entitycreature, BlockPosition blockposition) {
        blockposition = RandomPositionGenerator.a(blockposition, entitycreature.level.getMaxBuildHeight(), (blockposition1) -> {
            return PathfinderGoalUtil.c(entitycreature, blockposition1);
        });
        return !PathfinderGoalUtil.a(entitycreature, blockposition) && !PathfinderGoalUtil.b(entitycreature, blockposition) ? blockposition : null;
    }

    @Nullable
    public static BlockPosition a(EntityCreature entitycreature, int i, boolean flag, BlockPosition blockposition) {
        BlockPosition blockposition1 = RandomPositionGenerator.a(entitycreature, i, entitycreature.getRandom(), blockposition);

        return !PathfinderGoalUtil.a(blockposition1, entitycreature) && !PathfinderGoalUtil.a(flag, entitycreature, blockposition1) && !PathfinderGoalUtil.a(entitycreature.getNavigation(), blockposition1) ? blockposition1 : null;
    }
}
