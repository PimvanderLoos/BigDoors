package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class WorldChunkManagerMultiNoise extends WorldChunkManager {

    private static final MapCodec<Holder<BiomeBase>> ENTRY_CODEC = BiomeBase.CODEC.fieldOf("biome");
    public static final MapCodec<Climate.c<Holder<BiomeBase>>> DIRECT_CODEC = Climate.c.codec(WorldChunkManagerMultiNoise.ENTRY_CODEC).fieldOf("biomes");
    private static final MapCodec<Holder<MultiNoiseBiomeSourceParameterList>> PRESET_CODEC = MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable());
    public static final Codec<WorldChunkManagerMultiNoise> CODEC = Codec.mapEither(WorldChunkManagerMultiNoise.DIRECT_CODEC, WorldChunkManagerMultiNoise.PRESET_CODEC).xmap(WorldChunkManagerMultiNoise::new, (worldchunkmanagermultinoise) -> {
        return worldchunkmanagermultinoise.parameters;
    }).codec();
    private final Either<Climate.c<Holder<BiomeBase>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    private WorldChunkManagerMultiNoise(Either<Climate.c<Holder<BiomeBase>>, Holder<MultiNoiseBiomeSourceParameterList>> either) {
        this.parameters = either;
    }

    public static WorldChunkManagerMultiNoise createFromList(Climate.c<Holder<BiomeBase>> climate_c) {
        return new WorldChunkManagerMultiNoise(Either.left(climate_c));
    }

    public static WorldChunkManagerMultiNoise createFromPreset(Holder<MultiNoiseBiomeSourceParameterList> holder) {
        return new WorldChunkManagerMultiNoise(Either.right(holder));
    }

    private Climate.c<Holder<BiomeBase>> parameters() {
        return (Climate.c) this.parameters.map((climate_c) -> {
            return climate_c;
        }, (holder) -> {
            return ((MultiNoiseBiomeSourceParameterList) holder.value()).parameters();
        });
    }

    @Override
    protected Stream<Holder<BiomeBase>> collectPossibleBiomes() {
        return this.parameters().values().stream().map(Pair::getSecond);
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerMultiNoise.CODEC;
    }

    public boolean stable(ResourceKey<MultiNoiseBiomeSourceParameterList> resourcekey) {
        Optional<Holder<MultiNoiseBiomeSourceParameterList>> optional = this.parameters.right();

        return optional.isPresent() && ((Holder) optional.get()).is(resourcekey);
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        return this.getNoiseBiome(climate_sampler.sample(i, j, k));
    }

    @VisibleForDebug
    public Holder<BiomeBase> getNoiseBiome(Climate.h climate_h) {
        return (Holder) this.parameters().findValue(climate_h);
    }

    @Override
    public void addDebugInfo(List<String> list, BlockPosition blockposition, Climate.Sampler climate_sampler) {
        int i = QuartPos.fromBlock(blockposition.getX());
        int j = QuartPos.fromBlock(blockposition.getY());
        int k = QuartPos.fromBlock(blockposition.getZ());
        Climate.h climate_h = climate_sampler.sample(i, j, k);
        float f = Climate.unquantizeCoord(climate_h.continentalness());
        float f1 = Climate.unquantizeCoord(climate_h.erosion());
        float f2 = Climate.unquantizeCoord(climate_h.temperature());
        float f3 = Climate.unquantizeCoord(climate_h.humidity());
        float f4 = Climate.unquantizeCoord(climate_h.weirdness());
        double d0 = (double) NoiseRouterData.peaksAndValleys(f4);
        OverworldBiomeBuilder overworldbiomebuilder = new OverworldBiomeBuilder();
        String s = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(d0);

        list.add("Biome builder PV: " + s + " C: " + overworldbiomebuilder.getDebugStringForContinentalness((double) f) + " E: " + overworldbiomebuilder.getDebugStringForErosion((double) f1) + " T: " + overworldbiomebuilder.getDebugStringForTemperature((double) f2) + " H: " + overworldbiomebuilder.getDebugStringForHumidity((double) f3));
    }
}
