package net.minecraft.server;

public class PathfinderGoalBowShoot<T extends EntityMonster & IRangedEntity> extends PathfinderGoal {

    private final T a;
    private final double b;
    private int c;
    private final float d;
    private int e = -1;
    private int f;
    private boolean g;
    private boolean h;
    private int i = -1;

    public PathfinderGoalBowShoot(T t0, double d0, int i, float f) {
        this.a = t0;
        this.b = d0;
        this.c = i;
        this.d = f * f;
        this.a(3);
    }

    public void b(int i) {
        this.c = i;
    }

    public boolean a() {
        return this.a.getGoalTarget() == null ? false : this.f();
    }

    protected boolean f() {
        return !this.a.getItemInMainHand().isEmpty() && this.a.getItemInMainHand().getItem() == Items.BOW;
    }

    public boolean b() {
        return (this.a() || !this.a.getNavigation().o()) && this.f();
    }

    public void c() {
        super.c();
        ((IRangedEntity) this.a).p(true);
    }

    public void d() {
        super.d();
        ((IRangedEntity) this.a).p(false);
        this.f = 0;
        this.e = -1;
        this.a.cN();
    }

    public void e() {
        EntityLiving entityliving = this.a.getGoalTarget();

        if (entityliving != null) {
            double d0 = this.a.d(entityliving.locX, entityliving.getBoundingBox().b, entityliving.locZ);
            boolean flag = this.a.getEntitySenses().a(entityliving);
            boolean flag1 = this.f > 0;

            if (flag != flag1) {
                this.f = 0;
            }

            if (flag) {
                ++this.f;
            } else {
                --this.f;
            }

            if (d0 <= (double) this.d && this.f >= 20) {
                this.a.getNavigation().p();
                ++this.i;
            } else {
                this.a.getNavigation().a((Entity) entityliving, this.b);
                this.i = -1;
            }

            if (this.i >= 20) {
                if ((double) this.a.getRandom().nextFloat() < 0.3D) {
                    this.g = !this.g;
                }

                if ((double) this.a.getRandom().nextFloat() < 0.3D) {
                    this.h = !this.h;
                }

                this.i = 0;
            }

            if (this.i > -1) {
                if (d0 > (double) (this.d * 0.75F)) {
                    this.h = false;
                } else if (d0 < (double) (this.d * 0.25F)) {
                    this.h = true;
                }

                this.a.getControllerMove().a(this.h ? -0.5F : 0.5F, this.g ? 0.5F : -0.5F);
                this.a.a((Entity) entityliving, 30.0F, 30.0F);
            } else {
                this.a.getControllerLook().a(entityliving, 30.0F, 30.0F);
            }

            if (this.a.isHandRaised()) {
                if (!flag && this.f < -60) {
                    this.a.cN();
                } else if (flag) {
                    int i = this.a.cL();

                    if (i >= 20) {
                        this.a.cN();
                        ((IRangedEntity) this.a).a(entityliving, ItemBow.b(i));
                        this.e = this.c;
                    }
                }
            } else if (--this.e <= 0 && this.f >= -60) {
                this.a.c(EnumHand.MAIN_HAND);
            }

        }
    }
}
