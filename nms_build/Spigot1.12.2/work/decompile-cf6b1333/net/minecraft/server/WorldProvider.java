package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class WorldProvider {

    public static final float[] a = new float[] { 1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    protected World b;
    private WorldType type;
    private String i;
    protected WorldChunkManager c;
    protected boolean d;
    protected boolean e;
    protected boolean f;
    protected final float[] g = new float[16];
    private final float[] j = new float[4];

    public WorldProvider() {}

    public final void a(World world) {
        this.b = world;
        this.type = world.getWorldData().getType();
        this.i = world.getWorldData().getGeneratorOptions();
        this.b();
        this.a();
    }

    protected void a() {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.g[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
        }

    }

    protected void b() {
        this.f = true;
        WorldType worldtype = this.b.getWorldData().getType();

        if (worldtype == WorldType.FLAT) {
            WorldGenFlatInfo worldgenflatinfo = WorldGenFlatInfo.a(this.b.getWorldData().getGeneratorOptions());

            this.c = new WorldChunkManagerHell(BiomeBase.getBiome(worldgenflatinfo.a(), Biomes.b));
        } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.c = new WorldChunkManagerHell(Biomes.c);
        } else {
            this.c = new WorldChunkManager(this.b.getWorldData());
        }

    }

    public ChunkGenerator getChunkGenerator() {
        return (ChunkGenerator) (this.type == WorldType.FLAT ? new ChunkProviderFlat(this.b, this.b.getSeed(), this.b.getWorldData().shouldGenerateMapFeatures(), this.i) : (this.type == WorldType.DEBUG_ALL_BLOCK_STATES ? new ChunkProviderDebug(this.b) : (this.type == WorldType.CUSTOMIZED ? new ChunkProviderGenerate(this.b, this.b.getSeed(), this.b.getWorldData().shouldGenerateMapFeatures(), this.i) : new ChunkProviderGenerate(this.b, this.b.getSeed(), this.b.getWorldData().shouldGenerateMapFeatures(), this.i))));
    }

    public boolean canSpawn(int i, int j) {
        BlockPosition blockposition = new BlockPosition(i, 0, j);

        return this.b.getBiome(blockposition).i() ? true : this.b.c(blockposition).getBlock() == Blocks.GRASS;
    }

    public float a(long i, float f) {
        int j = (int) (i % 24000L);
        float f1 = ((float) j + f) / 24000.0F - 0.25F;

        if (f1 < 0.0F) {
            ++f1;
        }

        if (f1 > 1.0F) {
            --f1;
        }

        float f2 = f1;

        f1 = 1.0F - (float) ((Math.cos((double) f1 * 3.141592653589793D) + 1.0D) / 2.0D);
        f1 = f2 + (f1 - f2) / 3.0F;
        return f1;
    }

    public int a(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public boolean d() {
        return true;
    }

    public boolean e() {
        return true;
    }

    @Nullable
    public BlockPosition h() {
        return null;
    }

    public int getSeaLevel() {
        return this.type == WorldType.FLAT ? 4 : this.b.getSeaLevel() + 1;
    }

    public WorldChunkManager k() {
        return this.c;
    }

    public boolean l() {
        return this.d;
    }

    public boolean m() {
        return this.f;
    }

    public boolean n() {
        return this.e;
    }

    public float[] o() {
        return this.g;
    }

    public WorldBorder getWorldBorder() {
        return new WorldBorder();
    }

    public void a(EntityPlayer entityplayer) {}

    public void b(EntityPlayer entityplayer) {}

    public abstract DimensionManager getDimensionManager();

    public void r() {}

    public void s() {}

    public boolean c(int i, int j) {
        return true;
    }
}
