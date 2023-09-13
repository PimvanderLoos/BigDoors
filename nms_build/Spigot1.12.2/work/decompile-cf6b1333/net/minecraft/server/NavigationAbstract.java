package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class NavigationAbstract {

    protected EntityInsentient a;
    protected World b;
    @Nullable
    protected PathEntity c;
    protected double d;
    private final AttributeInstance i;
    protected int e;
    private int j;
    private Vec3D k;
    private Vec3D l;
    private long m;
    private long n;
    private double o;
    protected float f;
    protected boolean g;
    private long p;
    protected PathfinderAbstract h;
    private BlockPosition q;
    private final Pathfinder r;

    public NavigationAbstract(EntityInsentient entityinsentient, World world) {
        this.k = Vec3D.a;
        this.l = Vec3D.a;
        this.f = 0.5F;
        this.a = entityinsentient;
        this.b = world;
        this.i = entityinsentient.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        this.r = this.a();
    }

    protected abstract Pathfinder a();

    public void a(double d0) {
        this.d = d0;
    }

    public float i() {
        return (float) this.i.getValue();
    }

    public boolean j() {
        return this.g;
    }

    public void k() {
        if (this.b.getTime() - this.p > 20L) {
            if (this.q != null) {
                this.c = null;
                this.c = this.b(this.q);
                this.p = this.b.getTime();
                this.g = false;
            }
        } else {
            this.g = true;
        }

    }

    @Nullable
    public final PathEntity a(double d0, double d1, double d2) {
        return this.b(new BlockPosition(d0, d1, d2));
    }

    @Nullable
    public PathEntity b(BlockPosition blockposition) {
        if (!this.b()) {
            return null;
        } else if (this.c != null && !this.c.b() && blockposition.equals(this.q)) {
            return this.c;
        } else {
            this.q = blockposition;
            float f = this.i();

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
                float f = this.i();

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

            this.q_();
            if (this.c.d() <= 0) {
                return false;
            } else {
                this.d = d0;
                Vec3D vec3d = this.c();

                this.j = this.e;
                this.k = vec3d;
                return true;
            }
        }
    }

    @Nullable
    public PathEntity l() {
        return this.c;
    }

    public void d() {
        ++this.e;
        if (this.g) {
            this.k();
        }

        if (!this.o()) {
            Vec3D vec3d;

            if (this.b()) {
                this.n();
            } else if (this.c != null && this.c.e() < this.c.d()) {
                vec3d = this.c();
                Vec3D vec3d1 = this.c.a(this.a, this.c.e());

                if (vec3d.y > vec3d1.y && !this.a.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.c.c(this.c.e() + 1);
                }
            }

            this.m();
            if (!this.o()) {
                vec3d = this.c.a((Entity) this.a);
                BlockPosition blockposition = (new BlockPosition(vec3d)).down();
                AxisAlignedBB axisalignedbb = this.b.getType(blockposition).e(this.b, blockposition);

                vec3d = vec3d.a(0.0D, 1.0D - axisalignedbb.e, 0.0D);
                this.a.getControllerMove().a(vec3d.x, vec3d.y, vec3d.z, this.d);
            }
        }
    }

    protected void m() {}

    protected void n() {
        Vec3D vec3d = this.c();
        int i = this.c.d();

        for (int j = this.c.e(); j < this.c.d(); ++j) {
            if ((double) this.c.a(j).b != Math.floor(vec3d.y)) {
                i = j;
                break;
            }
        }

        this.f = this.a.width > 0.75F ? this.a.width / 2.0F : 0.75F - this.a.width / 2.0F;
        Vec3D vec3d1 = this.c.f();

        if (MathHelper.e((float) (this.a.locX - (vec3d1.x + 0.5D))) < this.f && MathHelper.e((float) (this.a.locZ - (vec3d1.z + 0.5D))) < this.f && Math.abs(this.a.locY - vec3d1.y) < 1.0D) {
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
        if (this.e - this.j > 100) {
            if (vec3d.distanceSquared(this.k) < 2.25D) {
                this.p();
            }

            this.j = this.e;
            this.k = vec3d;
        }

        if (this.c != null && !this.c.b()) {
            Vec3D vec3d1 = this.c.f();

            if (vec3d1.equals(this.l)) {
                this.m += System.currentTimeMillis() - this.n;
            } else {
                this.l = vec3d1;
                double d0 = vec3d.f(this.l);

                this.o = this.a.cy() > 0.0F ? d0 / (double) this.a.cy() * 1000.0D : 0.0D;
            }

            if (this.o > 0.0D && (double) this.m > this.o * 3.0D) {
                this.l = Vec3D.a;
                this.m = 0L;
                this.o = 0.0D;
                this.p();
            }

            this.n = System.currentTimeMillis();
        }

    }

    public boolean o() {
        return this.c == null || this.c.b();
    }

    public void p() {
        this.c = null;
    }

    protected abstract Vec3D c();

    protected abstract boolean b();

    protected boolean q() {
        return this.a.isInWater() || this.a.au();
    }

    protected void q_() {
        if (this.c != null) {
            for (int i = 0; i < this.c.d(); ++i) {
                PathPoint pathpoint = this.c.a(i);
                PathPoint pathpoint1 = i + 1 < this.c.d() ? this.c.a(i + 1) : null;
                IBlockData iblockdata = this.b.getType(new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c));
                Block block = iblockdata.getBlock();

                if (block == Blocks.cauldron) {
                    this.c.a(i, pathpoint.a(pathpoint.a, pathpoint.b + 1, pathpoint.c));
                    if (pathpoint1 != null && pathpoint.b >= pathpoint1.b) {
                        this.c.a(i + 1, pathpoint1.a(pathpoint1.a, pathpoint.b + 1, pathpoint1.c));
                    }
                }
            }

        }
    }

    protected abstract boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k);

    public boolean a(BlockPosition blockposition) {
        return this.b.getType(blockposition.down()).b();
    }

    public PathfinderAbstract r() {
        return this.h;
    }
}
