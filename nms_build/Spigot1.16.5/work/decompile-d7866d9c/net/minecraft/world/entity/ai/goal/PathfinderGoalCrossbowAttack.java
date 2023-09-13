package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PathfinderGoalCrossbowAttack<T extends EntityMonster & IRangedEntity & ICrossbow> extends PathfinderGoal {

    public static final IntRange a = new IntRange(20, 40);
    private final T b;
    private PathfinderGoalCrossbowAttack.State c;
    private final double d;
    private final float e;
    private int f;
    private int g;
    private int h;

    public PathfinderGoalCrossbowAttack(T t0, double d0, float f) {
        this.c = PathfinderGoalCrossbowAttack.State.UNCHARGED;
        this.b = t0;
        this.d = d0;
        this.e = f * f;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        return this.h() && this.g();
    }

    private boolean g() {
        return this.b.a(Items.CROSSBOW);
    }

    @Override
    public boolean b() {
        return this.h() && (this.a() || !this.b.getNavigation().m()) && this.g();
    }

    private boolean h() {
        return this.b.getGoalTarget() != null && this.b.getGoalTarget().isAlive();
    }

    @Override
    public void d() {
        super.d();
        this.b.setAggressive(false);
        this.b.setGoalTarget((EntityLiving) null);
        this.f = 0;
        if (this.b.isHandRaised()) {
            this.b.clearActiveItem();
            ((ICrossbow) this.b).b(false);
            ItemCrossbow.a(this.b.getActiveItem(), false);
        }

    }

    @Override
    public void e() {
        EntityLiving entityliving = this.b.getGoalTarget();

        if (entityliving != null) {
            boolean flag = this.b.getEntitySenses().a(entityliving);
            boolean flag1 = this.f > 0;

            if (flag != flag1) {
                this.f = 0;
            }

            if (flag) {
                ++this.f;
            } else {
                --this.f;
            }

            double d0 = this.b.h((Entity) entityliving);
            boolean flag2 = (d0 > (double) this.e || this.f < 5) && this.g == 0;

            if (flag2) {
                --this.h;
                if (this.h <= 0) {
                    this.b.getNavigation().a((Entity) entityliving, this.j() ? this.d : this.d * 0.5D);
                    this.h = PathfinderGoalCrossbowAttack.a.a(this.b.getRandom());
                }
            } else {
                this.h = 0;
                this.b.getNavigation().o();
            }

            this.b.getControllerLook().a(entityliving, 30.0F, 30.0F);
            if (this.c == PathfinderGoalCrossbowAttack.State.UNCHARGED) {
                if (!flag2) {
                    this.b.c(ProjectileHelper.a((EntityLiving) this.b, Items.CROSSBOW));
                    this.c = PathfinderGoalCrossbowAttack.State.CHARGING;
                    ((ICrossbow) this.b).b(true);
                }
            } else if (this.c == PathfinderGoalCrossbowAttack.State.CHARGING) {
                if (!this.b.isHandRaised()) {
                    this.c = PathfinderGoalCrossbowAttack.State.UNCHARGED;
                }

                int i = this.b.ea();
                ItemStack itemstack = this.b.getActiveItem();

                if (i >= ItemCrossbow.g(itemstack)) {
                    this.b.releaseActiveItem();
                    this.c = PathfinderGoalCrossbowAttack.State.CHARGED;
                    this.g = 20 + this.b.getRandom().nextInt(20);
                    ((ICrossbow) this.b).b(false);
                }
            } else if (this.c == PathfinderGoalCrossbowAttack.State.CHARGED) {
                --this.g;
                if (this.g == 0) {
                    this.c = PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK;
                }
            } else if (this.c == PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK && flag) {
                ((IRangedEntity) this.b).a(entityliving, 1.0F);
                ItemStack itemstack1 = this.b.b(ProjectileHelper.a((EntityLiving) this.b, Items.CROSSBOW));

                ItemCrossbow.a(itemstack1, false);
                this.c = PathfinderGoalCrossbowAttack.State.UNCHARGED;
            }

        }
    }

    private boolean j() {
        return this.c == PathfinderGoalCrossbowAttack.State.UNCHARGED;
    }

    static enum State {

        UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;

        private State() {}
    }
}
