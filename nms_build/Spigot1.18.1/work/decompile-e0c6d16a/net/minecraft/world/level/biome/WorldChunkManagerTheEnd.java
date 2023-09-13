package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3Handler;

public class WorldChunkManagerTheEnd extends WorldChunkManager {

    public static final Codec<WorldChunkManagerTheEnd> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryLookupCodec.create(IRegistry.BIOME_REGISTRY).forGetter((worldchunkmanagertheend) -> {
            return worldchunkmanagertheend.biomes;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((worldchunkmanagertheend) -> {
            return worldchunkmanagertheend.seed;
        })).apply(instance, instance.stable(WorldChunkManagerTheEnd::new));
    });
    private static final float ISLAND_THRESHOLD = -0.9F;
    public static final int ISLAND_CHUNK_DISTANCE = 64;
    private static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
    private final NoiseGenerator3Handler islandNoise;
    private final IRegistry<BiomeBase> biomes;
    private final long seed;
    private final BiomeBase end;
    private final BiomeBase highlands;
    private final BiomeBase midlands;
    private final BiomeBase islands;
    private final BiomeBase barrens;

    public WorldChunkManagerTheEnd(IRegistry<BiomeBase> iregistry, long i) {
        this(iregistry, i, (BiomeBase) iregistry.getOrThrow(Biomes.THE_END), (BiomeBase) iregistry.getOrThrow(Biomes.END_HIGHLANDS), (BiomeBase) iregistry.getOrThrow(Biomes.END_MIDLANDS), (BiomeBase) iregistry.getOrThrow(Biomes.SMALL_END_ISLANDS), (BiomeBase) iregistry.getOrThrow(Biomes.END_BARRENS));
    }

    private WorldChunkManagerTheEnd(IRegistry<BiomeBase> iregistry, long i, BiomeBase biomebase, BiomeBase biomebase1, BiomeBase biomebase2, BiomeBase biomebase3, BiomeBase biomebase4) {
        super((List) ImmutableList.of(biomebase, biomebase1, biomebase2, biomebase3, biomebase4));
        this.biomes = iregistry;
        this.seed = i;
        this.end = biomebase;
        this.highlands = biomebase1;
        this.midlands = biomebase2;
        this.islands = biomebase3;
        this.barrens = biomebase4;
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(i));

        seededrandom.consumeCount(17292);
        this.islandNoise = new NoiseGenerator3Handler(seededrandom);
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerTheEnd.CODEC;
    }

    @Override
    public WorldChunkManager withSeed(long i) {
        return new WorldChunkManagerTheEnd(this.biomes, i, this.end, this.highlands, this.midlands, this.islands, this.barrens);
    }

    @Override
    public BiomeBase getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        int l = i >> 2;
        int i1 = k >> 2;

        if ((long) l * (long) l + (long) i1 * (long) i1 <= 4096L) {
            return this.end;
        } else {
            float f = getHeightValue(this.islandNoise, l * 2 + 1, i1 * 2 + 1);

            return f > 40.0F ? this.highlands : (f >= 0.0F ? this.midlands : (f < -20.0F ? this.islands : this.barrens));
        }
    }

    public boolean stable(long i) {
        return this.seed == i;
    }

    public static float getHeightValue(NoiseGenerator3Handler noisegenerator3handler, int i, int j) {
        int k = i / 2;
        int l = j / 2;
        int i1 = i % 2;
        int j1 = j % 2;
        float f = 100.0F - MathHelper.sqrt((float) (i * i + j * j)) * 8.0F;

        f = MathHelper.clamp(f, -100.0F, 80.0F);

        for (int k1 = -12; k1 <= 12; ++k1) {
            for (int l1 = -12; l1 <= 12; ++l1) {
                long i2 = (long) (k + k1);
                long j2 = (long) (l + l1);

                if (i2 * i2 + j2 * j2 > 4096L && noisegenerator3handler.getValue((double) i2, (double) j2) < -0.8999999761581421D) {
                    float f1 = (MathHelper.abs((float) i2) * 3439.0F + MathHelper.abs((float) j2) * 147.0F) % 13.0F + 9.0F;
                    float f2 = (float) (i1 - k1 * 2);
                    float f3 = (float) (j1 - l1 * 2);
                    float f4 = 100.0F - MathHelper.sqrt(f2 * f2 + f3 * f3) * f1;

                    f4 = MathHelper.clamp(f4, -100.0F, 80.0F);
                    f = Math.max(f, f4);
                }
            }
        }

        return f;
    }
}
