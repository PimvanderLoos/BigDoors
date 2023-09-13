package net.minecraft.server;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

    World a;
    protected EntityCreature b;
    protected int c;
    double d;
    boolean e;
    PathEntity f;
    private int h;
    private double i;
    private double j;
    private double k;
    protected final int g = 20;

    public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
        this.b = entitycreature;
        this.a = entitycreature.world;
        this.d = d0;
        this.e = flag;
        this.a(3);
    }

    public boolean a() {
        EntityLiving entityliving = this.b.getGoalTarget();

        if (entityliving == null) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else {
            this.f = this.b.getNavigation().a((Entity) entityliving);
            return this.f != null ? true : this.a(entityliving) >= this.b.d(entityliving.locX, entityliving.getBoundingBox().b, entityliving.locZ);
        }
    }

    public boolean b() {
        EntityLiving entityliving = this.b.getGoalTarget();

        return entityliving == null ? false : (!entityliving.isAlive() ? false : (!this.e ? !this.b.getNavigation().o() : (!this.b.f(new BlockPosition(entityliving)) ? false : !(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).isSpectator() && !((EntityHuman) entityliving).z())));
    }

    public void c() {
        this.b.getNavigation().a(this.f, this.d);
        this.h = 0;
    }

    public void d() {
        EntityLiving entityliving = this.b.getGoalTarget();

        if (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).z())) {
            this.b.setGoalTarget((EntityLiving) null);
        }

        this.b.getNavigation().p();
    }

    public void e() {
        EntityLiving entityliving = this.b.getGoalTarget();

        this.b.getControllerLook().a(entityliving, 30.0F, 30.0F);
        double d0 = this.b.d(entityliving.locX, entityliving.getBoundingBox().b, entityliving.locZ);

        --this.h;
        if ((this.e || this.b.getEntitySenses().a(entityliving)) && this.h <= 0 && (this.i == 0.0D && this.j == 0.0D && this.k == 0.0D || entityliving.d(this.i, this.j, this.k) >= 1.0D || this.b.getRandom().nextFloat() < 0.05F)) {
            this.i = entityliving.locX;
            this.j = entityliving.getBoundingBox().b;
            this.k = entityliving.locZ;
            this.h = 4 + this.b.getRandom().nextInt(7);
            if (d0 > 1024.0D) {
                this.h += 10;
            } else if (d0 > 256.0D) {
                this.h += 5;
            }

            if (!this.b.getNavigation().a((Entity) entityliving, this.d)) {
                this.h += 15;
            }
        }

        this.c = Math.max(this.c - 1, 0);
        this.a(entityliving, d0);
    }

    protected void a(EntityLiving entityliving, double d0) {
        double d1 = this.a(entityliving);

        if (d0 <= d1 && this.c <= 0) {
            this.c = 20;
            this.b.a(EnumHand.MAIN_HAND);
            this.b.B(entityliving);
        }

    }

    protected double a(EntityLiving entityliving) {
        return (double) (this.b.width * 2.0F * this.b.width * 2.0F + entityliving.width);
    }
}
