package net.minecraft.server;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

    protected EntityCreature a;
    protected int b;
    private final double d;
    private final boolean e;
    private PathEntity f;
    private int g;
    private double h;
    private double i;
    private double j;
    protected final int c = 20;

    public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
        this.a = entitycreature;
        this.d = d0;
        this.e = flag;
        this.a(3);
    }

    public boolean a() {
        EntityLiving entityliving = this.a.getGoalTarget();

        if (entityliving == null) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else {
            this.f = this.a.getNavigation().a((Entity) entityliving);
            return this.f != null ? true : this.a(entityliving) >= this.a.d(entityliving.locX, entityliving.getBoundingBox().minY, entityliving.locZ);
        }
    }

    public boolean b() {
        EntityLiving entityliving = this.a.getGoalTarget();

        return entityliving == null ? false : (!entityliving.isAlive() ? false : (!this.e ? !this.a.getNavigation().p() : (!this.a.f(new BlockPosition(entityliving)) ? false : !(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).isSpectator() && !((EntityHuman) entityliving).u())));
    }

    public void c() {
        this.a.getNavigation().a(this.f, this.d);
        this.g = 0;
    }

    public void d() {
        EntityLiving entityliving = this.a.getGoalTarget();

        if (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).u())) {
            this.a.setGoalTarget((EntityLiving) null);
        }

        this.a.getNavigation().q();
    }

    public void e() {
        EntityLiving entityliving = this.a.getGoalTarget();

        this.a.getControllerLook().a(entityliving, 30.0F, 30.0F);
        double d0 = this.a.d(entityliving.locX, entityliving.getBoundingBox().minY, entityliving.locZ);

        --this.g;
        if ((this.e || this.a.getEntitySenses().a(entityliving)) && this.g <= 0 && (this.h == 0.0D && this.i == 0.0D && this.j == 0.0D || entityliving.d(this.h, this.i, this.j) >= 1.0D || this.a.getRandom().nextFloat() < 0.05F)) {
            this.h = entityliving.locX;
            this.i = entityliving.getBoundingBox().minY;
            this.j = entityliving.locZ;
            this.g = 4 + this.a.getRandom().nextInt(7);
            if (d0 > 1024.0D) {
                this.g += 10;
            } else if (d0 > 256.0D) {
                this.g += 5;
            }

            if (!this.a.getNavigation().a((Entity) entityliving, this.d)) {
                this.g += 15;
            }
        }

        this.b = Math.max(this.b - 1, 0);
        this.a(entityliving, d0);
    }

    protected void a(EntityLiving entityliving, double d0) {
        double d1 = this.a(entityliving);

        if (d0 <= d1 && this.b <= 0) {
            this.b = 20;
            this.a.a(EnumHand.MAIN_HAND);
            this.a.B(entityliving);
        }

    }

    protected double a(EntityLiving entityliving) {
        return (double) (this.a.width * 2.0F * this.a.width * 2.0F + entityliving.width);
    }
}
