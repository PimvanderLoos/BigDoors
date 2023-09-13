package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public class WorldChunkManagerCheckerBoard extends WorldChunkManager {

    public static final Codec<WorldChunkManagerCheckerBoard> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.LIST_CODEC.fieldOf("biomes").forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.allowedBiomes;
        }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.size;
        })).apply(instance, WorldChunkManagerCheckerBoard::new);
    });
    private final HolderSet<BiomeBase> allowedBiomes;
    private final int bitShift;
    private final int size;

    public WorldChunkManagerCheckerBoard(HolderSet<BiomeBase> holderset, int i) {
        this.allowedBiomes = holderset;
        this.bitShift = i + 2;
        this.size = i;
    }

    @Override
    protected Stream<Holder<BiomeBase>> collectPossibleBiomes() {
        return this.allowedBiomes.stream();
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerCheckerBoard.CODEC;
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        return this.allowedBiomes.get(Math.floorMod((i >> this.bitShift) + (k >> this.bitShift), this.allowedBiomes.size()));
    }
}
