package net.minecraft.server;

public class PathfinderGoalWaterJump extends PathfinderGoal {

    private static final int[] a = new int[] { 0, 1, 4, 5, 6, 7};
    private final EntityDolphin b;
    private final int c;
    private boolean d;

    public PathfinderGoalWaterJump(EntityDolphin entitydolphin, int i) {
        this.b = entitydolphin;
        this.c = i;
        this.a(5);
    }

    public boolean a() {
        if (this.b.getRandom().nextInt(this.c) != 0) {
            return false;
        } else {
            EnumDirection enumdirection = this.b.getAdjustedDirection();
            int i = enumdirection.getAdjacentX();
            int j = enumdirection.getAdjacentZ();
            BlockPosition blockposition = new BlockPosition(this.b);
            int[] aint = PathfinderGoalWaterJump.a;
            int k = aint.length;

            for (int l = 0; l < k; ++l) {
                int i1 = aint[l];

                if (!this.a(blockposition, i, j, i1) || !this.b(blockposition, i, j, i1)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean a(BlockPosition blockposition, int i, int j, int k) {
        BlockPosition blockposition1 = blockposition.a(i * k, 0, j * k);

        return this.b.world.getFluid(blockposition1).a(TagsFluid.WATER) && !this.b.world.getType(blockposition1).getMaterial().isSolid();
    }

    private boolean b(BlockPosition blockposition, int i, int j, int k) {
        return this.b.world.getType(blockposition.a(i * k, 1, j * k)).isAir() && this.b.world.getType(blockposition.a(i * k, 2, j * k)).isAir();
    }

    public boolean b() {
        return (this.b.motY * this.b.motY >= 0.029999999329447746D || this.b.pitch == 0.0F || Math.abs(this.b.pitch) >= 10.0F || !this.b.isInWater()) && !this.b.onGround;
    }

    public boolean f() {
        return false;
    }

    public void c() {
        EnumDirection enumdirection = this.b.getAdjustedDirection();

        this.b.motX += (double) enumdirection.getAdjacentX() * 0.6D;
        this.b.motY += 0.7D;
        this.b.motZ += (double) enumdirection.getAdjacentZ() * 0.6D;
        this.b.getNavigation().q();
    }

    public void d() {
        this.b.pitch = 0.0F;
    }

    public void e() {
        boolean flag = this.d;

        if (!flag) {
            Fluid fluid = this.b.world.getFluid(new BlockPosition(this.b));

            this.d = fluid.a(TagsFluid.WATER);
        }

        if (this.d && !flag) {
            this.b.a(SoundEffects.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        if (this.b.motY * this.b.motY < 0.029999999329447746D && this.b.pitch != 0.0F) {
            this.b.pitch = this.a(this.b.pitch, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(this.b.motX * this.b.motX + this.b.motY * this.b.motY + this.b.motZ * this.b.motZ);
            double d1 = Math.sqrt(this.b.motX * this.b.motX + this.b.motZ * this.b.motZ);
            double d2 = Math.signum(-this.b.motY) * Math.acos(d1 / d0) * 57.2957763671875D;

            this.b.pitch = (float) d2;
        }

    }

    protected float a(float f, float f1, float f2) {
        float f3;

        for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F) {
            ;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        return f + f2 * f3;
    }
}
