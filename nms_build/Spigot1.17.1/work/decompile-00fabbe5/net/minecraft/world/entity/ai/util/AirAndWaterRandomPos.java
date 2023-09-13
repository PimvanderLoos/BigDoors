package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class AirAndWaterRandomPos {

    public AirAndWaterRandomPos() {}

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, int k, double d0, double d1, double d2) {
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            return a(entitycreature, i, j, k, d0, d1, d2, flag);
        });
    }

    @Nullable
    public static BlockPosition a(EntityCreature entitycreature, int i, int j, int k, double d0, double d1, double d2, boolean flag) {
        BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j, k, d0, d1, d2);

        if (blockposition == null) {
            return null;
        } else {
            BlockPosition blockposition1 = RandomPositionGenerator.a(entitycreature, i, entitycreature.getRandom(), blockposition);

            if (!PathfinderGoalUtil.a(blockposition1, entitycreature) && !PathfinderGoalUtil.a(flag, entitycreature, blockposition1)) {
                blockposition1 = RandomPositionGenerator.a(blockposition1, entitycreature.level.getMaxBuildHeight(), (blockposition2) -> {
                    return PathfinderGoalUtil.c(entitycreature, blockposition2);
                });
                return PathfinderGoalUtil.b(entitycreature, blockposition1) ? null : blockposition1;
            } else {
                return null;
            }
        }
    }
}
