package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalRandomLookaround extends PathfinderGoal {

    private final EntityInsentient mob;
    private double relX;
    private double relZ;
    private int lookTime;

    public PathfinderGoalRandomLookaround(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        return this.mob.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean b() {
        return this.lookTime >= 0;
    }

    @Override
    public void c() {
        double d0 = 6.283185307179586D * this.mob.getRandom().nextDouble();

        this.relX = Math.cos(d0);
        this.relZ = Math.sin(d0);
        this.lookTime = 20 + this.mob.getRandom().nextInt(20);
    }

    @Override
    public void e() {
        --this.lookTime;
        this.mob.getControllerLook().a(this.mob.locX() + this.relX, this.mob.getHeadY(), this.mob.locZ() + this.relZ);
    }
}
