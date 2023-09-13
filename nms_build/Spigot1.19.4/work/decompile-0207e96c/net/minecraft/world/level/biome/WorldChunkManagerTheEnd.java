package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.DensityFunction;

public class WorldChunkManagerTheEnd extends WorldChunkManager {

    public static final Codec<WorldChunkManagerTheEnd> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryOps.retrieveElement(Biomes.THE_END), RegistryOps.retrieveElement(Biomes.END_HIGHLANDS), RegistryOps.retrieveElement(Biomes.END_MIDLANDS), RegistryOps.retrieveElement(Biomes.SMALL_END_ISLANDS), RegistryOps.retrieveElement(Biomes.END_BARRENS)).apply(instance, instance.stable(WorldChunkManagerTheEnd::new));
    });
    private final Holder<BiomeBase> end;
    private final Holder<BiomeBase> highlands;
    private final Holder<BiomeBase> midlands;
    private final Holder<BiomeBase> islands;
    private final Holder<BiomeBase> barrens;

    public static WorldChunkManagerTheEnd create(HolderGetter<BiomeBase> holdergetter) {
        return new WorldChunkManagerTheEnd(holdergetter.getOrThrow(Biomes.THE_END), holdergetter.getOrThrow(Biomes.END_HIGHLANDS), holdergetter.getOrThrow(Biomes.END_MIDLANDS), holdergetter.getOrThrow(Biomes.SMALL_END_ISLANDS), holdergetter.getOrThrow(Biomes.END_BARRENS));
    }

    private WorldChunkManagerTheEnd(Holder<BiomeBase> holder, Holder<BiomeBase> holder1, Holder<BiomeBase> holder2, Holder<BiomeBase> holder3, Holder<BiomeBase> holder4) {
        this.end = holder;
        this.highlands = holder1;
        this.midlands = holder2;
        this.islands = holder3;
        this.barrens = holder4;
    }

    @Override
    protected Stream<Holder<BiomeBase>> collectPossibleBiomes() {
        return Stream.of(this.end, this.highlands, this.midlands, this.islands, this.barrens);
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerTheEnd.CODEC;
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        int l = QuartPos.toBlock(i);
        int i1 = QuartPos.toBlock(j);
        int j1 = QuartPos.toBlock(k);
        int k1 = SectionPosition.blockToSectionCoord(l);
        int l1 = SectionPosition.blockToSectionCoord(j1);

        if ((long) k1 * (long) k1 + (long) l1 * (long) l1 <= 4096L) {
            return this.end;
        } else {
            int i2 = (SectionPosition.blockToSectionCoord(l) * 2 + 1) * 8;
            int j2 = (SectionPosition.blockToSectionCoord(j1) * 2 + 1) * 8;
            double d0 = climate_sampler.erosion().compute(new DensityFunction.e(i2, i1, j2));

            return d0 > 0.25D ? this.highlands : (d0 >= -0.0625D ? this.midlands : (d0 < -0.21875D ? this.islands : this.barrens));
        }
    }
}
