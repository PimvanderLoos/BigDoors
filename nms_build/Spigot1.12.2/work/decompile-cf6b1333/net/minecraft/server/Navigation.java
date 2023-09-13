package net.minecraft.server;

import java.util.Iterator;

public class Navigation extends NavigationAbstract {

    private boolean i;

    public Navigation(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    protected Pathfinder a() {
        this.h = new PathfinderNormal();
        this.h.a(true);
        return new Pathfinder(this.h);
    }

    protected boolean b() {
        return this.a.onGround || this.h() && this.q() || this.a.isPassenger();
    }

    protected Vec3D c() {
        return new Vec3D(this.a.locX, (double) this.s(), this.a.locZ);
    }

    public PathEntity b(BlockPosition blockposition) {
        BlockPosition blockposition1;

        if (this.b.getType(blockposition).getMaterial() == Material.AIR) {
            for (blockposition1 = blockposition.down(); blockposition1.getY() > 0 && this.b.getType(blockposition1).getMaterial() == Material.AIR; blockposition1 = blockposition1.down()) {
                ;
            }

            if (blockposition1.getY() > 0) {
                return super.b(blockposition1.up());
            }

            while (blockposition1.getY() < this.b.getHeight() && this.b.getType(blockposition1).getMaterial() == Material.AIR) {
                blockposition1 = blockposition1.up();
            }

            blockposition = blockposition1;
        }

        if (!this.b.getType(blockposition).getMaterial().isBuildable()) {
            return super.b(blockposition);
        } else {
            for (blockposition1 = blockposition.up(); blockposition1.getY() < this.b.getHeight() && this.b.getType(blockposition1).getMaterial().isBuildable(); blockposition1 = blockposition1.up()) {
                ;
            }

            return super.b(blockposition1);
        }
    }

    public PathEntity a(Entity entity) {
        return this.b(new BlockPosition(entity));
    }

    private int s() {
        if (this.a.isInWater() && this.h()) {
            int i = (int) this.a.getBoundingBox().b;
            Block block = this.b.getType(new BlockPosition(MathHelper.floor(this.a.locX), i, MathHelper.floor(this.a.locZ))).getBlock();
            int j = 0;

            do {
                if (block != Blocks.FLOWING_WATER && block != Blocks.WATER) {
                    return i;
                }

                ++i;
                block = this.b.getType(new BlockPosition(MathHelper.floor(this.a.locX), i, MathHelper.floor(this.a.locZ))).getBlock();
                ++j;
            } while (j <= 16);

            return (int) this.a.getBoundingBox().b;
        } else {
            return (int) (this.a.getBoundingBox().b + 0.5D);
        }
    }

    protected void q_() {
        super.q_();
        if (this.i) {
            if (this.b.h(new BlockPosition(MathHelper.floor(this.a.locX), (int) (this.a.getBoundingBox().b + 0.5D), MathHelper.floor(this.a.locZ)))) {
                return;
            }

            for (int i = 0; i < this.c.d(); ++i) {
                PathPoint pathpoint = this.c.a(i);

                if (this.b.h(new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c))) {
                    this.c.b(i - 1);
                    return;
                }
            }
        }

    }

    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        int l = MathHelper.floor(vec3d.x);
        int i1 = MathHelper.floor(vec3d.z);
        double d0 = vec3d1.x - vec3d.x;
        double d1 = vec3d1.z - vec3d.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);

            d0 *= d3;
            d1 *= d3;
            i += 2;
            k += 2;
            if (!this.a(l, (int) vec3d.y, i1, i, j, k, vec3d, d0, d1)) {
                return false;
            } else {
                i -= 2;
                k -= 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) l - vec3d.x;
                double d7 = (double) i1 - vec3d.z;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 /= d0;
                d7 /= d1;
                int j1 = d0 < 0.0D ? -1 : 1;
                int k1 = d1 < 0.0D ? -1 : 1;
                int l1 = MathHelper.floor(vec3d1.x);
                int i2 = MathHelper.floor(vec3d1.z);
                int j2 = l1 - l;
                int k2 = i2 - i1;

                do {
                    if (j2 * j1 <= 0 && k2 * k1 <= 0) {
                        return true;
                    }

                    if (d6 < d7) {
                        d6 += d4;
                        l += j1;
                        j2 = l1 - l;
                    } else {
                        d7 += d5;
                        i1 += k1;
                        k2 = i2 - i1;
                    }
                } while (this.a(l, (int) vec3d.y, i1, i, j, k, vec3d, d0, d1));

                return false;
            }
        }
    }

    private boolean a(int i, int j, int k, int l, int i1, int j1, Vec3D vec3d, double d0, double d1) {
        int k1 = i - l / 2;
        int l1 = k - j1 / 2;

        if (!this.b(k1, j, l1, l, i1, j1, vec3d, d0, d1)) {
            return false;
        } else {
            for (int i2 = k1; i2 < k1 + l; ++i2) {
                for (int j2 = l1; j2 < l1 + j1; ++j2) {
                    double d2 = (double) i2 + 0.5D - vec3d.x;
                    double d3 = (double) j2 + 0.5D - vec3d.z;

                    if (d2 * d0 + d3 * d1 >= 0.0D) {
                        PathType pathtype = this.h.a(this.b, i2, j - 1, j2, this.a, l, i1, j1, true, true);

                        if (pathtype == PathType.WATER) {
                            return false;
                        }

                        if (pathtype == PathType.LAVA) {
                            return false;
                        }

                        if (pathtype == PathType.OPEN) {
                            return false;
                        }

                        pathtype = this.h.a(this.b, i2, j, j2, this.a, l, i1, j1, true, true);
                        float f = this.a.a(pathtype);

                        if (f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        if (pathtype == PathType.DAMAGE_FIRE || pathtype == PathType.DANGER_FIRE || pathtype == PathType.DAMAGE_OTHER) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean b(int i, int j, int k, int l, int i1, int j1, Vec3D vec3d, double d0, double d1) {
        Iterator iterator = BlockPosition.a(new BlockPosition(i, j, k), new BlockPosition(i + l - 1, j + i1 - 1, k + j1 - 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();
            double d2 = (double) blockposition.getX() + 0.5D - vec3d.x;
            double d3 = (double) blockposition.getZ() + 0.5D - vec3d.z;

            if (d2 * d0 + d3 * d1 >= 0.0D) {
                Block block = this.b.getType(blockposition).getBlock();

                if (!block.b(this.b, blockposition)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void a(boolean flag) {
        this.h.b(flag);
    }

    public void b(boolean flag) {
        this.h.a(flag);
    }

    public boolean g() {
        return this.h.c();
    }

    public void c(boolean flag) {
        this.h.c(flag);
    }

    public boolean h() {
        return this.h.e();
    }

    public void d(boolean flag) {
        this.i = flag;
    }
}
