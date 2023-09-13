package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;

public class PathfinderGoalInteract extends PathfinderGoalLookAtPlayer {

    public PathfinderGoalInteract(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f, float f1) {
        super(entityinsentient, oclass, f, f1);
        this.a(EnumSet.of(PathfinderGoal.Type.LOOK, PathfinderGoal.Type.MOVE));
    }
}
