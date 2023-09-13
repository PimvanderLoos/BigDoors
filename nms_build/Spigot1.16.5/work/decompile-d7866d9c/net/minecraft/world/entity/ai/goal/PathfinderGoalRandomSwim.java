package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomSwim extends PathfinderGoalRandomStroll {

    public PathfinderGoalRandomSwim(EntityCreature entitycreature, double d0, int i) {
        super(entitycreature, d0, i);
    }

    @Nullable
    @Override
    protected Vec3D g() {
        Vec3D vec3d = RandomPositionGenerator.a(this.a, 10, 7);

        for (int i = 0; vec3d != null && !this.a.world.getType(new BlockPosition(vec3d)).a((IBlockAccess) this.a.world, new BlockPosition(vec3d), PathMode.WATER) && i++ < 10; vec3d = RandomPositionGenerator.a(this.a, 10, 7)) {
            ;
        }

        return vec3d;
    }
}
