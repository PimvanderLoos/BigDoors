package net.minecraft.server;

public class NavigationGuardian extends NavigationAbstract {

    private boolean p;

    public NavigationGuardian(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    protected Pathfinder a() {
        this.p = this.a instanceof EntityDolphin;
        this.o = new PathfinderWater(this.p);
        return new Pathfinder(this.o);
    }

    protected boolean b() {
        return this.p || this.r();
    }

    protected Vec3D c() {
        return new Vec3D(this.a.locX, this.a.locY + (double) this.a.length * 0.5D, this.a.locZ);
    }

    public void d() {
        ++this.e;
        if (this.m) {
            this.l();
        }

        if (!this.p()) {
            Vec3D vec3d;

            if (this.b()) {
                this.o();
            } else if (this.c != null && this.c.e() < this.c.d()) {
                vec3d = this.c.a(this.a, this.c.e());
                if (MathHelper.floor(this.a.locX) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.a.locY) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.a.locZ) == MathHelper.floor(vec3d.z)) {
                    this.c.c(this.c.e() + 1);
                }
            }

            this.n();
            if (!this.p()) {
                vec3d = this.c.a((Entity) this.a);
                this.a.getControllerMove().a(vec3d.x, vec3d.y, vec3d.z, this.d);
            }
        }
    }

    protected void o() {
        if (this.c != null) {
            Vec3D vec3d = this.c();
            float f = this.a.width > 0.75F ? this.a.width / 2.0F : 0.75F - this.a.width / 2.0F;

            if ((double) MathHelper.e((float) this.a.motX) > 0.2D || (double) MathHelper.e((float) this.a.motZ) > 0.2D) {
                f *= MathHelper.sqrt(this.a.motX * this.a.motX + this.a.motY * this.a.motY + this.a.motZ * this.a.motZ) * 6.0F;
            }

            boolean flag = true;
            Vec3D vec3d1 = this.c.f();

            if (MathHelper.e((float) (this.a.locX - (vec3d1.x + 0.5D))) < f && MathHelper.e((float) (this.a.locZ - (vec3d1.z + 0.5D))) < f && Math.abs(this.a.locY - vec3d1.y) < (double) (f * 2.0F)) {
                this.c.a();
            }

            for (int i = Math.min(this.c.e() + 6, this.c.d() - 1); i > this.c.e(); --i) {
                vec3d1 = this.c.a(this.a, i);
                if (vec3d1.distanceSquared(vec3d) <= 36.0D && this.a(vec3d, vec3d1, 0, 0, 0)) {
                    this.c.c(i);
                    break;
                }
            }

            this.a(vec3d);
        }
    }

    protected void a(Vec3D vec3d) {
        if (this.e - this.f > 100) {
            if (vec3d.distanceSquared(this.g) < 2.25D) {
                this.q();
            }

            this.f = this.e;
            this.g = vec3d;
        }

        if (this.c != null && !this.c.b()) {
            Vec3D vec3d1 = this.c.f();

            if (vec3d1.equals(this.h)) {
                this.i += SystemUtils.getMonotonicMillis() - this.j;
            } else {
                this.h = vec3d1;
                double d0 = vec3d.f(this.h);

                this.k = this.a.cK() > 0.0F ? d0 / (double) this.a.cK() * 100.0D : 0.0D;
            }

            if (this.k > 0.0D && (double) this.i > this.k * 2.0D) {
                this.h = Vec3D.a;
                this.i = 0L;
                this.k = 0.0D;
                this.q();
            }

            this.j = SystemUtils.getMonotonicMillis();
        }

    }

    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        MovingObjectPosition movingobjectposition = this.b.rayTrace(vec3d, new Vec3D(vec3d1.x, vec3d1.y + (double) this.a.length * 0.5D, vec3d1.z), FluidCollisionOption.NEVER, true, false);

        return movingobjectposition == null || movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    public boolean a(BlockPosition blockposition) {
        return !this.b.getType(blockposition).f(this.b, blockposition);
    }

    public void d(boolean flag) {}
}
