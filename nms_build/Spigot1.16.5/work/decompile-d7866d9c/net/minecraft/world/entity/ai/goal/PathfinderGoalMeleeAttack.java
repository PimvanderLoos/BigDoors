package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

    protected final EntityCreature a;
    private final double b;
    private final boolean c;
    private PathEntity d;
    private double e;
    private double f;
    private double g;
    private int h;
    private int i;
    private final int j = 20;
    private long k;

    public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
        this.a = entitycreature;
        this.b = d0;
        this.c = flag;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        long i = this.a.world.getTime();

        if (i - this.k < 20L) {
            return false;
        } else {
            this.k = i;
            EntityLiving entityliving = this.a.getGoalTarget();

            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else {
                this.d = this.a.getNavigation().a((Entity) entityliving, 0);
                return this.d != null ? true : this.a(entityliving) >= this.a.h(entityliving.locX(), entityliving.locY(), entityliving.locZ());
            }
        }
    }

    @Override
    public boolean b() {
        EntityLiving entityliving = this.a.getGoalTarget();

        return entityliving == null ? false : (!entityliving.isAlive() ? false : (!this.c ? !this.a.getNavigation().m() : (!this.a.a(entityliving.getChunkCoordinates()) ? false : !(entityliving instanceof EntityHuman) || !entityliving.isSpectator() && !((EntityHuman) entityliving).isCreative())));
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.d, this.b);
        this.a.setAggressive(true);
        this.h = 0;
        this.i = 0;
    }

    @Override
    public void d() {
        EntityLiving entityliving = this.a.getGoalTarget();

        if (!IEntitySelector.e.test(entityliving)) {
            this.a.setGoalTarget((EntityLiving) null);
        }

        this.a.setAggressive(false);
        this.a.getNavigation().o();
    }

    @Override
    public void e() {
        EntityLiving entityliving = this.a.getGoalTarget();

        this.a.getControllerLook().a(entityliving, 30.0F, 30.0F);
        double d0 = this.a.h(entityliving.locX(), entityliving.locY(), entityliving.locZ());

        this.h = Math.max(this.h - 1, 0);
        if ((this.c || this.a.getEntitySenses().a(entityliving)) && this.h <= 0 && (this.e == 0.0D && this.f == 0.0D && this.g == 0.0D || entityliving.h(this.e, this.f, this.g) >= 1.0D || this.a.getRandom().nextFloat() < 0.05F)) {
            this.e = entityliving.locX();
            this.f = entityliving.locY();
            this.g = entityliving.locZ();
            this.h = 4 + this.a.getRandom().nextInt(7);
            if (d0 > 1024.0D) {
                this.h += 10;
            } else if (d0 > 256.0D) {
                this.h += 5;
            }

            if (!this.a.getNavigation().a((Entity) entityliving, this.b)) {
                this.h += 15;
            }
        }

        this.i = Math.max(this.i - 1, 0);
        this.a(entityliving, d0);
    }

    protected void a(EntityLiving entityliving, double d0) {
        double d1 = this.a(entityliving);

        if (d0 <= d1 && this.i <= 0) {
            this.g();
            this.a.swingHand(EnumHand.MAIN_HAND);
            this.a.attackEntity(entityliving);
        }

    }

    protected void g() {
        this.i = 20;
    }

    protected boolean h() {
        return this.i <= 0;
    }

    protected int j() {
        return this.i;
    }

    protected int k() {
        return 20;
    }

    protected double a(EntityLiving entityliving) {
        return (double) (this.a.getWidth() * 2.0F * this.a.getWidth() * 2.0F + entityliving.getWidth());
    }
}
