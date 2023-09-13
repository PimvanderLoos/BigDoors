package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemBow;
import net.minecraft.world.item.Items;

public class PathfinderGoalBowShoot<T extends EntityMonster & IRangedEntity> extends PathfinderGoal {

    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public PathfinderGoalBowShoot(T t0, double d0, int i, float f) {
        this.mob = t0;
        this.speedModifier = d0;
        this.attackIntervalMin = i;
        this.attackRadiusSqr = f * f;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    public void a(int i) {
        this.attackIntervalMin = i;
    }

    @Override
    public boolean a() {
        return this.mob.getGoalTarget() == null ? false : this.g();
    }

    protected boolean g() {
        return this.mob.a(Items.BOW);
    }

    @Override
    public boolean b() {
        return (this.a() || !this.mob.getNavigation().m()) && this.g();
    }

    @Override
    public void c() {
        super.c();
        this.mob.setAggressive(true);
    }

    @Override
    public void d() {
        super.d();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.clearActiveItem();
    }

    @Override
    public void e() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        if (entityliving != null) {
            double d0 = this.mob.h(entityliving.locX(), entityliving.locY(), entityliving.locZ());
            boolean flag = this.mob.getEntitySenses().a(entityliving);
            boolean flag1 = this.seeTime > 0;

            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (d0 <= (double) this.attackRadiusSqr && this.seeTime >= 20) {
                this.mob.getNavigation().o();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().a((Entity) entityliving, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.mob.getControllerMove().a(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.mob.a((Entity) entityliving, 30.0F, 30.0F);
            } else {
                this.mob.getControllerLook().a(entityliving, 30.0F, 30.0F);
            }

            if (this.mob.isHandRaised()) {
                if (!flag && this.seeTime < -60) {
                    this.mob.clearActiveItem();
                } else if (flag) {
                    int i = this.mob.eJ();

                    if (i >= 20) {
                        this.mob.clearActiveItem();
                        ((IRangedEntity) this.mob).a(entityliving, ItemBow.a(i));
                        this.attackTime = this.attackIntervalMin;
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                this.mob.c(ProjectileHelper.a((EntityLiving) this.mob, Items.BOW));
            }

        }
    }
}
