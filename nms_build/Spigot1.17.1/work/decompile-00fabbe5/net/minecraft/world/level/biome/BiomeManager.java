package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;

public class BiomeManager {

    static final int CHUNK_CENTER_QUART = QuartPos.a(8);
    private final BiomeManager.Provider noiseBiomeSource;
    private final long biomeZoomSeed;
    private final GenLayerZoomer zoomer;

    public BiomeManager(BiomeManager.Provider biomemanager_provider, long i, GenLayerZoomer genlayerzoomer) {
        this.noiseBiomeSource = biomemanager_provider;
        this.biomeZoomSeed = i;
        this.zoomer = genlayerzoomer;
    }

    public static long a(long i) {
        return Hashing.sha256().hashLong(i).asLong();
    }

    public BiomeManager a(WorldChunkManager worldchunkmanager) {
        return new BiomeManager(worldchunkmanager, this.biomeZoomSeed, this.zoomer);
    }

    public BiomeBase a(BlockPosition blockposition) {
        return this.zoomer.a(this.biomeZoomSeed, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.noiseBiomeSource);
    }

    public BiomeBase a(double d0, double d1, double d2) {
        int i = QuartPos.a(MathHelper.floor(d0));
        int j = QuartPos.a(MathHelper.floor(d1));
        int k = QuartPos.a(MathHelper.floor(d2));

        return this.a(i, j, k);
    }

    public BiomeBase b(BlockPosition blockposition) {
        int i = QuartPos.a(blockposition.getX());
        int j = QuartPos.a(blockposition.getY());
        int k = QuartPos.a(blockposition.getZ());

        return this.a(i, j, k);
    }

    public BiomeBase a(int i, int j, int k) {
        return this.noiseBiomeSource.getBiome(i, j, k);
    }

    public BiomeBase a(ChunkCoordIntPair chunkcoordintpair) {
        return this.noiseBiomeSource.b(chunkcoordintpair);
    }

    public interface Provider {

        BiomeBase getBiome(int i, int j, int k);

        default BiomeBase b(ChunkCoordIntPair chunkcoordintpair) {
            return this.getBiome(QuartPos.c(chunkcoordintpair.x) + BiomeManager.CHUNK_CENTER_QUART, 0, QuartPos.c(chunkcoordintpair.z) + BiomeManager.CHUNK_CENTER_QUART);
        }
    }
}
