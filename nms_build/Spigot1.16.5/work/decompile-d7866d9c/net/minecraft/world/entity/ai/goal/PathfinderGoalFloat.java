package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalFloat extends PathfinderGoal {

    private final EntityInsentient a;

    public PathfinderGoalFloat(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP));
        entityinsentient.getNavigation().d(true);
    }

    @Override
    public boolean a() {
        return this.a.isInWater() && this.a.b((Tag) TagsFluid.WATER) > this.a.cx() || this.a.aQ();
    }

    @Override
    public void e() {
        if (this.a.getRandom().nextFloat() < 0.8F) {
            this.a.getControllerJump().jump();
        }

    }
}
