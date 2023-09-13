package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.NoiseSampler;

public class Climate {

    private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
    private static final float QUANTIZATION_FACTOR = 10000.0F;
    @VisibleForTesting
    protected static final int PARAMETER_COUNT = 7;

    public Climate() {}

    public static Climate.h target(float f, float f1, float f2, float f3, float f4, float f5) {
        return new Climate.h(quantizeCoord(f), quantizeCoord(f1), quantizeCoord(f2), quantizeCoord(f3), quantizeCoord(f4), quantizeCoord(f5));
    }

    public static Climate.d parameters(float f, float f1, float f2, float f3, float f4, float f5, float f6) {
        return new Climate.d(Climate.b.point(f), Climate.b.point(f1), Climate.b.point(f2), Climate.b.point(f3), Climate.b.point(f4), Climate.b.point(f5), quantizeCoord(f6));
    }

    public static Climate.d parameters(Climate.b climate_b, Climate.b climate_b1, Climate.b climate_b2, Climate.b climate_b3, Climate.b climate_b4, Climate.b climate_b5, float f) {
        return new Climate.d(climate_b, climate_b1, climate_b2, climate_b3, climate_b4, climate_b5, quantizeCoord(f));
    }

    public static long quantizeCoord(float f) {
        return (long) (f * 10000.0F);
    }

    public static float unquantizeCoord(long i) {
        return (float) i / 10000.0F;
    }

    public static BlockPosition findSpawnPosition(List<Climate.d> list, NoiseSampler noisesampler) {
        return (new Climate.g(list, noisesampler)).result.location();
    }

    public static record h(long a, long b, long c, long d, long e, long f) {

        final long temperature;
        final long humidity;
        final long continentalness;
        final long erosion;
        final long depth;
        final long weirdness;

        public h(long i, long j, long k, long l, long i1, long j1) {
            this.temperature = i;
            this.humidity = j;
            this.continentalness = k;
            this.erosion = l;
            this.depth = i1;
            this.weirdness = j1;
        }

        @VisibleForTesting
        protected long[] toParameterArray() {
            return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
        }

        public long temperature() {
            return this.temperature;
        }

        public long humidity() {
            return this.humidity;
        }

        public long continentalness() {
            return this.continentalness;
        }

        public long erosion() {
            return this.erosion;
        }

        public long depth() {
            return this.depth;
        }

        public long weirdness() {
            return this.weirdness;
        }
    }

    public static record d(Climate.b b, Climate.b c, Climate.b d, Climate.b e, Climate.b f, Climate.b g, long h) {

