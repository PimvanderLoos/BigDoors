package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class WorldChunkManagerMultiNoise extends WorldChunkManager {

    private static final WorldChunkManagerMultiNoise.a DEFAULT_NOISE_PARAMETERS = new WorldChunkManagerMultiNoise.a(-7, ImmutableList.of(1.0D, 1.0D));
    public static final MapCodec<WorldChunkManagerMultiNoise> DIRECT_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.seed;
        }), RecordCodecBuilder.create((instance1) -> {
            return instance1.group(BiomeBase.c.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), BiomeBase.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(instance1, Pair::of);
        }).listOf().fieldOf("biomes").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.parameters;
        }), WorldChunkManagerMultiNoise.a.CODEC.fieldOf("temperature_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.temperatureParams;
        }), WorldChunkManagerMultiNoise.a.CODEC.fieldOf("humidity_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.humidityParams;
        }), WorldChunkManagerMultiNoise.a.CODEC.fieldOf("altitude_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.altitudeParams;
        }), WorldChunkManagerMultiNoise.a.CODEC.fieldOf("weirdness_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.weirdnessParams;
        })).apply(instance, WorldChunkManagerMultiNoise::new);
    });
    public static final Codec<WorldChunkManagerMultiNoise> CODEC = Codec.mapEither(WorldChunkManagerMultiNoise.c.CODEC, WorldChunkManagerMultiNoise.DIRECT_CODEC).xmap((either) -> {
        return (WorldChunkManagerMultiNoise) either.map(WorldChunkManagerMultiNoise.c::d, Function.identity());
    }, (worldchunkmanagermultinoise) -> {
        return (Either) worldchunkmanagermultinoise.d().map(Either::left).orElseGet(() -> {
            return Either.right(worldchunkmanagermultinoise);
        });
    }).codec();
    private final WorldChunkManagerMultiNoise.a temperatureParams;
    private final WorldChunkManagerMultiNoise.a humidityParams;
    private final WorldChunkManagerMultiNoise.a altitudeParams;
    private final WorldChunkManagerMultiNoise.a weirdnessParams;
    private final NoiseGeneratorNormal temperatureNoise;
    private final NoiseGeneratorNormal humidityNoise;
    private final NoiseGeneratorNormal altitudeNoise;
    private final NoiseGeneratorNormal weirdnessNoise;
    private final List<Pair<BiomeBase.c, Supplier<BiomeBase>>> parameters;
    private final boolean useY;
    private final long seed;
    private final Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> preset;

    public WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list) {
        this(i, list, Optional.empty());
    }

    WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> optional) {
        this(i, list, WorldChunkManagerMultiNoise.DEFAULT_NOISE_PARAMETERS, WorldChunkManagerMultiNoise.DEFAULT_NOISE_PARAMETERS, WorldChunkManagerMultiNoise.DEFAULT_NOISE_PARAMETERS, WorldChunkManagerMultiNoise.DEFAULT_NOISE_PARAMETERS, optional);
    }

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a1, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a2, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a3) {
        this(i, list, worldchunkmanagermultinoise_a, worldchunkmanagermultinoise_a1, worldchunkmanagermultinoise_a2, worldchunkmanagermultinoise_a3, Optional.empty());
    }

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a1, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a2, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a3, Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> optional) {
        super(list.stream().map(Pair::getSecond));
        this.seed = i;
        this.preset = optional;
        this.temperatureParams = worldchunkmanagermultinoise_a;
        this.humidityParams = worldchunkmanagermultinoise_a1;
        this.altitudeParams = worldchunkmanagermultinoise_a2;
        this.weirdnessParams = worldchunkmanagermultinoise_a3;
        this.temperatureNoise = NoiseGeneratorNormal.a(new SeededRandom(i), worldchunkmanagermultinoise_a.a(), worldchunkmanagermultinoise_a.b());
        this.humidityNoise = NoiseGeneratorNormal.a(new SeededRandom(i + 1L), worldchunkmanagermultinoise_a1.a(), worldchunkmanagermultinoise_a1.b());
        this.altitudeNoise = NoiseGeneratorNormal.a(new SeededRandom(i + 2L), worldchunkmanagermultinoise_a2.a(), worldchunkmanagermultinoise_a2.b());
        this.weirdnessNoise = NoiseGeneratorNormal.a(new SeededRandom(i + 3L), worldchunkmanagermultinoise_a3.a(), worldchunkmanagermultinoise_a3.b());
        this.parameters = list;
        this.useY = false;
    }

    public static WorldChunkManagerMultiNoise a(IRegistry<BiomeBase> iregistry, long i) {
        ImmutableList<Pair<BiomeBase.c, Supplier<BiomeBase>>> immutablelist = a(iregistry);
        WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a = new WorldChunkManagerMultiNoise.a(-9, new double[]{1.0D, 0.0D, 3.0D, 3.0D, 3.0D, 3.0D});
        WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a1 = new WorldChunkManagerMultiNoise.a(-7, new double[]{1.0D, 2.0D, 4.0D, 4.0D});
        WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a2 = new WorldChunkManagerMultiNoise.a(-9, new double[]{1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0D});
        WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a3 = new WorldChunkManagerMultiNoise.a(-8, new double[]{1.2D, 0.6D, 0.0D, 0.0D, 1.0D, 0.0D});

        return new WorldChunkManagerMultiNoise(i, immutablelist, worldchunkmanagermultinoise_a, worldchunkmanagermultinoise_a1, worldchunkmanagermultinoise_a2, worldchunkmanagermultinoise_a3, Optional.empty());
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerMultiNoise.CODEC;
    }

    @Override
    public WorldChunkManager a(long i) {
        return new WorldChunkManagerMultiNoise(i, this.parameters, this.temperatureParams, this.humidityParams, this.altitudeParams, this.weirdnessParams, this.preset);
    }

    private Optional<WorldChunkManagerMultiNoise.c> d() {
        return this.preset.map((pair) -> {
            return new WorldChunkManagerMultiNoise.c((WorldChunkManagerMultiNoise.b) pair.getSecond(), (IRegistry) pair.getFirst(), this.seed);
        });
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = this.useY ? j : 0;
        BiomeBase.c biomebase_c = new BiomeBase.c((float) this.temperatureNoise.a((double) i, (double) l, (double) k), (float) this.humidityNoise.a((double) i, (double) l, (double) k), (float) this.altitudeNoise.a((double) i, (double) l, (double) k), (float) this.weirdnessNoise.a((double) i, (double) l, (double) k), 0.0F);

        return (BiomeBase) this.parameters.stream().min(Comparator.comparing((pair) -> {
            return ((BiomeBase.c) pair.getFirst()).a(biomebase_c);
        })).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
    }

    public static ImmutableList<Pair<BiomeBase.c, Supplier<BiomeBase>>> a(IRegistry<BiomeBase> iregistry) {
        return ImmutableList.of(Pair.of(new BiomeBase.c(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (BiomeBase) iregistry.d(Biomes.PLAINS);
        }));
    }

    public boolean b(long i) {
        return this.seed == i && this.preset.isPresent() && Objects.equals(((Pair) this.preset.get()).getSecond(), WorldChunkManagerMultiNoise.b.NETHER);
    }

    private static class a {

        private final int firstOctave;
        private final DoubleList amplitudes;
        public static final Codec<WorldChunkManagerMultiNoise.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("firstOctave").forGetter(WorldChunkManagerMultiNoise.a::a), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(WorldChunkManagerMultiNoise.a::b)).apply(instance, WorldChunkManagerMultiNoise.a::new);
        });

        public a(int i, List<Double> list) {
            this.firstOctave = i;
            this.amplitudes = new DoubleArrayList(list);
        }

        public a(int i, double... adouble) {
            this.firstOctave = i;
            this.amplitudes = new DoubleArrayList(adouble);
        }

        public int a() {
            return this.firstOctave;
        }

        public DoubleList b() {
            return this.amplitudes;
        }
    }

    public static class b {

        static final Map<MinecraftKey, WorldChunkManagerMultiNoise.b> BY_NAME = Maps.newHashMap();
        public static final WorldChunkManagerMultiNoise.b NETHER = new WorldChunkManagerMultiNoise.b(new MinecraftKey("nether"), (worldchunkmanagermultinoise_b, iregistry, olong) -> {
            return new WorldChunkManagerMultiNoise(olong, ImmutableList.of(Pair.of(new BiomeBase.c(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.d(Biomes.NETHER_WASTES);
            }), Pair.of(new BiomeBase.c(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.d(Biomes.SOUL_SAND_VALLEY);
            }), Pair.of(new BiomeBase.c(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return (BiomeBase) iregistry.d(Biomes.CRIMSON_FOREST);
            }), Pair.of(new BiomeBase.c(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
                return (BiomeBase) iregistry.d(Biomes.WARPED_FOREST);
            }), Pair.of(new BiomeBase.c(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
                return (BiomeBase) iregistry.d(Biomes.BASALT_DELTAS);
            })), Optional.of(Pair.of(iregistry, worldchunkmanagermultinoise_b)));
        });
        final MinecraftKey name;
        private final Function3<WorldChunkManagerMultiNoise.b, IRegistry<BiomeBase>, Long, WorldChunkManagerMultiNoise> biomeSource;

        public b(MinecraftKey minecraftkey, Function3<WorldChunkManagerMultiNoise.b, IRegistry<BiomeBase>, Long, WorldChunkManagerMultiNoise> function3) {
            this.name = minecraftkey;
            this.biomeSource = function3;
            WorldChunkManagerMultiNoise.b.BY_NAME.put(minecraftkey, this);
        }

        public WorldChunkManagerMultiNoise a(IRegistry<BiomeBase> iregistry, long i) {
            return (WorldChunkManagerMultiNoise) this.biomeSource.apply(this, iregistry, i);
        }
    }

    private static final class c {

        public static final MapCodec<WorldChunkManagerMultiNoise.c> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
                return (DataResult) Optional.ofNullable((WorldChunkManagerMultiNoise.b) WorldChunkManagerMultiNoise.b.BY_NAME.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + minecraftkey);
                });
            }, (worldchunkmanagermultinoise_b) -> {
                return DataResult.success(worldchunkmanagermultinoise_b.name);
            }).fieldOf("preset").stable().forGetter(WorldChunkManagerMultiNoise.c::a), RegistryLookupCodec.a(IRegistry.BIOME_REGISTRY).forGetter(WorldChunkManagerMultiNoise.c::b), Codec.LONG.fieldOf("seed").stable().forGetter(WorldChunkManagerMultiNoise.c::c)).apply(instance, instance.stable(WorldChunkManagerMultiNoise.c::new));
        });
        private final WorldChunkManagerMultiNoise.b preset;
        private final IRegistry<BiomeBase> biomes;
        private final long seed;

        c(WorldChunkManagerMultiNoise.b worldchunkmanagermultinoise_b, IRegistry<BiomeBase> iregistry, long i) {
            this.preset = worldchunkmanagermultinoise_b;
            this.biomes = iregistry;
            this.seed = i;
        }

        public WorldChunkManagerMultiNoise.b a() {
            return this.preset;
        }

        public IRegistry<BiomeBase> b() {
            return this.biomes;
        }

        public long c() {
            return this.seed;
        }

        public WorldChunkManagerMultiNoise d() {
            return this.preset.a(this.biomes, this.seed);
        }
    }
}
