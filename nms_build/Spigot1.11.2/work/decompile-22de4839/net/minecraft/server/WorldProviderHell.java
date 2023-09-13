package net.minecraft.server;

public class WorldProviderHell extends WorldProvider {

    public WorldProviderHell() {}

    public void b() {
        this.c = new WorldChunkManagerHell(Biomes.j);
        this.d = true;
        this.e = true;
    }

    protected void a() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.g[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }

    }

    public ChunkGenerator getChunkGenerator() {
        return new ChunkProviderHell(this.b, this.b.getWorldData().shouldGenerateMapFeatures(), this.b.getSeed());
    }

    public boolean d() {
        return false;
    }

    public boolean canSpawn(int i, int j) {
        return false;
    }

    public float a(long i, float f) {
        return 0.5F;
    }

    public boolean e() {
        return false;
    }

    public WorldBorder getWorldBorder() {
        return new WorldBorder() {
            public double getCenterX() {
                return super.getCenterX() / 8.0D;
            }

            public double getCenterZ() {
                return super.getCenterZ() / 8.0D;
            }
        };
    }

    public DimensionManager getDimensionManager() {
        return DimensionManager.NETHER;
    }
}
