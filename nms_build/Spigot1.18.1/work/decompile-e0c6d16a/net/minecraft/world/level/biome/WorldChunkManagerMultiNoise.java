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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.TerrainInfo;
import net.minecraft.world.level.levelgen.blending.Blender;

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
    private final Climate.c<Supplier<BiomeBase>> parameters;
    private final Optional<WorldChunkManagerMultiNoise.b> preset;

    private WorldChunkManagerMultiNoise(Climate.c<Supplier<BiomeBase>> climate_c) {
        this(climate_c, Optional.empty());
    }

    WorldChunkManagerMultiNoise(Climate.c<Supplier<BiomeBase>> climate_c, Optional<WorldChunkManagerMultiNoise.b> optional) {
        super(climate_c.values().stream().map(Pair::getSecond));
        this.preset = optional;
        this.parameters = climate_c;
    }

    @Override
    protected Codec<? extends WorldChunkManager> codec() {
        return WorldChunkManagerMultiNoise.CODEC;
    }

    @Override
    public WorldChunkManager withSeed(long i) {
        return this;
    }

    private Optional<WorldChunkManagerMultiNoise.b> preset() {
        return this.preset;
    }

    public boolean stable(WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a) {
        return this.preset.isPresent() && Objects.equals(((WorldChunkManagerMultiNoise.b) this.preset.get()).preset(), worldchunkmanagermultinoise_a);
    }

    @Override
    public BiomeBase getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler) {
        return this.getNoiseBiome(climate_sampler.sample(i, j, k));
    }

    @VisibleForDebug
    public BiomeBase getNoiseBiome(Climate.h climate_h) {
        return (BiomeBase) ((Supplier) this.parameters.findValue(climate_h, () -> {
            return BiomeRegistry.THE_VOID;
        })).get();
    }

    @Override
    public void addMultinoiseDebugInfo(List<String> list, BlockPosition blockposition, Climate.Sampler climate_sampler) {
        int i = QuartPos.fromBlock(blockposition.getX());
        int j = QuartPos.fromBlock(blockposition.getY());
        int k = QuartPos.fromBlock(blockposition.getZ());
        Climate.h climate_h = climate_sampler.sample(i, j, k);
        float f = Climate.unquantizeCoord(climate_h.continentalness());
        float f1 = Climate.unquantizeCoord(climate_h.erosion());
        float f2 = Climate.unquantizeCoord(climate_h.temperature());
        float f3 = Climate.unquantizeCoord(climate_h.humidity());
        float f4 = Climate.unquantizeCoord(climate_h.weirdness());
        double d0 = (double) TerrainShaper.peaksAndValleys(f4);
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        String s = decimalformat.format((double) f);

        list.add("Multinoise C: " + s + " E: " + decimalformat.format((double) f1) + " T: " + decimalformat.format((double) f2) + " H: " + decimalformat.format((double) f3) + " W: " + decimalformat.format((double) f4));
        OverworldBiomeBuilder overworldbiomebuilder = new OverworldBiomeBuilder();

        s = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(d0);
        list.add("Biome builder PV: " + s + " C: " + overworldbiomebuilder.getDebugStringForContinentalness((double) f) + " E: " + overworldbiomebuilder.getDebugStringForErosion((double) f1) + " T: " + overworldbiomebuilder.getDebugStringForTemperature((double) f2) + " H: " + overworldbiomebuilder.getDebugStringForHumidity((double) f3));
        if (climate_sampler instanceof NoiseSampler) {
            NoiseSampler noisesampler = (NoiseSampler) climate_sampler;
            TerrainInfo terraininfo = noisesampler.terrainInfo(blockposition.getX(), blockposition.getZ(), f, f4, f1, Blender.empty());

            s = decimalformat.format(d0);
            list.add("Terrain PV: " + s + " O: " + decimalformat.format(terraininfo.offset()) + " F: " + decimalformat.format(terraininfo.factor()) + " JA: " + decimalformat.format(terraininfo.jaggedness()));
        }
    }

    private static record b(WorldChunkManagerMultiNoise.a b, IRegistry<BiomeBase> c) {

        private final WorldChunkManagerMultiNoise.a preset;
        private final IRegistry<BiomeBase> biomes;
        public static final MapCodec<WorldChunkManagerMultiNoise.b> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
                return (DataResult) Optional.ofNullable((WorldChunkManagerMultiNoise.a) WorldChunkManagerMultiNoise.a.BY_NAME.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + minecraftkey);
                });
            }, (worldchunkmanagermultinoise_a) -> {
                return DataResult.success(worldchunkmanagermultinoise_a.name);
            }).fieldOf("preset").stable().forGetter(WorldChunkManagerMultiNoise.b::preset), RegistryLookupCodec.create(IRegistry.BIOME_REGISTRY).forGetter(WorldChunkManagerMultiNoise.b::biomes)).apply(instance, instance.stable(WorldChunkManagerMultiNoise.b::new));
        });

        b(WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a, IRegistry<BiomeBase> iregistry) {
            this.preset = worldchunkmanagermultinoise_a;
            this.biomes = iregistry;
        }

        public WorldChunkManagerMultiNoise biomeSource() {
            return this.preset.biomeSource(this, true);
        }

        public WorldChunkManagerMultiNoise.a preset() {
            return this.preset;
        }

        public IRegistry<BiomeBase> biomes() {
            return this.biomes;
        }
    }

    public static class a {

        static final Map<MinecraftKey, WorldChunkManagerMultiNoise.a> BY_NAME = Maps.newHashMap();
        public static final WorldChunkManagerMultiNoise.a NETHER = new WorldChunkManagerMultiNoise.a(new MinecraftKey("nether"), (iregistry) -> {
            return new Climate.c<>(ImmutableList.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.NETHER_WASTES);
            }), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.SOUL_SAND_VALLEY);
            }), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.CRIMSON_FOREST);
            }), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.WARPED_FOREST);
            }), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.BASALT_DELTAS);
            })));
        });
        public static final WorldChunkManagerMultiNoise.a OVERWORLD = new WorldChunkManagerMultiNoise.a(new MinecraftKey("overworld"), (iregistry) -> {
            Builder<Pair<Climate.d, Supplier<BiomeBase>>> builder = ImmutableList.builder();

            (new OverworldBiomeBuilder()).addBiomes((pair) -> {
                builder.add(pair.mapSecond((resourcekey) -> {
                    return () -> {
                        return (BiomeBase) iregistry.getOrThrow(resourcekey);
                    };
                }));
            });
            return new Climate.c<>(builder.build());
        });
        final MinecraftKey name;
        private final Function<IRegistry<BiomeBase>, Climate.c<Supplier<BiomeBase>>> parameterSource;

        public a(MinecraftKey minecraftkey, Function<IRegistry<BiomeBase>, Climate.c<Supplier<BiomeBase>>> function) {
            this.name = minecraftkey;
            this.parameterSource = function;
            WorldChunkManagerMultiNoise.a.BY_NAME.put(minecraftkey, this);
        }

        WorldChunkManagerMultiNoise biomeSource(WorldChunkManagerMultiNoise.b worldchunkmanagermultinoise_b, boolean flag) {
            Climate.c<Supplier<BiomeBase>> climate_c = (Climate.c) this.parameterSource.apply(worldchunkmanagermultinoise_b.biomes());

            return new WorldChunkManagerMultiNoise(climate_c, flag ? Optional.of(worldchunkmanagermultinoise_b) : Optional.empty());
        }

        public WorldChunkManagerMultiNoise biomeSource(IRegistry<BiomeBase> iregistry, boolean flag) {
            return this.biomeSource(new WorldChunkManagerMultiNoise.b(this, iregistry), flag);
        }

        public WorldChunkManagerMultiNoise biomeSource(IRegistry<BiomeBase> iregistry) {
            return this.biomeSource(iregistry, true);
        }
    }
}
