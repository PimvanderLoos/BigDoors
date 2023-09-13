package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPosition;

public class BiomeManager {

    private final BiomeManager.Provider a;
    private final long b;
    private final GenLayerZoomer c;

    public BiomeManager(BiomeManager.Provider biomemanager_provider, long i, GenLayerZoomer genlayerzoomer) {
        this.a = biomemanager_provider;
        this.b = i;
        this.c = genlayerzoomer;
    }

    public static long a(long i) {
        return Hashing.sha256().hashLong(i).asLong();
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
