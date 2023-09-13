package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTameableAnimal;

public class PathfinderGoalSit extends PathfinderGoal {

    private final EntityTameableAnimal mob;

    public PathfinderGoalSit(EntityTameableAnimal entitytameableanimal) {
        this.mob = entitytameableanimal;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean b() {
        return this.mob.isWillSit();
    }

    @Override
    public boolean a() {
        if (!this.mob.isTamed()) {
            return false;
        } else if (this.mob.aO()) {
            return false;
        } else if (!this.mob.isOnGround()) {
            return false;
        } else {
            EntityLiving entityliving = this.mob.getOwner();

            return entityliving == null ? true : (this.mob.f((Entity) entityliving) < 144.0D && entityliving.getLastDamager() != null ? false : this.mob.isWillSit());
        }
    }

    @Override
    public void c() {
        this.mob.getNavigation().o();
        this.mob.setSitting(true);
    }

    @Override
    public void d() {
        this.mob.setSitting(false);
    }
}
