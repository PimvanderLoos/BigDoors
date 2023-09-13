package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class HoverRandomPos {

    public HoverRandomPos() {}

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, double d0, double d1, float f, int k, int l) {
        boolean flag = PathfinderGoalUtil.a(entitycreature, i);

        return RandomPositionGenerator.a(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.a(entitycreature.getRandom(), i, j, 0, d0, d1, (double) f);

            if (blockposition == null) {
                return null;
            } else {
                BlockPosition blockposition1 = LandRandomPos.a(entitycreature, i, flag, blockposition);

                if (blockposition1 == null) {
                    return null;
                } else {
                    blockposition1 = RandomPositionGenerator.a(blockposition1, entitycreature.getRandom().nextInt(k - l + 1) + l, entitycreature.level.getMaxBuildHeight(), (blockposition2) -> {
                        return PathfinderGoalUtil.c(entitycreature, blockposition2);
                    });
                    return !PathfinderGoalUtil.a(entitycreature, blockposition1) && !PathfinderGoalUtil.b(entitycreature, blockposition1) ? blockposition1 : null;
                }
            }
        });
    }
}
