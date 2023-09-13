package net.minecraft.world.entity.ai.behavior;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class RandomSwim extends BehaviorStrollRandomUnconstrained {

    public static final int[][] XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

    public RandomSwim(float f) {
        super(f);
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        return entitycreature.aO();
    }

    @Nullable
    @Override
    protected Vec3D a(EntityCreature entitycreature) {
        Vec3D vec3d = null;
        Vec3D vec3d1 = null;
        int[][] aint = RandomSwim.XY_DISTANCE_TIERS;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int[] aint1 = aint[j];

            if (vec3d == null) {
                vec3d1 = BehaviorUtil.a(entitycreature, aint1[0], aint1[1]);
            } else {
                vec3d1 = entitycreature.getPositionVector().e(entitycreature.getPositionVector().a(vec3d).d().d((double) aint1[0], (double) aint1[1], (double) aint1[0]));
            }

            if (vec3d1 == null || entitycreature.level.getFluid(new BlockPosition(vec3d1)).isEmpty()) {
                return vec3d;
            }

            vec3d = vec3d1;
        }

        return vec3d1;
    }
}
