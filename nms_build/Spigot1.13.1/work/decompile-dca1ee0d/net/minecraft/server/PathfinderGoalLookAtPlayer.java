package net.minecraft.server;

public class PathfinderGoalLookAtPlayer extends PathfinderGoal {

    protected EntityInsentient a;
    protected Entity b;
    protected float c;
    private int e;
    private final float f;
    protected Class<? extends Entity> d;

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends Entity> oclass, float f) {
        this(entityinsentient, oclass, f, 0.02F);
    }

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends Entity> oclass, float f, float f1) {
        this.a = entityinsentient;
        this.d = oclass;
        this.c = f;
        this.f = f1;
        this.a(2);
    }

    public boolean a() {
        if (this.a.getRandom().nextFloat() >= this.f) {
            return false;
        } else {
            if (this.a.getGoalTarget() != null) {
                this.b = this.a.getGoalTarget();
            }

            if (this.d == EntityHuman.class) {
                this.b = this.a.world.a(this.a.locX, this.a.locY, this.a.locZ, (double) this.c, IEntitySelector.f.and(IEntitySelector.b(this.a)));
            } else {
                this.b = this.a.world.a(this.d, this.a.getBoundingBox().grow((double) this.c, 3.0D, (double) this.c), (Entity) this.a);
            }

            return this.b != null;
        }
    }

    public boolean b() {
        return !this.b.isAlive() ? false : (this.a.h(this.b) > (double) (this.c * this.c) ? false : this.e > 0);
    }

    public void c() {
        this.e = 40 + this.a.getRandom().nextInt(40);
    }

    public void d() {
        this.b = null;
    }

    public void e() {
        this.a.getControllerLook().a(this.b.locX, this.b.locY + (double) this.b.getHeadHeight(), this.b.locZ, (float) this.a.L(), (float) this.a.K());
        --this.e;
    }
}
