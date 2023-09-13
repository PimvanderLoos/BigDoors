package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalRandomLookaround extends PathfinderGoal {

    private final EntityInsentient a;
    private double b;
    private double c;
    private int d;

    public PathfinderGoalRandomLookaround(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        return this.a.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean b() {
        return this.d >= 0;
    }

    @Override
    public void c() {
        double d0 = 6.283185307179586D * this.a.getRandom().nextDouble();

        this.b = Math.cos(d0);
        this.c = Math.sin(d0);
        this.d = 20 + this.a.getRandom().nextInt(20);
    }

    @Override
    public void e() {
        --this.d;
        this.a.getControllerLook().a(this.a.locX() + this.b, this.a.getHeadY(), this.a.locZ() + this.c);
    }
}
