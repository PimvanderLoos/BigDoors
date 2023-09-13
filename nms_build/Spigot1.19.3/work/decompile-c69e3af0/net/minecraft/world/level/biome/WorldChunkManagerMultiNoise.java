package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class WorldChunkManagerMultiNoise extends WorldChunkManager {

    public static final MapCodec<WorldChunkManagerMultiNoise> DIRECT_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((instance1) -> {
            return instance1.group(Climate.d.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), BiomeBase.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(instance1, Pair::of);
        }).listOf()).xmap(Climate.c::new, Climate.c::values).fieldOf("biomes").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.parameters;
        })).apply(instance, WorldChunkManagerMultiNoise::new);
    });
    public static final Codec<WorldChunkManagerMultiNoise> CODEC = Codec.mapEither(WorldChunkManagerMultiNoise.b.CODEC, WorldChunkManagerMultiNoise.DIRECT_CODEC).xmap((either) -> {
        return (WorldChunkManagerMultiNoise) either.map(WorldChunkManagerMultiNoise.b::biomeSource, Function.identity());
    }, (worldchunkmanagermultinoise) -> {
        return (Either) worldchunkmanagermultinoise.preset().map(Either::left).orElseGet(() -> {
            return Either.right(worldchunkmanagermultinoise);
        });
    }).codec();
    private final Climate.c<Holder<BiomeBase>> parameters;
    private final Optional<WorldChunkManagerMultiNoise.b> preset;

    private WorldChunkManagerMultiNoise(Climate.c<Holder<BiomeBase>> climate_c) {
        this(climate_c, Optional.empty());
    }

    WorldChunkManagerMultiNoise(Climate.c<Holder<BiomeBase>> climate_c, Optional<WorldChunkManagerMultiNoise.b> optional) {
        super(climate_c.values().stream().map(Pair::getSecond));
        this.preset = optional;
        this.parameters = climate_c;
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerMultiNoise.CODEC;
    }

    private Optional<WorldChunkManagerMultiNoise.b> preset() {
        return this.preset;
    }

    public boolean stable(WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a) {
        return this.preset.isPresent() && Objects.equals(((WorldChunkManagerMultiNoise.b) this.preset.get()).preset(), worldchunkmanagermultinoise_a);
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        return this.getNoiseBiome(climate_sampler.sample(i, j, k));
    }

    @VisibleForDebug
    public Holder<BiomeBase> getNoiseBiome(Climate.h climate_h) {
        return (Holder) this.parameters.findValue(climate_h);
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

    private static record b(WorldChunkManagerMultiNoise.a preset, HolderGetter<BiomeBase> biomes) {

        public static final MapCodec<WorldChunkManagerMultiNoise.b> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
                return (DataResult) Optional.ofNullable((WorldChunkManagerMultiNoise.a) WorldChunkManagerMultiNoise.a.BY_NAME.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + minecraftkey);
                });
            }, (worldchunkmanagermultinoise_a) -> {
                return DataResult.success(worldchunkmanagermultinoise_a.name);
            }).fieldOf("preset").stable().forGetter(WorldChunkManagerMultiNoise.b::preset), RegistryOps.retrieveGetter(Registries.BIOME)).apply(instance, instance.stable(WorldChunkManagerMultiNoise.b::new));
        });

        public WorldChunkManagerMultiNoise biomeSource() {
            return this.preset.biomeSource(this, true);
        }
    }

    public static class a {

        static final Map<MinecraftKey, WorldChunkManagerMultiNoise.a> BY_NAME = Maps.newHashMap();
        public static final WorldChunkManagerMultiNoise.a NETHER = new WorldChunkManagerMultiNoise.a(new MinecraftKey("nether"), (holdergetter) -> {
            return new Climate.c<>(ImmutableList.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), holdergetter.getOrThrow(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), holdergetter.getOrThrow(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), holdergetter.getOrThrow(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), holdergetter.getOrThrow(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), holdergetter.getOrThrow(Biomes.BASALT_DELTAS))));
        });
        public static final WorldChunkManagerMultiNoise.a OVERWORLD = new WorldChunkManagerMultiNoise.a(new MinecraftKey("overworld"), (holdergetter) -> {
            Builder<Pair<Climate.d, Holder<BiomeBase>>> builder = ImmutableList.builder();

            (new OverworldBiomeBuilder()).addBiomes((pair) -> {
                Objects.requireNonNull(holdergetter);
                builder.add(pair.mapSecond(holdergetter::getOrThrow));
            });
            return new Climate.c<>(builder.build());
        });
        final MinecraftKey name;
        private final Function<HolderGetter<BiomeBase>, Climate.c<Holder<BiomeBase>>> parameterSource;

        public a(MinecraftKey minecraftkey, Function<HolderGetter<BiomeBase>, Climate.c<Holder<BiomeBase>>> function) {
            this.name = minecraftkey;
            this.parameterSource = function;
            WorldChunkManagerMultiNoise.a.BY_NAME.put(minecraftkey, this);
        }

        @VisibleForDebug
        public static Stream<Pair<MinecraftKey, WorldChunkManagerMultiNoise.a>> getPresets() {
            return WorldChunkManagerMultiNoise.a.BY_NAME.entrySet().stream().map((entry) -> {
                return Pair.of((MinecraftKey) entry.getKey(), (WorldChunkManagerMultiNoise.a) entry.getValue());
            });
        }

        WorldChunkManagerMultiNoise biomeSource(WorldChunkManagerMultiNoise.b worldchunkmanagermultinoise_b, boolean flag) {
            Climate.c<Holder<BiomeBase>> climate_c = (Climate.c) this.parameterSource.apply(worldchunkmanagermultinoise_b.biomes());

            return new WorldChunkManagerMultiNoise(climate_c, flag ? Optional.of(worldchunkmanagermultinoise_b) : Optional.empty());
        }

        public WorldChunkManagerMultiNoise biomeSource(HolderGetter<BiomeBase> holdergetter, boolean flag) {
            return this.biomeSource(new WorldChunkManagerMultiNoise.b(this, holdergetter), flag);
        }

        public WorldChunkManagerMultiNoise biomeSource(HolderGetter<BiomeBase> holdergetter) {
            return this.biomeSource(holdergetter, true);
        }

        public Stream<ResourceKey<BiomeBase>> possibleBiomes(HolderGetter<BiomeBase> holdergetter) {
            return this.biomeSource(holdergetter).possibleBiomes().stream().flatMap((holder) -> {
                return holder.unwrapKey().stream();
            });
        }
    }
}
