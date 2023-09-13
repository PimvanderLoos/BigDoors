package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.DensityFunction;

public class WorldChunkManagerTheEnd extends WorldChunkManager {

    public static final Codec<WorldChunkManagerTheEnd> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryOps.retrieveRegistry(IRegistry.BIOME_REGISTRY).forGetter((worldchunkmanagertheend) -> {
            return null;
        })).apply(instance, instance.stable(WorldChunkManagerTheEnd::new));
    });
    private final Holder<BiomeBase> end;
    private final Holder<BiomeBase> highlands;
    private final Holder<BiomeBase> midlands;
    private final Holder<BiomeBase> islands;
    private final Holder<BiomeBase> barrens;

    public WorldChunkManagerTheEnd(IRegistry<BiomeBase> iregistry) {
        this(iregistry.getOrCreateHolderOrThrow(Biomes.THE_END), iregistry.getOrCreateHolderOrThrow(Biomes.END_HIGHLANDS), iregistry.getOrCreateHolderOrThrow(Biomes.END_MIDLANDS), iregistry.getOrCreateHolderOrThrow(Biomes.SMALL_END_ISLANDS), iregistry.getOrCreateHolderOrThrow(Biomes.END_BARRENS));
    }

    private WorldChunkManagerTheEnd(Holder<BiomeBase> holder, Holder<BiomeBase> holder1, Holder<BiomeBase> holder2, Holder<BiomeBase> holder3, Holder<BiomeBase> holder4) {
        super((List) ImmutableList.of(holder, holder1, holder2, holder3, holder4));
        this.end = holder;
        this.highlands = holder1;
        this.midlands = holder2;
        this.islands = holder3;
        this.barrens = holder4;
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