        private final Climate.b temperature;
        private final Climate.b humidity;
        private final Climate.b continentalness;
        private final Climate.b erosion;
        private final Climate.b depth;
        private final Climate.b weirdness;
        private final long offset;
        public static final Codec<Climate.d> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Climate.b.CODEC.fieldOf("temperature").forGetter((climate_d) -> {
                return climate_d.temperature;
            }), Climate.b.CODEC.fieldOf("humidity").forGetter((climate_d) -> {
                return climate_d.humidity;
            }), Climate.b.CODEC.fieldOf("continentalness").forGetter((climate_d) -> {
                return climate_d.continentalness;
            }), Climate.b.CODEC.fieldOf("erosion").forGetter((climate_d) -> {
                return climate_d.erosion;
            }), Climate.b.CODEC.fieldOf("depth").forGetter((climate_d) -> {
                return climate_d.depth;
            }), Climate.b.CODEC.fieldOf("weirdness").forGetter((climate_d) -> {
                return climate_d.weirdness;
            }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((climate_d) -> {
                return climate_d.offset;
            })).apply(instance, Climate.d::new);
        });

        public d(Climate.b climate_b, Climate.b climate_b1, Climate.b climate_b2, Climate.b climate_b3, Climate.b climate_b4, Climate.b climate_b5, long i) {
            this.temperature = climate_b;
            this.humidity = climate_b1;
            this.continentalness = climate_b2;
            this.erosion = climate_b3;
            this.depth = climate_b4;
            this.weirdness = climate_b5;
            this.offset = i;
        }

        long fitness(Climate.h climate_h) {
            return MathHelper.square(this.temperature.distance(climate_h.temperature)) + MathHelper.square(this.humidity.distance(climate_h.humidity)) + MathHelper.square(this.continentalness.distance(climate_h.continentalness)) + MathHelper.square(this.erosion.distance(climate_h.erosion)) + MathHelper.square(this.depth.distance(climate_h.depth)) + MathHelper.square(this.weirdness.distance(climate_h.weirdness)) + MathHelper.square(this.offset);
        }

        protected List<Climate.b> parameterSpace() {
            return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Climate.b(this.offset, this.offset));
        }

        public Climate.b temperature() {
            return this.temperature;
        }

        public Climate.b humidity() {
            return this.humidity;
        }

        public Climate.b continentalness() {
            return this.continentalness;
        }

        public Climate.b erosion() {
            return this.erosion;
        }

        public Climate.b depth() {
            return this.depth;
        }

        public Climate.b weirdness() {
            return this.weirdness;
        }

        public long offset() {
            return this.offset;
        }
    }

    public static record b(long b, long c) {

        private final long min;
        private final long max;
        public static final Codec<Climate.b> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (ofloat, ofloat1) -> {
            return ofloat.compareTo(ofloat1) > 0 ? DataResult.error("Cannon construct interval, min > max (" + ofloat + " > " + ofloat1 + ")") : DataResult.success(new Climate.b(Climate.quantizeCoord(ofloat), Climate.quantizeCoord(ofloat1)));
        }, (climate_b) -> {
            return Climate.unquantizeCoord(climate_b.min());
        }, (climate_b) -> {
            return Climate.unquantizeCoord(climate_b.max());
        });

        public b(long i, long j) {
            this.min = i;
            this.max = j;
        }

        public static Climate.b point(float f) {
            return span(f, f);
        }

        public static Climate.b span(float f, float f1) {
            if (f > f1) {
                throw new IllegalArgumentException("min > max: " + f + " " + f1);
            } else {
                return new Climate.b(Climate.quantizeCoord(f), Climate.quantizeCoord(f1));
            }
        }

        public static Climate.b span(Climate.b climate_b, Climate.b climate_b1) {
            if (climate_b.min() > climate_b1.max()) {
                throw new IllegalArgumentException("min > max: " + climate_b + " " + climate_b1);
            } else {
                return new Climate.b(climate_b.min(), climate_b1.max());
            }
        }

        public String toString() {
            return this.min == this.max ? String.format("%d", this.min) : String.format("[%d-%d]", this.min, this.max);
        }

        public long distance(long i) {
            long j = i - this.max;
            long k = this.min - i;

            return j > 0L ? j : Math.max(k, 0L);
        }

        public long distance(Climate.b climate_b) {
            long i = climate_b.min() - this.max;
            long j = this.min - climate_b.max();

            return i > 0L ? i : Math.max(j, 0L);
        }

        public Climate.b span(@Nullable Climate.b climate_b) {
            return climate_b == null ? this : new Climate.b(Math.min(this.min, climate_b.min()), Math.max(this.max, climate_b.max()));
        }

        public long min() {
            return this.min;
        }

        public long max() {
            return this.max;
        }
    }

    private static class g {

        Climate.g.a result;

        g(List<Climate.d> list, NoiseSampler noisesampler) {
            this.result = getSpawnPositionAndFitness(list, noisesampler, 0, 0);
            this.radialSearch(list, noisesampler, 2048.0F, 512.0F);
            this.radialSearch(list, noisesampler, 512.0F, 32.0F);
        }

        private void radialSearch(List<Climate.d> list, NoiseSampler noisesampler, float f, float f1) {
            float f2 = 0.0F;
            float f3 = f1;
            BlockPosition blockposition = this.result.location();

            while (f3 <= f) {
                int i = blockposition.getX() + (int) (Math.sin((double) f2) * (double) f3);
                int j = blockposition.getZ() + (int) (Math.cos((double) f2) * (double) f3);
                Climate.g.a climate_g_a = getSpawnPositionAndFitness(list, noisesampler, i, j);

                if (climate_g_a.fitness() < this.result.fitness()) {
                    this.result = climate_g_a;
                }

                f2 += f1 / f3;
                if ((double) f2 > 6.283185307179586D) {
                    f2 = 0.0F;
                    f3 += f1;
                }
            }

        }

        private static Climate.g.a getSpawnPositionAndFitness(List<Climate.d> list, NoiseSampler noisesampler, int i, int j) {
            double d0 = MathHelper.square(2500.0D);
            boolean flag = true;
            long k = (long) ((double) MathHelper.square(10000.0F) * Math.pow((double) (MathHelper.square((long) i) + MathHelper.square((long) j)) / d0, 2.0D));
            Climate.h climate_h = noisesampler.sample(QuartPos.fromBlock(i), 0, QuartPos.fromBlock(j));
            Climate.h climate_h1 = new Climate.h(climate_h.temperature(), climate_h.humidity(), climate_h.continentalness(), climate_h.erosion(), 0L, climate_h.weirdness());
            long l = Long.MAX_VALUE;

            Climate.d climate_d;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); l = Math.min(l, climate_d.fitness(climate_h1))) {
                climate_d = (Climate.d) iterator.next();
            }

            return new Climate.g.a(new BlockPosition(i, 0, j), k + l);
        }

        private static record a(BlockPosition a, long b) {

            private final BlockPosition location;
            private final long fitness;

            a(BlockPosition blockposition, long i) {
                this.location = blockposition;
                this.fitness = i;
            }

            public BlockPosition location() {
                return this.location;
            }

            public long fitness() {
                return this.fitness;
            }
        }
    }

    public interface Sampler {

        Climate.h sample(int i, int j, int k);

        default BlockPosition findSpawnPosition() {
            return BlockPosition.ZERO;
        }
    }

    public static class c<T> {

        private final List<Pair<Climate.d, T>> values;
        private final Climate.e<T> index;

        public c(List<Pair<Climate.d, T>> list) {
            this.values = list;
            this.index = Climate.e.create(list);
        }

        public List<Pair<Climate.d, T>> values() {
            return this.values;
        }

        public T findValue(Climate.h climate_h, T t0) {
            return this.findValueIndex(climate_h);
        }

        @VisibleForTesting
        public T findValueBruteForce(Climate.h climate_h, T t0) {
            long i = Long.MAX_VALUE;
            T t1 = t0;
            Iterator iterator = this.values().iterator();

            while (iterator.hasNext()) {
                Pair<Climate.d, T> pair = (Pair) iterator.next();
                long j = ((Climate.d) pair.getFirst()).fitness(climate_h);

                if (j < i) {
                    i = j;
                    t1 = pair.getSecond();
                }
            }

            return t1;
        }

        public T findValueIndex(Climate.h climate_h) {
            return this.findValueIndex(climate_h, Climate.e.b::distance);
        }

        protected T findValueIndex(Climate.h climate_h, Climate.a<T> climate_a) {
            return this.index.search(climate_h, climate_a);
        }
    }

    protected static final class e<T> {

        private static final int CHILDREN_PER_NODE = 10;
        private final Climate.e.b<T> root;
        private final ThreadLocal<Climate.e.a<T>> lastResult = new ThreadLocal();

        private e(Climate.e.b<T> climate_e_b) {
            this.root = climate_e_b;
        }

        public static <T> Climate.e<T> create(List<Pair<Climate.d, T>> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            } else {
                int i = ((Climate.d) ((Pair) list.get(0)).getFirst()).parameterSpace().size();

                if (i != 7) {
                    throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
                } else {
                    List<Climate.e.a<T>> list1 = (List) list.stream().map((pair) -> {
                        return new Climate.e.a<>((Climate.d) pair.getFirst(), pair.getSecond());
                    }).collect(Collectors.toCollection(ArrayList::new));

                    return new Climate.e<>(build(i, list1));
                }
            }
        }

        private static <T> Climate.e.b<T> build(int i, List<? extends Climate.e.b<T>> list) {
            if (list.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            } else if (list.size() == 1) {
                return (Climate.e.b) list.get(0);
            } else if (list.size() <= 10) {
                list.sort(Comparator.comparingLong((climate_e_b) -> {
                    long j = 0L;

                    for (int k = 0; k < i; ++k) {
                        Climate.b climate_b = climate_e_b.parameterSpace[k];

                        j += Math.abs((climate_b.min() + climate_b.max()) / 2L);
                    }

                    return j;
                }));
                return new Climate.e.c<>(list);
            } else {
                long j = Long.MAX_VALUE;
                int k = -1;
                List<Climate.e.c<T>> list1 = null;

                for (int l = 0; l < i; ++l) {
                    sort(list, i, l, false);
                    List<Climate.e.c<T>> list2 = bucketize(list);
                    long i1 = 0L;

                    Climate.e.c climate_e_c;

                    for (Iterator iterator = list2.iterator(); iterator.hasNext(); i1 += cost(climate_e_c.parameterSpace)) {
                        climate_e_c = (Climate.e.c) iterator.next();
                    }

                    if (j > i1) {
                        j = i1;
                        k = l;
                        list1 = list2;
                    }
                }

                sort(list1, i, k, true);
                return new Climate.e.c<>((List) list1.stream().map((climate_e_c1) -> {
                    return build(i, Arrays.asList(climate_e_c1.children));
                }).collect(Collectors.toList()));
            }
        }

        private static <T> void sort(List<? extends Climate.e.b<T>> list, int i, int j, boolean flag) {
            Comparator<Climate.e.b<T>> comparator = comparator(j, flag);

            for (int k = 1; k < i; ++k) {
                comparator = comparator.thenComparing(comparator((j + k) % i, flag));
            }

            list.sort(comparator);
        }

        private static <T> Comparator<Climate.e.b<T>> comparator(int i, boolean flag) {
            return Comparator.comparingLong((climate_e_b) -> {
                Climate.b climate_b = climate_e_b.parameterSpace[i];
                long j = (climate_b.min() + climate_b.max()) / 2L;

                return flag ? Math.abs(j) : j;
            });
        }

        private static <T> List<Climate.e.c<T>> bucketize(List<? extends Climate.e.b<T>> list) {
            List<Climate.e.c<T>> list1 = Lists.newArrayList();
            List<Climate.e.b<T>> list2 = Lists.newArrayList();
            int i = (int) Math.pow(10.0D, Math.floor(Math.log((double) list.size() - 0.01D) / Math.log(10.0D)));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Climate.e.b<T> climate_e_b = (Climate.e.b) iterator.next();

                list2.add(climate_e_b);
                if (list2.size() >= i) {
                    list1.add(new Climate.e.c<>(list2));
                    list2 = Lists.newArrayList();
                }
            }

            if (!list2.isEmpty()) {
                list1.add(new Climate.e.c<>(list2));
            }

            return list1;
        }

        private static long cost(Climate.b[] aclimate_b) {
            long i = 0L;
            Climate.b[] aclimate_b1 = aclimate_b;
            int j = aclimate_b.length;

            for (int k = 0; k < j; ++k) {
                Climate.b climate_b = aclimate_b1[k];

                i += Math.abs(climate_b.max() - climate_b.min());
            }

            return i;
        }

        static <T> List<Climate.b> buildParameterSpace(List<? extends Climate.e.b<T>> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            } else {
                boolean flag = true;
                List<Climate.b> list1 = Lists.newArrayList();

                for (int i = 0; i < 7; ++i) {
                    list1.add((Object) null);
                }

                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Climate.e.b<T> climate_e_b = (Climate.e.b) iterator.next();

                    for (int j = 0; j < 7; ++j) {
                        list1.set(j, climate_e_b.parameterSpace[j].span((Climate.b) list1.get(j)));
                    }
                }

                return list1;
            }
        }

        public T search(Climate.h climate_h, Climate.a<T> climate_a) {
            long[] along = climate_h.toParameterArray();
            Climate.e.a<T> climate_e_a = this.root.search(along, (Climate.e.a) this.lastResult.get(), climate_a);

            this.lastResult.set(climate_e_a);
            return climate_e_a.value;
        }

        abstract static class b<T> {

            protected final Climate.b[] parameterSpace;

            protected b(List<Climate.b> list) {
                this.parameterSpace = (Climate.b[]) list.toArray(new Climate.b[0]);
            }

            protected abstract Climate.e.a<T> search(long[] along, @Nullable Climate.e.a<T> climate_e_a, Climate.a<T> climate_a);

            protected long distance(long[] along) {
                long i = 0L;

                for (int j = 0; j < 7; ++j) {
                    i += MathHelper.square(this.parameterSpace[j].distance(along[j]));
                }

                return i;
            }

            public String toString() {
                return Arrays.toString(this.parameterSpace);
            }
        }

        private static final class c<T> extends Climate.e.b<T> {

            final Climate.e.b<T>[] children;

            protected c(List<? extends Climate.e.b<T>> list) {
                this(Climate.e.buildParameterSpace(list), list);
            }

            protected c(List<Climate.b> list, List<? extends Climate.e.b<T>> list1) {
                super(list);
                this.children = (Climate.e.b[]) list1.toArray(new Climate.e.b[0]);
            }

            @Override
            protected Climate.e.a<T> search(long[] along, @Nullable Climate.e.a<T> climate_e_a, Climate.a<T> climate_a) {
                long i = climate_e_a == null ? Long.MAX_VALUE : climate_a.distance(climate_e_a, along);
                Climate.e.a<T> climate_e_a1 = climate_e_a;
                Climate.e.b[] aclimate_e_b = this.children;
                int j = aclimate_e_b.length;

                for (int k = 0; k < j; ++k) {
                    Climate.e.b<T> climate_e_b = aclimate_e_b[k];
                    long l = climate_a.distance(climate_e_b, along);

                    if (i > l) {
                        Climate.e.a<T> climate_e_a2 = climate_e_b.search(along, climate_e_a1, climate_a);
                        long i1 = climate_e_b == climate_e_a2 ? l : climate_a.distance(climate_e_a2, along);

                        if (i > i1) {
                            i = i1;
                            climate_e_a1 = climate_e_a2;
                        }
                    }
                }

                return climate_e_a1;
            }
        }

        private static final class a<T> extends Climate.e.b<T> {

            final T value;

            a(Climate.d climate_d, T t0) {
                super(climate_d.parameterSpace());
                this.value = t0;
            }

            @Override
            protected Climate.e.a<T> search(long[] along, @Nullable Climate.e.a<T> climate_e_a, Climate.a<T> climate_a) {
                return this;
            }
        }
    }

    interface a<T> {

        long distance(Climate.e.b<T> climate_e_b, long[] along);
    }
}
