package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class NavigationAbstract {

    protected EntityInsentient a;
    protected World b;
    @Nullable
    protected PathEntity c;
    protected double d;
    private final AttributeInstance f;
    private int g;
    private int h;
    private Vec3D i;
    private Vec3D j;
    private long k;
    private long l;
    private double m;
    private float n;
    private boolean o;
    private long p;
    protected PathfinderAbstract e;
    private BlockPosition q;
    private final Pathfinder r;

    public NavigationAbstract(EntityInsentient entityinsentient, World world) {
        this.i = Vec3D.a;
        this.j = Vec3D.a;
        this.n = 0.5F;
        this.a = entityinsentient;
        this.b = world;
        this.f = entityinsentient.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        this.r = this.a();
    }

    protected abstract Pathfinder a();

    public void a(double d0) {
        this.d = d0;
    }

    public float h() {
        return (float) this.f.getValue();
    }

    public boolean i() {
        return this.o;
    }

    public void j() {
        if (this.b.getTime() - this.p > 20L) {
            if (this.q != null) {
                this.c = null;
                this.c = this.a(this.q);
                this.p = this.b.getTime();
                this.o = false;
            }
        } else {
            this.o = true;
        }

    }

    @Nullable
    public final PathEntity a(double d0, double d1, double d2) {
        return this.a(new BlockPosition(d0, d1, d2));
    }

    @Nullable
    public PathEntity a(BlockPosition blockposition) {
        if (!this.b()) {
            return null;
        } else if (this.c != null && !this.c.b() && blockposition.equals(this.q)) {
            return this.c;
        } else {
            this.q = blockposition;
            float f = this.h();

            this.b.methodProfiler.a("pathfind");
            BlockPosition blockposition1 = new BlockPosition(this.a);
            int i = (int) (f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(this.b, blockposition1.a(-i, -i, -i), blockposition1.a(i, i, i), 0);
            PathEntity pathentity = this.r.a(chunkcache, this.a, this.q, f);

            this.b.methodProfiler.b();
            return pathentity;
        }
    }

    @Nullable
    public PathEntity a(Entity entity) {
        if (!this.b()) {
            return null;
        } else {
            BlockPosition blockposition = new BlockPosition(entity);

            if (this.c != null && !this.c.b() && blockposition.equals(this.q)) {
                return this.c;
            } else {
                this.q = blockposition;
                float f = this.h();

                this.b.methodProfiler.a("pathfind");
                BlockPosition blockposition1 = (new BlockPosition(this.a)).up();
                int i = (int) (f + 16.0F);
                ChunkCache chunkcache = new ChunkCache(this.b, blockposition1.a(-i, -i, -i), blockposition1.a(i, i, i), 0);
                PathEntity pathentity = this.r.a(chunkcache, this.a, entity, f);

                this.b.methodProfiler.b();
                return pathentity;
            }
        }
    }

    public boolean a(double d0, double d1, double d2, double d3) {
        return this.a(this.a(d0, d1, d2), d3);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity);

        return pathentity != null && this.a(pathentity, d0);
    }

    public boolean a(@Nullable PathEntity pathentity, double d0) {
        if (pathentity == null) {
            this.c = null;
            return false;
        } else {
            if (!pathentity.a(this.c)) {
                this.c = pathentity;
            }

            this.d();
            if (this.c.d() == 0) {
                return false;
            } else {
                this.d = d0;
                Vec3D vec3d = this.c();

                this.h = this.g;
                this.i = vec3d;
                return true;
            }
        }
    }

    @Nullable
    public PathEntity k() {
        return this.c;
    }

    public void l() {
        ++this.g;
        if (this.o) {
            this.j();
        }

        if (!this.n()) {
            Vec3D vec3d;

            if (this.b()) {
                this.m();
            } else if (this.c != null && this.c.e() < this.c.d()) {
                vec3d = this.c();
                Vec3D vec3d1 = this.c.a(this.a, this.c.e());

                if (vec3d.y > vec3d1.y && !this.a.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.c.c(this.c.e() + 1);
                }
            }

            if (!this.n()) {
                vec3d = this.c.a((Entity) this.a);
                if (vec3d != null) {
                    BlockPosition blockposition = (new BlockPosition(vec3d)).down();
                    AxisAlignedBB axisalignedbb = this.b.getType(blockposition).d(this.b, blockposition);

                    vec3d = vec3d.a(0.0D, 1.0D - axisalignedbb.e, 0.0D);
                    this.a.getControllerMove().a(vec3d.x, vec3d.y, vec3d.z, this.d);
                }
            }
        }
    }

    protected void m() {
        Vec3D vec3d = this.c();
        int i = this.c.d();

        for (int j = this.c.e(); j < this.c.d(); ++j) {
            if ((double) this.c.a(j).b != Math.floor(vec3d.y)) {
                i = j;
                break;
            }
        }

        this.n = this.a.width > 0.75F ? this.a.width / 2.0F : 0.75F - this.a.width / 2.0F;
        Vec3D vec3d1 = this.c.f();

        if (MathHelper.e((float) (this.a.locX - (vec3d1.x + 0.5D))) < this.n && MathHelper.e((float) (this.a.locZ - (vec3d1.z + 0.5D))) < this.n && Math.abs(this.a.locY - vec3d1.y) < 1.0D) {
            this.c.c(this.c.e() + 1);
        }

        int k = MathHelper.f(this.a.width);
        int l = MathHelper.f(this.a.length);
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.c.e(); --j1) {
            if (this.a(vec3d, this.c.a(this.a, j1), k, l, i1)) {
                this.c.c(j1);
                break;
            }
        }

        this.a(vec3d);
    }

    protected void a(Vec3D vec3d) {
        if (this.g - this.h > 100) {
            if (vec3d.distanceSquared(this.i) < 2.25D) {
                this.o();
            }

            this.h = this.g;
            this.i = vec3d;
        }

        if (this.c != null && !this.c.b()) {
            Vec3D vec3d1 = this.c.f();

            if (vec3d1.equals(this.j)) {
                this.k += System.currentTimeMillis() - this.l;
            } else {
                this.j = vec3d1;
                double d0 = vec3d.f(this.j);

                this.m = this.a.cq() > 0.0F ? d0 / (double) this.a.cq() * 1000.0D : 0.0D;
            }

            if (this.m > 0.0D && (double) this.k > this.m * 3.0D) {
                this.j = Vec3D.a;
                this.k = 0L;
                this.m = 0.0D;
                this.o();
            }

            this.l = System.currentTimeMillis();
        }

    }

    public boolean n() {
        return this.c == null || this.c.b();
    }

    public void o() {
        this.c = null;
    }

    protected abstract Vec3D c();

    protected abstract boolean b();

    protected boolean p() {
        return this.a.isInWater() || this.a.ao();
    }

    protected void d() {}

    protected abstract boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k);

    public boolean b(BlockPosition blockposition) {
        return this.b.getType(blockposition.down()).b();
    }

    public PathfinderAbstract q() {
        return this.e;
    }
}
