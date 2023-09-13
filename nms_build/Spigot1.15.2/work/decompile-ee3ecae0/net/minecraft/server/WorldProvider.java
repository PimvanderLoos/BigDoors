package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class WorldProvider {

    public static final float[] a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    protected final World b;
    private final DimensionManager f;
    protected boolean c;
    protected boolean d;
    protected final float[] e = new float[16];
    private final float[] g = new float[4];

    public WorldProvider(World world, DimensionManager dimensionmanager, float f) {
        this.b = world;
        this.f = dimensionmanager;

        for (int i = 0; i <= 15; ++i) {
            float f1 = (float) i / 15.0F;
            float f2 = f1 / (4.0F - 3.0F * f1);

            this.e[i] = MathHelper.g(f, f2, 1.0F);
        }

    }

    public int a(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    @Nullable
    public BlockPosition c() {
        return null;
    }

    public boolean isNether() {
        return this.c;
    }

    public boolean f() {
        return this.f.hasSkyLight();
    }

    public boolean g() {
        return this.d;
    }

    public float a(int i) {
        return this.e[i];
    }

    public WorldBorder getWorldBorder() {
        return new WorldBorder();
    }

    public void i() {}

    public void j() {}

    public abstract ChunkGenerator<?> getChunkGenerator();

    @Nullable
    public abstract BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag);

    @Nullable
    public abstract BlockPosition a(int i, int j, boolean flag);

    public abstract float a(long i, float f);

    public abstract boolean isOverworld();

    public abstract boolean canRespawn();

    public abstract DimensionManager getDimensionManager();
}
