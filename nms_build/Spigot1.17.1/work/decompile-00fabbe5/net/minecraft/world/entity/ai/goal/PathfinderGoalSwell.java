package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityCreeper;

public class PathfinderGoalSwell extends PathfinderGoal {

    private final EntityCreeper creeper;
    private EntityLiving target;

    public PathfinderGoalSwell(EntityCreeper entitycreeper) {
        this.creeper = entitycreeper;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        EntityLiving entityliving = this.creeper.getGoalTarget();

        return this.creeper.p() > 0 || entityliving != null && this.creeper.f((Entity) entityliving) < 9.0D;
    }

    @Override
    public void c() {
        this.creeper.getNavigation().o();
        this.target = this.creeper.getGoalTarget();
    }

    @Override
    public void d() {
        this.target = null;
    }

    @Override
    public void e() {
        if (this.target == null) {
            this.creeper.a((int) -1);
        } else if (this.creeper.f((Entity) this.target) > 49.0D) {
            this.creeper.a((int) -1);
        } else if (!this.creeper.getEntitySenses().a(this.target)) {
            this.creeper.a((int) -1);
        } else {
            this.creeper.a((int) 1);
        }
    }
}
