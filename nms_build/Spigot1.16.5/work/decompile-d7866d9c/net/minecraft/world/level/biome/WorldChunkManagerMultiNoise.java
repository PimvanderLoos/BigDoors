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

    private static final WorldChunkManagerMultiNoise.a g = new WorldChunkManagerMultiNoise.a(-7, ImmutableList.of(1.0D, 1.0D));
    public static final MapCodec<WorldChunkManagerMultiNoise> e = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.r;
        }), RecordCodecBuilder.create((instance1) -> {
            return instance1.group(BiomeBase.c.a.fieldOf("parameters").forGetter(Pair::getFirst), BiomeBase.d.fieldOf("biome").forGetter(Pair::getSecond)).apply(instance1, Pair::of);
        }).listOf().fieldOf("biomes").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.p;
        }), WorldChunkManagerMultiNoise.a.a.fieldOf("temperature_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.h;
        }), WorldChunkManagerMultiNoise.a.a.fieldOf("humidity_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.i;
        }), WorldChunkManagerMultiNoise.a.a.fieldOf("altitude_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.j;
        }), WorldChunkManagerMultiNoise.a.a.fieldOf("weirdness_noise").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.k;
        })).apply(instance, WorldChunkManagerMultiNoise::new);
    });
    public static final Codec<WorldChunkManagerMultiNoise> f = Codec.mapEither(WorldChunkManagerMultiNoise.c.a, WorldChunkManagerMultiNoise.e).xmap((either) -> {
        return (WorldChunkManagerMultiNoise) either.map(WorldChunkManagerMultiNoise.c::d, Function.identity());
    }, (worldchunkmanagermultinoise) -> {
        return (Either) worldchunkmanagermultinoise.d().map(Either::left).orElseGet(() -> {
            return Either.right(worldchunkmanagermultinoise);
        });
    }).codec();
    private final WorldChunkManagerMultiNoise.a h;
    private final WorldChunkManagerMultiNoise.a i;
    private final WorldChunkManagerMultiNoise.a j;
    private final WorldChunkManagerMultiNoise.a k;
    private final NoiseGeneratorNormal l;
    private final NoiseGeneratorNormal m;
    private final NoiseGeneratorNormal n;
    private final NoiseGeneratorNormal o;
    private final List<Pair<BiomeBase.c, Supplier<BiomeBase>>> p;
    private final boolean q;
    private final long r;
    private final Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> s;

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> optional) {
        this(i, list, WorldChunkManagerMultiNoise.g, WorldChunkManagerMultiNoise.g, WorldChunkManagerMultiNoise.g, WorldChunkManagerMultiNoise.g, optional);
    }

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a1, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a2, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a3) {
        this(i, list, worldchunkmanagermultinoise_a, worldchunkmanagermultinoise_a1, worldchunkmanagermultinoise_a2, worldchunkmanagermultinoise_a3, Optional.empty());
    }

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.c, Supplier<BiomeBase>>> list, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a1, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a2, WorldChunkManagerMultiNoise.a worldchunkmanagermultinoise_a3, Optional<Pair<IRegistry<BiomeBase>, WorldChunkManagerMultiNoise.b>> optional) {
        super(list.stream().map(Pair::getSecond));
        this.r = i;
        this.s = optional;
        this.h = worldchunkmanagermultinoise_a;
        this.i = worldchunkmanagermultinoise_a1;
        this.j = worldchunkmanagermultinoise_a2;
        this.k = worldchunkmanagermultinoise_a3;
        this.l = NoiseGeneratorNormal.a(new SeededRandom(i), worldchunkmanagermultinoise_a.a(), worldchunkmanagermultinoise_a.b());
        this.m = NoiseGeneratorNormal.a(new SeededRandom(i + 1L), worldchunkmanagermultinoise_a1.a(), worldchunkmanagermultinoise_a1.b());
        this.n = NoiseGeneratorNormal.a(new SeededRandom(i + 2L), worldchunkmanagermultinoise_a2.a(), worldchunkmanagermultinoise_a2.b());
        this.o = NoiseGeneratorNormal.a(new SeededRandom(i + 3L), worldchunkmanagermultinoise_a3.a(), worldchunkmanagermultinoise_a3.b());
        this.p = list;
        this.q = false;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerMultiNoise.f;
    }

    private Optional<WorldChunkManagerMultiNoise.c> d() {
        return this.s.map((pair) -> {
            return new WorldChunkManagerMultiNoise.c((WorldChunkManagerMultiNoise.b) pair.getSecond(), (IRegistry) pair.getFirst(), this.r);
        });
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = this.q ? j : 0;
        BiomeBase.c biomebase_c = new BiomeBase.c((float) this.l.a((double) i, (double) l, (double) k), (float) this.m.a((double) i, (double) l, (double) k), (float) this.n.a((double) i, (double) l, (double) k), (float) this.o.a((double) i, (double) l, (double) k), 0.0F);

        return (BiomeBase) this.p.stream().min(Comparator.comparing((pair) -> {
            return ((BiomeBase.c) pair.getFirst()).a(biomebase_c);
        })).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.b);
    }

    public boolean b(long i) {
        return this.r == i && this.s.isPresent() && Objects.equals(((Pair) this.s.get()).getSecond(), WorldChunkManagerMultiNoise.b.a);
    }

    public static class b {

        private static final Map<MinecraftKey, WorldChunkManagerMultiNoise.b> b = Maps.newHashMap();
        public static final WorldChunkManagerMultiNoise.b a = new WorldChunkManagerMultiNoise.b(new MinecraftKey("nether"), (worldchunkmanagermultinoise_b, iregistry, olong) -> {
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
        private final MinecraftKey c;
        private final Function3<WorldChunkManagerMultiNoise.b, IRegistry<BiomeBase>, Long, WorldChunkManagerMultiNoise> d;

        public b(MinecraftKey minecraftkey, Function3<WorldChunkManagerMultiNoise.b, IRegistry<BiomeBase>, Long, WorldChunkManagerMultiNoise> function3) {
            this.c = minecraftkey;
            this.d = function3;
            WorldChunkManagerMultiNoise.b.b.put(minecraftkey, this);
        }

        public WorldChunkManagerMultiNoise a(IRegistry<BiomeBase> iregistry, long i) {
            return (WorldChunkManagerMultiNoise) this.d.apply(this, iregistry, i);
        }
    }

    static final class c {

        public static final MapCodec<WorldChunkManagerMultiNoise.c> a = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.a.flatXmap((minecraftkey) -> {
                return (DataResult) Optional.ofNullable(WorldChunkManagerMultiNoise.b.b.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + minecraftkey);
                });
            }, (worldchunkmanagermultinoise_b) -> {
                return DataResult.success(worldchunkmanagermultinoise_b.c);
            }).fieldOf("preset").stable().forGetter(WorldChunkManagerMultiNoise.c::a), RegistryLookupCodec.a(IRegistry.ay).forGetter(WorldChunkManagerMultiNoise.c::b), Codec.LONG.fieldOf("seed").stable().forGetter(WorldChunkManagerMultiNoise.c::c)).apply(instance, instance.stable(WorldChunkManagerMultiNoise.c::new));
        });
        private final WorldChunkManagerMultiNoise.b b;
        private final IRegistry<BiomeBase> c;
        private final long d;

        private c(WorldChunkManagerMultiNoise.b worldchunkmanagermultinoise_b, IRegistry<BiomeBase> iregistry, long i) {
            this.b = worldchunkmanagermultinoise_b;
            this.c = iregistry;
            this.d = i;
        }

        public WorldChunkManagerMultiNoise.b a() {
            return this.b;
        }

        public IRegistry<BiomeBase> b() {
            return this.c;
        }

        public long c() {
            return this.d;
        }

        public WorldChunkManagerMultiNoise d() {
            return this.b.a(this.c, this.d);
        }
    }

    static class a {

        private final int b;
        private final DoubleList c;
        public static final Codec<WorldChunkManagerMultiNoise.a> a = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("firstOctave").forGetter(WorldChunkManagerMultiNoise.a::a), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(WorldChunkManagerMultiNoise.a::b)).apply(instance, WorldChunkManagerMultiNoise.a::new);
        });

        public a(int i, List<Double> list) {
            this.b = i;
            this.c = new DoubleArrayList(list);
        }

        public int a() {
            return this.b;
        }

        public DoubleList b() {
            return this.c;
        }
    }
}
