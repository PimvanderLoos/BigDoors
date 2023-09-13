package net.minecraft.server;

import java.util.EnumSet;

public abstract class PathfinderGoalWaterJumpAbstract extends PathfinderGoal {

    public PathfinderGoalWaterJumpAbstract() {
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.JUMP));
    }
}
