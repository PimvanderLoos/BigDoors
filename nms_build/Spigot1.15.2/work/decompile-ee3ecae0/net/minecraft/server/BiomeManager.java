package net.minecraft.server;

public class BiomeManager {

    private final BiomeManager.Provider a;
    private final long b;
    private final GenLayerZoomer c;

    public BiomeManager(BiomeManager.Provider biomemanager_provider, long i, GenLayerZoomer genlayerzoomer) {
        this.a = biomemanager_provider;
        this.b = i;
        this.c = genlayerzoomer;
    }

    public BiomeManager a(WorldChunkManager worldchunkmanager) {
        return new BiomeManager(worldchunkmanager, this.b, this.c);
    }

    public BiomeBase a(BlockPosition blockposition) {
        return this.c.a(this.b, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.a);
    }

    public interface Provider {

        BiomeBase getBiome(int i, int j, int k);
    }
}
