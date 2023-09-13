package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.IBlockAccess;

public class PathfinderGoalOcelotAttack extends PathfinderGoal {

    private final IBlockAccess level;
    private final EntityInsentient mob;
    private EntityLiving target;
    private int attackTime;

    public PathfinderGoalOcelotAttack(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
        this.level = entityinsentient.level;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        if (entityliving == null) {
            return false;
        } else {
            this.target = entityliving;
            return true;
        }
    }

    @Override
    public boolean b() {
        return !this.target.isAlive() ? false : (this.mob.f((Entity) this.target) > 225.0D ? false : !this.mob.getNavigation().m() || this.a());
    }

    @Override
    public void d() {
        this.target = null;
        this.mob.getNavigation().o();
    }

    @Override
    public void e() {
        this.mob.getControllerLook().a(this.target, 30.0F, 30.0F);
        double d0 = (double) (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
        double d1 = this.mob.h(this.target.locX(), this.target.locY(), this.target.locZ());
        double d2 = 0.8D;

        if (d1 > d0 && d1 < 16.0D) {
            d2 = 1.33D;
        } else if (d1 < 225.0D) {
            d2 = 0.6D;
        }

        this.mob.getNavigation().a((Entity) this.target, d2);
        this.attackTime = Math.max(this.attackTime - 1, 0);
        if (d1 <= d0) {
            if (this.attackTime <= 0) {
                this.attackTime = 20;
                this.mob.attackEntity(this.target);
            }
        }
    }
}
