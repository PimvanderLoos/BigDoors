package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldChunkManagerMultiNoise extends WorldChunkManager {

    public static final MapCodec<WorldChunkManagerMultiNoise> e = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.m;
        }), RecordCodecBuilder.create((instance1) -> {
            return instance1.group(BiomeBase.d.a.fieldOf("parameters").forGetter(Pair::getFirst), IRegistry.BIOME.fieldOf("biome").forGetter(Pair::getSecond)).apply(instance1, Pair::of);
        }).listOf().fieldOf("biomes").forGetter((worldchunkmanagermultinoise) -> {
            return worldchunkmanagermultinoise.k;
        })).apply(instance, WorldChunkManagerMultiNoise::new);
    });
    public static final Codec<WorldChunkManagerMultiNoise> f = Codec.mapEither(WorldChunkManagerMultiNoise.a.a, WorldChunkManagerMultiNoise.e).xmap((either) -> {
        return (WorldChunkManagerMultiNoise) either.map((pair) -> {
            return ((WorldChunkManagerMultiNoise.a) pair.getFirst()).a((Long) pair.getSecond());
        }, Function.identity());
    }, (worldchunkmanagermultinoise) -> {
        return (Either) worldchunkmanagermultinoise.n.map((worldchunkmanagermultinoise_a) -> {
            return Either.left(Pair.of(worldchunkmanagermultinoise_a, worldchunkmanagermultinoise.m));
        }).orElseGet(() -> {
            return Either.right(worldchunkmanagermultinoise);
        });
    }).codec();
    private final NoiseGeneratorNormal g;
    private final NoiseGeneratorNormal h;
    private final NoiseGeneratorNormal i;
    private final NoiseGeneratorNormal j;
    private final List<Pair<BiomeBase.d, BiomeBase>> k;
    private final boolean l;
    private final long m;
    private final Optional<WorldChunkManagerMultiNoise.a> n;

    private WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.d, BiomeBase>> list) {
        this(i, list, Optional.empty());
    }

    public WorldChunkManagerMultiNoise(long i, List<Pair<BiomeBase.d, BiomeBase>> list, Optional<WorldChunkManagerMultiNoise.a> optional) {
        super((List) list.stream().map(Pair::getSecond).collect(Collectors.toList()));
        this.m = i;
        this.n = optional;
        IntStream intstream = IntStream.rangeClosed(-7, -6);
        IntStream intstream1 = IntStream.rangeClosed(-7, -6);
        IntStream intstream2 = IntStream.rangeClosed(-7, -6);
        IntStream intstream3 = IntStream.rangeClosed(-7, -6);

        this.g = new NoiseGeneratorNormal(new SeededRandom(i), intstream);
        this.h = new NoiseGeneratorNormal(new SeededRandom(i + 1L), intstream1);
        this.i = new NoiseGeneratorNormal(new SeededRandom(i + 2L), intstream2);
        this.j = new NoiseGeneratorNormal(new SeededRandom(i + 3L), intstream3);
        this.k = list;
        this.l = false;
    }

    private static WorldChunkManagerMultiNoise d(long i) {
        ImmutableList<BiomeBase> immutablelist = ImmutableList.of(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS);

        return new WorldChunkManagerMultiNoise(i, (List) immutablelist.stream().flatMap((biomebase) -> {
            return biomebase.B().map((biomebase_d) -> {
                return Pair.of(biomebase_d, biomebase);
            });
        }).collect(ImmutableList.toImmutableList()), Optional.of(WorldChunkManagerMultiNoise.a.b));
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerMultiNoise.f;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = this.l ? j : 0;
        BiomeBase.d biomebase_d = new BiomeBase.d((float) this.g.a((double) i, (double) l, (double) k), (float) this.h.a((double) i, (double) l, (double) k), (float) this.i.a((double) i, (double) l, (double) k), (float) this.j.a((double) i, (double) l, (double) k), 0.0F);

        return (BiomeBase) this.k.stream().min(Comparator.comparing((pair) -> {
            return ((BiomeBase.d) pair.getFirst()).a(biomebase_d);
        })).map(Pair::getSecond).orElse(Biomes.THE_VOID);
    }

    public boolean b(long i) {
        return this.m == i && Objects.equals(this.n, Optional.of(WorldChunkManagerMultiNoise.a.b));
    }

    public static class a {

        private static final Map<MinecraftKey, WorldChunkManagerMultiNoise.a> c = Maps.newHashMap();
        public static final MapCodec<Pair<WorldChunkManagerMultiNoise.a, Long>> a = Codec.mapPair(MinecraftKey.a.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable(WorldChunkManagerMultiNoise.a.c.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown preset: " + minecraftkey);
            });
        }, (worldchunkmanagermultinoise_a) -> {
            return DataResult.success(worldchunkmanagermultinoise_a.d);
        }).fieldOf("preset"), Codec.LONG.fieldOf("seed")).stable();
        public static final WorldChunkManagerMultiNoise.a b = new WorldChunkManagerMultiNoise.a(new MinecraftKey("nether"), (i) -> {
            return WorldChunkManagerMultiNoise.d(i);
        });
        private final MinecraftKey d;
        private final LongFunction<WorldChunkManagerMultiNoise> e;

        public a(MinecraftKey minecraftkey, LongFunction<WorldChunkManagerMultiNoise> longfunction) {
            this.d = minecraftkey;
            this.e = longfunction;
            WorldChunkManagerMultiNoise.a.c.put(minecraftkey, this);
        }

        public WorldChunkManagerMultiNoise a(long i) {
            return (WorldChunkManagerMultiNoise) this.e.apply(i);
        }
    }
}
