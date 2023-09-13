package net.minecraft.world.level.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.dimension.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeStorage implements BiomeManager.Provider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WIDTH_BITS = MathHelper.e(16) - 2;
    private static final int HORIZONTAL_MASK = (1 << BiomeStorage.WIDTH_BITS) - 1;
    public static final int MAX_SIZE = 1 << BiomeStorage.WIDTH_BITS + BiomeStorage.WIDTH_BITS + DimensionManager.BITS_FOR_Y - 2;
    public final Registry<BiomeBase> biomeRegistry;
    private final BiomeBase[] biomes;
    private final int quartMinY;
    private final int quartHeight;

    protected BiomeStorage(Registry<BiomeBase> registry, LevelHeightAccessor levelheightaccessor, BiomeBase[] abiomebase) {
        this.biomeRegistry = registry;
        this.biomes = abiomebase;
        this.quartMinY = QuartPos.a(levelheightaccessor.getMinBuildHeight());
        this.quartHeight = QuartPos.a(levelheightaccessor.getHeight()) - 1;
    }

    public BiomeStorage(Registry<BiomeBase> registry, LevelHeightAccessor levelheightaccessor, int[] aint) {
        this(registry, levelheightaccessor, new BiomeBase[aint.length]);
        int i = -1;

        for (int j = 0; j < this.biomes.length; ++j) {
            int k = aint[j];
            BiomeBase biomebase = (BiomeBase) registry.fromId(k);

            if (biomebase == null) {
                if (i == -1) {
                    i = j;
                }

                this.biomes[j] = (BiomeBase) registry.fromId(0);
            } else {
                this.biomes[j] = biomebase;
            }
        }

        if (i != -1) {
            BiomeStorage.LOGGER.warn("Invalid biome data received, starting from {}: {}", i, Arrays.toString(aint));
        }

    }

    public BiomeStorage(Registry<BiomeBase> registry, LevelHeightAccessor levelheightaccessor, ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager) {
        this(registry, levelheightaccessor, chunkcoordintpair, worldchunkmanager, (int[]) null);
    }

    public BiomeStorage(Registry<BiomeBase> registry, LevelHeightAccessor levelheightaccessor, ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager, @Nullable int[] aint) {
        this(registry, levelheightaccessor, new BiomeBase[(1 << BiomeStorage.WIDTH_BITS + BiomeStorage.WIDTH_BITS) * a(levelheightaccessor.getHeight(), 4)]);
        int i = QuartPos.a(chunkcoordintpair.d());
        int j = this.quartMinY;
        int k = QuartPos.a(chunkcoordintpair.e());

        for (int l = 0; l < this.biomes.length; ++l) {
            if (aint != null && l < aint.length) {
                this.biomes[l] = (BiomeBase) registry.fromId(aint[l]);
            }

            if (this.biomes[l] == null) {
                this.biomes[l] = a(worldchunkmanager, i, j, k, l);
            }
        }

    }

    private static int a(int i, int j) {
        return (i + j - 1) / j;
    }

    private static BiomeBase a(WorldChunkManager worldchunkmanager, int i, int j, int k, int l) {
        int i1 = l & BiomeStorage.HORIZONTAL_MASK;
        int j1 = l >> BiomeStorage.WIDTH_BITS + BiomeStorage.WIDTH_BITS;
        int k1 = l >> BiomeStorage.WIDTH_BITS & BiomeStorage.HORIZONTAL_MASK;

        return worldchunkmanager.getBiome(i + i1, j + j1, k + k1);
    }

    public int[] a() {
        int[] aint = new int[this.biomes.length];

        for (int i = 0; i < this.biomes.length; ++i) {
            aint[i] = this.biomeRegistry.getId(this.biomes[i]);
        }

        return aint;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = i & BiomeStorage.HORIZONTAL_MASK;
        int i1 = MathHelper.clamp(j - this.quartMinY, 0, this.quartHeight);
        int j1 = k & BiomeStorage.HORIZONTAL_MASK;

        return this.biomes[i1 << BiomeStorage.WIDTH_BITS + BiomeStorage.WIDTH_BITS | j1 << BiomeStorage.WIDTH_BITS | l];
    }
}
