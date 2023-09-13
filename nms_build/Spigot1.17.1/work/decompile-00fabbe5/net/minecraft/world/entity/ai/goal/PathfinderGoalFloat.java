package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalFloat extends PathfinderGoal {

    private final EntityInsentient mob;

    public PathfinderGoalFloat(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP));
        entityinsentient.getNavigation().d(true);
    }

    @Override
    public boolean a() {
        return this.mob.isInWater() && this.mob.b((Tag) TagsFluid.WATER) > this.mob.cN() || this.mob.aX();
    }

    @Override
    public void e() {
        if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getControllerJump().jump();
        }

    }
}
