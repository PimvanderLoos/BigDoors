package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3Handler;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.slf4j.Logger;

public final class DensityFunctions {

    private static final Codec<DensityFunction> CODEC = IRegistry.DENSITY_FUNCTION_TYPES.byNameCodec().dispatch(DensityFunction::codec, Function.identity());
    protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0D;
    static final Codec<Double> NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0D, 1000000.0D);
    public static final Codec<DensityFunction> DIRECT_CODEC = Codec.either(DensityFunctions.NOISE_VALUE_CODEC, DensityFunctions.CODEC).xmap((either) -> {
        return (DensityFunction) either.map(DensityFunctions::constant, Function.identity());
    }, (densityfunction) -> {
        if (densityfunction instanceof DensityFunctions.h) {
            DensityFunctions.h densityfunctions_h = (DensityFunctions.h) densityfunction;

            return Either.left(densityfunctions_h.value());
        } else {
            return Either.right(densityfunction);
        }
    });

    public static Codec<? extends DensityFunction> bootstrap(IRegistry<Codec<? extends DensityFunction>> iregistry) {
        register(iregistry, "blend_alpha", DensityFunctions.d.CODEC);
        register(iregistry, "blend_offset", DensityFunctions.f.CODEC);
        register(iregistry, "beardifier", DensityFunctions.b.CODEC);
        register(iregistry, "old_blended_noise", BlendedNoise.CODEC);
        DensityFunctions.l.a[] adensityfunctions_l_a = DensityFunctions.l.a.values();
        int i = adensityfunctions_l_a.length;

        int j;

        for (j = 0; j < i; ++j) {
            DensityFunctions.l.a densityfunctions_l_a = adensityfunctions_l_a[j];

            register(iregistry, densityfunctions_l_a.getSerializedName(), densityfunctions_l_a.codec);
        }

        register(iregistry, "noise", DensityFunctions.o.CODEC);
        register(iregistry, "end_islands", DensityFunctions.i.CODEC);
        register(iregistry, "weird_scaled_sampler", DensityFunctions.ab.CODEC);
        register(iregistry, "shifted_noise", DensityFunctions.v.CODEC);
        register(iregistry, "range_choice", DensityFunctions.q.CODEC);
        register(iregistry, "shift_a", DensityFunctions.s.CODEC);
        register(iregistry, "shift_b", DensityFunctions.t.CODEC);
        register(iregistry, "shift", DensityFunctions.r.CODEC);
        register(iregistry, "blend_density", DensityFunctions.e.CODEC);
        register(iregistry, "clamp", DensityFunctions.g.CODEC);
        DensityFunctions.k.a[] adensityfunctions_k_a = DensityFunctions.k.a.values();

        i = adensityfunctions_k_a.length;

        for (j = 0; j < i; ++j) {
            DensityFunctions.k.a densityfunctions_k_a = adensityfunctions_k_a[j];

            register(iregistry, densityfunctions_k_a.getSerializedName(), densityfunctions_k_a.codec);
        }

        register(iregistry, "slide", DensityFunctions.w.CODEC);
        DensityFunctions.aa.a[] adensityfunctions_aa_a = DensityFunctions.aa.a.values();

        i = adensityfunctions_aa_a.length;

        for (j = 0; j < i; ++j) {
            DensityFunctions.aa.a densityfunctions_aa_a = adensityfunctions_aa_a[j];

            register(iregistry, densityfunctions_aa_a.getSerializedName(), densityfunctions_aa_a.codec);
        }

        register(iregistry, "spline", DensityFunctions.x.CODEC);
        register(iregistry, "terrain_shaper_spline", DensityFunctions.y.CODEC);
        register(iregistry, "constant", DensityFunctions.h.CODEC);
        return register(iregistry, "y_clamped_gradient", DensityFunctions.ac.CODEC);
    }

    private static Codec<? extends DensityFunction> register(IRegistry<Codec<? extends DensityFunction>> iregistry, String s, Codec<? extends DensityFunction> codec) {
        return (Codec) IRegistry.register(iregistry, s, codec);
    }

    static <A, O> Codec<O> singleArgumentCodec(Codec<A> codec, Function<A, O> function, Function<O, A> function1) {
        return codec.fieldOf("argument").xmap(function, function1).codec();
    }

    static <O> Codec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> function, Function<O, DensityFunction> function1) {
        return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, function, function1);
    }

    static <O> Codec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> bifunction, Function<O, DensityFunction> function, Function<O, DensityFunction> function1) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(function), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(function1)).apply(instance, bifunction);
        });
    }

    static <O> Codec<O> makeCodec(MapCodec<O> mapcodec) {
        return mapcodec.codec();
    }

    private DensityFunctions() {}

    public static DensityFunction interpolated(DensityFunction densityfunction) {
        return new DensityFunctions.l(DensityFunctions.l.a.Interpolated, densityfunction);
    }

    public static DensityFunction flatCache(DensityFunction densityfunction) {
        return new DensityFunctions.l(DensityFunctions.l.a.FlatCache, densityfunction);
    }

    public static DensityFunction cache2d(DensityFunction densityfunction) {
        return new DensityFunctions.l(DensityFunctions.l.a.Cache2D, densityfunction);
    }

    public static DensityFunction cacheOnce(DensityFunction densityfunction) {
        return new DensityFunctions.l(DensityFunctions.l.a.CacheOnce, densityfunction);
    }

    public static DensityFunction cacheAllInCell(DensityFunction densityfunction) {
        return new DensityFunctions.l(DensityFunctions.l.a.CacheAllInCell, densityfunction);
    }

    public static DensityFunction mappedNoise(Holder<NoiseGeneratorNormal.a> holder, @Deprecated double d0, double d1, double d2, double d3) {
        return mapFromUnitTo(new DensityFunctions.o(holder, (NoiseGeneratorNormal) null, d0, d1), d2, d3);
    }

    public static DensityFunction mappedNoise(Holder<NoiseGeneratorNormal.a> holder, double d0, double d1, double d2) {
        return mappedNoise(holder, 1.0D, d0, d1, d2);
    }

    public static DensityFunction mappedNoise(Holder<NoiseGeneratorNormal.a> holder, double d0, double d1) {
        return mappedNoise(holder, 1.0D, 1.0D, d0, d1);
    }

    public static DensityFunction shiftedNoise2d(DensityFunction densityfunction, DensityFunction densityfunction1, double d0, Holder<NoiseGeneratorNormal.a> holder) {
        return new DensityFunctions.v(densityfunction, zero(), densityfunction1, d0, 0.0D, holder, (NoiseGeneratorNormal) null);
    }

    public static DensityFunction noise(Holder<NoiseGeneratorNormal.a> holder) {
        return noise(holder, 1.0D, 1.0D);
    }

    public static DensityFunction noise(Holder<NoiseGeneratorNormal.a> holder, double d0, double d1) {
        return new DensityFunctions.o(holder, (NoiseGeneratorNormal) null, d0, d1);
    }

    public static DensityFunction noise(Holder<NoiseGeneratorNormal.a> holder, double d0) {
        return noise(holder, 1.0D, d0);
    }

    public static DensityFunction rangeChoice(DensityFunction densityfunction, double d0, double d1, DensityFunction densityfunction1, DensityFunction densityfunction2) {
        return new DensityFunctions.q(densityfunction, d0, d1, densityfunction1, densityfunction2);
    }

    public static DensityFunction shiftA(Holder<NoiseGeneratorNormal.a> holder) {
        return new DensityFunctions.s(holder, (NoiseGeneratorNormal) null);
    }

    public static DensityFunction shiftB(Holder<NoiseGeneratorNormal.a> holder) {
        return new DensityFunctions.t(holder, (NoiseGeneratorNormal) null);
    }

    public static DensityFunction shift(Holder<NoiseGeneratorNormal.a> holder) {
        return new DensityFunctions.r(holder, (NoiseGeneratorNormal) null);
    }

    public static DensityFunction blendDensity(DensityFunction densityfunction) {
        return new DensityFunctions.e(densityfunction);
    }

    public static DensityFunction endIslands(long i) {
        return new DensityFunctions.i(i);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction densityfunction, Holder<NoiseGeneratorNormal.a> holder, DensityFunctions.ab.a densityfunctions_ab_a) {
        return new DensityFunctions.ab(densityfunction, holder, (NoiseGeneratorNormal) null, densityfunctions_ab_a);
    }

    public static DensityFunction slide(NoiseSettings noisesettings, DensityFunction densityfunction) {
        return new DensityFunctions.w(noisesettings, densityfunction);
    }

    public static DensityFunction add(DensityFunction densityfunction, DensityFunction densityfunction1) {
        return DensityFunctions.aa.create(DensityFunctions.aa.a.ADD, densityfunction, densityfunction1);
    }

    public static DensityFunction mul(DensityFunction densityfunction, DensityFunction densityfunction1) {
        return DensityFunctions.aa.create(DensityFunctions.aa.a.MUL, densityfunction, densityfunction1);
    }

    public static DensityFunction min(DensityFunction densityfunction, DensityFunction densityfunction1) {
        return DensityFunctions.aa.create(DensityFunctions.aa.a.MIN, densityfunction, densityfunction1);
    }

    public static DensityFunction max(DensityFunction densityfunction, DensityFunction densityfunction1) {
        return DensityFunctions.aa.create(DensityFunctions.aa.a.MAX, densityfunction, densityfunction1);
    }

    public static DensityFunction terrainShaperSpline(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunctions.y.b densityfunctions_y_b, double d0, double d1) {
        return new DensityFunctions.y(densityfunction, densityfunction1, densityfunction2, (TerrainShaper) null, densityfunctions_y_b, d0, d1);
    }

    public static DensityFunction zero() {
        return DensityFunctions.h.ZERO;
    }

    public static DensityFunction constant(double d0) {
        return new DensityFunctions.h(d0);
    }

    public static DensityFunction yClampedGradient(int i, int j, double d0, double d1) {
        return new DensityFunctions.ac(i, j, d0, d1);
    }

    public static DensityFunction map(DensityFunction densityfunction, DensityFunctions.k.a densityfunctions_k_a) {
        return DensityFunctions.k.create(densityfunctions_k_a, densityfunction);
    }

    private static DensityFunction mapFromUnitTo(DensityFunction densityfunction, double d0, double d1) {
        double d2 = (d0 + d1) * 0.5D;
        double d3 = (d1 - d0) * 0.5D;

        return add(constant(d2), mul(constant(d3), densityfunction));
    }

    public static DensityFunction blendAlpha() {
        return DensityFunctions.d.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return DensityFunctions.f.INSTANCE;
    }

    public static DensityFunction lerp(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2) {
        DensityFunction densityfunction3 = cacheOnce(densityfunction);
        DensityFunction densityfunction4 = add(mul(densityfunction3, constant(-1.0D)), constant(1.0D));

        return add(mul(densityfunction1, densityfunction4), mul(densityfunction2, densityfunction3));
    }

    protected static enum d implements DensityFunction.c {

        INSTANCE;

        public static final Codec<DensityFunction> CODEC = Codec.unit(DensityFunctions.d.INSTANCE);

        private d() {}

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return 1.0D;
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            Arrays.fill(adouble, 1.0D);
        }

        @Override
        public double minValue() {
            return 1.0D;
        }

        @Override
        public double maxValue() {
            return 1.0D;
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.d.CODEC;
        }
    }

    protected static enum f implements DensityFunction.c {

        INSTANCE;

        public static final Codec<DensityFunction> CODEC = Codec.unit(DensityFunctions.f.INSTANCE);

        private f() {}

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return 0.0D;
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            Arrays.fill(adouble, 0.0D);
        }

        @Override
        public double minValue() {
            return 0.0D;
        }

        @Override
        public double maxValue() {
            return 0.0D;
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.f.CODEC;
        }
    }

    protected static enum b implements DensityFunctions.c {

        INSTANCE;

        private b() {}

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return 0.0D;
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            Arrays.fill(adouble, 0.0D);
        }

        @Override
        public double minValue() {
            return 0.0D;
        }

        @Override
        public double maxValue() {
            return 0.0D;
        }
    }

    protected static record l(DensityFunctions.l.a a, DensityFunction e) implements DensityFunctions.m {

        private final DensityFunctions.l.a type;
        private final DensityFunction wrapped;

        protected l(DensityFunctions.l.a densityfunctions_l_a, DensityFunction densityfunction) {
            this.type = densityfunctions_l_a;
            this.wrapped = densityfunction;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.wrapped.compute(densityfunction_b);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.wrapped.fillArray(adouble, densityfunction_a);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.l(this.type, this.wrapped.mapAll(densityfunction_e)));
        }

        @Override
        public double minValue() {
            return this.wrapped.minValue();
        }

        @Override
        public double maxValue() {
            return this.wrapped.maxValue();
        }

        @Override
        public DensityFunctions.l.a type() {
            return this.type;
        }

        @Override
        public DensityFunction wrapped() {
            return this.wrapped;
        }

        static enum a implements INamable {

            Interpolated("interpolated"), FlatCache("flat_cache"), Cache2D("cache_2d"), CacheOnce("cache_once"), CacheAllInCell("cache_all_in_cell");

            private final String name;
            final Codec<DensityFunctions.m> codec = DensityFunctions.singleFunctionArgumentCodec((densityfunction) -> {
                return new DensityFunctions.l(this, densityfunction);
            }, DensityFunctions.m::wrapped);

            private a(String s) {
                this.name = s;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    protected static record o(Holder<NoiseGeneratorNormal.a> f, @Nullable NoiseGeneratorNormal g, double h, double i) implements DensityFunction.c {

        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal noise;
        /** @deprecated */
        @Deprecated
        private final double xzScale;
        private final double yScale;
        public static final MapCodec<DensityFunctions.o> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(NoiseGeneratorNormal.a.CODEC.fieldOf("noise").forGetter(DensityFunctions.o::noiseData), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.o::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.o::yScale)).apply(instance, DensityFunctions.o::createUnseeded);
        });
        public static final Codec<DensityFunctions.o> CODEC = DensityFunctions.makeCodec(DensityFunctions.o.DATA_CODEC);

        protected o(Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal, @Deprecated double d0, double d1) {
            this.noiseData = holder;
            this.noise = noisegeneratornormal;
            this.xzScale = d0;
            this.yScale = d1;
        }

        public static DensityFunctions.o createUnseeded(Holder<NoiseGeneratorNormal.a> holder, @Deprecated double d0, double d1) {
            return new DensityFunctions.o(holder, (NoiseGeneratorNormal) null, d0, d1);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.noise == null ? 0.0D : this.noise.getValue((double) densityfunction_b.blockX() * this.xzScale, (double) densityfunction_b.blockY() * this.yScale, (double) densityfunction_b.blockZ() * this.xzScale);
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise == null ? 2.0D : this.noise.maxValue();
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.o.CODEC;
        }

        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        public NoiseGeneratorNormal noise() {
            return this.noise;
        }

        /** @deprecated */
        @Deprecated
        public double xzScale() {
            return this.xzScale;
        }

        public double yScale() {
            return this.yScale;
        }
    }

    protected static final class i implements DensityFunction.c {

        public static final Codec<DensityFunctions.i> CODEC = Codec.unit(new DensityFunctions.i(0L));
        final NoiseGenerator3Handler islandNoise;

        public i(long i) {
            LegacyRandomSource legacyrandomsource = new LegacyRandomSource(i);

            legacyrandomsource.consumeCount(17292);
            this.islandNoise = new NoiseGenerator3Handler(legacyrandomsource);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return ((double) WorldChunkManagerTheEnd.getHeightValue(this.islandNoise, densityfunction_b.blockX() / 8, densityfunction_b.blockZ() / 8) - 8.0D) / 128.0D;
        }

        @Override
        public double minValue() {
            return -0.84375D;
        }

        @Override
        public double maxValue() {
            return 0.5625D;
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.i.CODEC;
        }
    }

    protected static record ab(DensityFunction e, Holder<NoiseGeneratorNormal.a> f, @Nullable NoiseGeneratorNormal g, DensityFunctions.ab.a h) implements DensityFunctions.z {

        private final DensityFunction input;
        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal noise;
        private final DensityFunctions.ab.a rarityValueMapper;
        private static final MapCodec<DensityFunctions.ab> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.ab::input), NoiseGeneratorNormal.a.CODEC.fieldOf("noise").forGetter(DensityFunctions.ab::noiseData), DensityFunctions.ab.a.CODEC.fieldOf("rarity_value_mapper").forGetter(DensityFunctions.ab::rarityValueMapper)).apply(instance, DensityFunctions.ab::createUnseeded);
        });
        public static final Codec<DensityFunctions.ab> CODEC = DensityFunctions.makeCodec(DensityFunctions.ab.DATA_CODEC);

        protected ab(DensityFunction densityfunction, Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal, DensityFunctions.ab.a densityfunctions_ab_a) {
            this.input = densityfunction;
            this.noiseData = holder;
            this.noise = noisegeneratornormal;
            this.rarityValueMapper = densityfunctions_ab_a;
        }

        public static DensityFunctions.ab createUnseeded(DensityFunction densityfunction, Holder<NoiseGeneratorNormal.a> holder, DensityFunctions.ab.a densityfunctions_ab_a) {
            return new DensityFunctions.ab(densityfunction, holder, (NoiseGeneratorNormal) null, densityfunctions_ab_a);
        }

        @Override
        public double transform(DensityFunction.b densityfunction_b, double d0) {
            if (this.noise == null) {
                return 0.0D;
            } else {
                double d1 = this.rarityValueMapper.mapper.get(d0);

                return d1 * Math.abs(this.noise.getValue((double) densityfunction_b.blockX() / d1, (double) densityfunction_b.blockY() / d1, (double) densityfunction_b.blockZ() / d1));
            }
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            this.input.mapAll(densityfunction_e);
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.ab(this.input.mapAll(densityfunction_e), this.noiseData, this.noise, this.rarityValueMapper));
        }

        @Override
        public double minValue() {
            return 0.0D;
        }

        @Override
        public double maxValue() {
            return this.rarityValueMapper.maxRarity * (this.noise == null ? 2.0D : this.noise.maxValue());
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.ab.CODEC;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }

        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        public NoiseGeneratorNormal noise() {
            return this.noise;
        }

        public DensityFunctions.ab.a rarityValueMapper() {
            return this.rarityValueMapper;
        }

        public static enum a implements INamable {

            TYPE1("type_1", NoiseRouterData.a::getSpaghettiRarity3D, 2.0D), TYPE2("type_2", NoiseRouterData.a::getSphaghettiRarity2D, 3.0D);

            private static final Map<String, DensityFunctions.ab.a> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(DensityFunctions.ab.a::getSerializedName, (densityfunctions_ab_a) -> {
                return densityfunctions_ab_a;
            }));
            public static final Codec<DensityFunctions.ab.a> CODEC;
            private final String name;
            final Double2DoubleFunction mapper;
            final double maxRarity;

            private a(String s, Double2DoubleFunction double2doublefunction, double d0) {
                this.name = s;
                this.mapper = double2doublefunction;
                this.maxRarity = d0;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }

            static {
                Supplier supplier = DensityFunctions.ab.a::values;
                Map map = DensityFunctions.ab.a.BY_NAME;

                Objects.requireNonNull(map);
                CODEC = INamable.fromEnum(supplier, map::get);
            }
        }
    }

    protected static record v(DensityFunction e, DensityFunction f, DensityFunction g, double h, double i, Holder<NoiseGeneratorNormal.a> j, @Nullable NoiseGeneratorNormal k) implements DensityFunction {

        private final DensityFunction shiftX;
        private final DensityFunction shiftY;
        private final DensityFunction shiftZ;
        private final double xzScale;
        private final double yScale;
        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal noise;
        private static final MapCodec<DensityFunctions.v> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(DensityFunctions.v::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(DensityFunctions.v::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(DensityFunctions.v::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.v::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.v::yScale), NoiseGeneratorNormal.a.CODEC.fieldOf("noise").forGetter(DensityFunctions.v::noiseData)).apply(instance, DensityFunctions.v::createUnseeded);
        });
        public static final Codec<DensityFunctions.v> CODEC = DensityFunctions.makeCodec(DensityFunctions.v.DATA_CODEC);

        protected v(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, double d0, double d1, Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal) {
            this.shiftX = densityfunction;
            this.shiftY = densityfunction1;
            this.shiftZ = densityfunction2;
            this.xzScale = d0;
            this.yScale = d1;
            this.noiseData = holder;
            this.noise = noisegeneratornormal;
        }

        public static DensityFunctions.v createUnseeded(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, double d0, double d1, Holder<NoiseGeneratorNormal.a> holder) {
            return new DensityFunctions.v(densityfunction, densityfunction1, densityfunction2, d0, d1, holder, (NoiseGeneratorNormal) null);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            if (this.noise == null) {
                return 0.0D;
            } else {
                double d0 = (double) densityfunction_b.blockX() * this.xzScale + this.shiftX.compute(densityfunction_b);
                double d1 = (double) densityfunction_b.blockY() * this.yScale + this.shiftY.compute(densityfunction_b);
                double d2 = (double) densityfunction_b.blockZ() * this.xzScale + this.shiftZ.compute(densityfunction_b);

                return this.noise.getValue(d0, d1, d2);
            }
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.v(this.shiftX.mapAll(densityfunction_e), this.shiftY.mapAll(densityfunction_e), this.shiftZ.mapAll(densityfunction_e), this.xzScale, this.yScale, this.noiseData, this.noise));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise == null ? 2.0D : this.noise.maxValue();
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.v.CODEC;
        }

        public DensityFunction shiftX() {
            return this.shiftX;
        }

        public DensityFunction shiftY() {
            return this.shiftY;
        }

        public DensityFunction shiftZ() {
            return this.shiftZ;
        }

        public double xzScale() {
            return this.xzScale;
        }

        public double yScale() {
            return this.yScale;
        }

        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        public NoiseGeneratorNormal noise() {
            return this.noise;
        }
    }

    private static record q(DensityFunction f, double g, double h, DensityFunction i, DensityFunction j) implements DensityFunction {

        private final DensityFunction input;
        private final double minInclusive;
        private final double maxExclusive;
        private final DensityFunction whenInRange;
        private final DensityFunction whenOutOfRange;
        public static final MapCodec<DensityFunctions.q> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.q::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(DensityFunctions.q::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(DensityFunctions.q::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(DensityFunctions.q::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(DensityFunctions.q::whenOutOfRange)).apply(instance, DensityFunctions.q::new);
        });
        public static final Codec<DensityFunctions.q> CODEC = DensityFunctions.makeCodec(DensityFunctions.q.DATA_CODEC);

        q(DensityFunction densityfunction, double d0, double d1, DensityFunction densityfunction1, DensityFunction densityfunction2) {
            this.input = densityfunction;
            this.minInclusive = d0;
            this.maxExclusive = d1;
            this.whenInRange = densityfunction1;
            this.whenOutOfRange = densityfunction2;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            double d0 = this.input.compute(densityfunction_b);

            return d0 >= this.minInclusive && d0 < this.maxExclusive ? this.whenInRange.compute(densityfunction_b) : this.whenOutOfRange.compute(densityfunction_b);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.input.fillArray(adouble, densityfunction_a);

            for (int i = 0; i < adouble.length; ++i) {
                double d0 = adouble[i];

                if (d0 >= this.minInclusive && d0 < this.maxExclusive) {
                    adouble[i] = this.whenInRange.compute(densityfunction_a.forIndex(i));
                } else {
                    adouble[i] = this.whenOutOfRange.compute(densityfunction_a.forIndex(i));
                }
            }

        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.q(this.input.mapAll(densityfunction_e), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(densityfunction_e), this.whenOutOfRange.mapAll(densityfunction_e)));
        }

        @Override
        public double minValue() {
            return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
        }

        @Override
        public double maxValue() {
            return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.q.CODEC;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minInclusive() {
            return this.minInclusive;
        }

        public double maxExclusive() {
            return this.maxExclusive;
        }

        public DensityFunction whenInRange() {
            return this.whenInRange;
        }

        public DensityFunction whenOutOfRange() {
            return this.whenOutOfRange;
        }
    }

    protected static record s(Holder<NoiseGeneratorNormal.a> a, @Nullable NoiseGeneratorNormal e) implements DensityFunctions.u {

        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal offsetNoise;
        static final Codec<DensityFunctions.s> CODEC = DensityFunctions.singleArgumentCodec(NoiseGeneratorNormal.a.CODEC, (holder) -> {
            return new DensityFunctions.s(holder, (NoiseGeneratorNormal) null);
        }, DensityFunctions.s::noiseData);

        protected s(Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal) {
            this.noiseData = holder;
            this.offsetNoise = noisegeneratornormal;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.compute((double) densityfunction_b.blockX(), 0.0D, (double) densityfunction_b.blockZ());
        }

        @Override
        public DensityFunctions.u withNewNoise(NoiseGeneratorNormal noisegeneratornormal) {
            return new DensityFunctions.s(this.noiseData, noisegeneratornormal);
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.s.CODEC;
        }

        @Override
        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        @Override
        public NoiseGeneratorNormal offsetNoise() {
            return this.offsetNoise;
        }
    }

    protected static record t(Holder<NoiseGeneratorNormal.a> a, @Nullable NoiseGeneratorNormal e) implements DensityFunctions.u {

        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal offsetNoise;
        static final Codec<DensityFunctions.t> CODEC = DensityFunctions.singleArgumentCodec(NoiseGeneratorNormal.a.CODEC, (holder) -> {
            return new DensityFunctions.t(holder, (NoiseGeneratorNormal) null);
        }, DensityFunctions.t::noiseData);

        protected t(Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal) {
            this.noiseData = holder;
            this.offsetNoise = noisegeneratornormal;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.compute((double) densityfunction_b.blockZ(), (double) densityfunction_b.blockX(), 0.0D);
        }

        @Override
        public DensityFunctions.u withNewNoise(NoiseGeneratorNormal noisegeneratornormal) {
            return new DensityFunctions.t(this.noiseData, noisegeneratornormal);
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.t.CODEC;
        }

        @Override
        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        @Override
        public NoiseGeneratorNormal offsetNoise() {
            return this.offsetNoise;
        }
    }

    private static record r(Holder<NoiseGeneratorNormal.a> a, @Nullable NoiseGeneratorNormal e) implements DensityFunctions.u {

        private final Holder<NoiseGeneratorNormal.a> noiseData;
        @Nullable
        private final NoiseGeneratorNormal offsetNoise;
        static final Codec<DensityFunctions.r> CODEC = DensityFunctions.singleArgumentCodec(NoiseGeneratorNormal.a.CODEC, (holder) -> {
            return new DensityFunctions.r(holder, (NoiseGeneratorNormal) null);
        }, DensityFunctions.r::noiseData);

        r(Holder<NoiseGeneratorNormal.a> holder, @Nullable NoiseGeneratorNormal noisegeneratornormal) {
            this.noiseData = holder;
            this.offsetNoise = noisegeneratornormal;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.compute((double) densityfunction_b.blockX(), (double) densityfunction_b.blockY(), (double) densityfunction_b.blockZ());
        }

        @Override
        public DensityFunctions.u withNewNoise(NoiseGeneratorNormal noisegeneratornormal) {
            return new DensityFunctions.r(this.noiseData, noisegeneratornormal);
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.r.CODEC;
        }

        @Override
        public Holder<NoiseGeneratorNormal.a> noiseData() {
            return this.noiseData;
        }

        @Nullable
        @Override
        public NoiseGeneratorNormal offsetNoise() {
            return this.offsetNoise;
        }
    }

    private static record e(DensityFunction a) implements DensityFunctions.z {

        private final DensityFunction input;
        static final Codec<DensityFunctions.e> CODEC = DensityFunctions.singleFunctionArgumentCodec(DensityFunctions.e::new, DensityFunctions.e::input);

        e(DensityFunction densityfunction) {
            this.input = densityfunction;
        }

        @Override
        public double transform(DensityFunction.b densityfunction_b, double d0) {
            return densityfunction_b.getBlender().blendDensity(densityfunction_b, d0);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.e(this.input.mapAll(densityfunction_e)));
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.e.CODEC;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }
    }

    protected static record g(DensityFunction e, double f, double g) implements DensityFunctions.p {

        private final DensityFunction input;
        private final double minValue;
        private final double maxValue;
        private static final MapCodec<DensityFunctions.g> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(DensityFunctions.g::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(DensityFunctions.g::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(DensityFunctions.g::maxValue)).apply(instance, DensityFunctions.g::new);
        });
        public static final Codec<DensityFunctions.g> CODEC = DensityFunctions.makeCodec(DensityFunctions.g.DATA_CODEC);

        protected g(DensityFunction densityfunction, double d0, double d1) {
            this.input = densityfunction;
            this.minValue = d0;
            this.maxValue = d1;
        }

        @Override
        public double transform(double d0) {
            return MathHelper.clamp(d0, this.minValue, this.maxValue);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return new DensityFunctions.g(this.input.mapAll(densityfunction_e), this.minValue, this.maxValue);
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.g.CODEC;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }
    }

    protected static record k(DensityFunctions.k.a a, DensityFunction e, double f, double g) implements DensityFunctions.p {

        private final DensityFunctions.k.a type;
        private final DensityFunction input;
        private final double minValue;
        private final double maxValue;

        protected k(DensityFunctions.k.a densityfunctions_k_a, DensityFunction densityfunction, double d0, double d1) {
            this.type = densityfunctions_k_a;
            this.input = densityfunction;
            this.minValue = d0;
            this.maxValue = d1;
        }

        public static DensityFunctions.k create(DensityFunctions.k.a densityfunctions_k_a, DensityFunction densityfunction) {
            double d0 = densityfunction.minValue();
            double d1 = transform(densityfunctions_k_a, d0);
            double d2 = transform(densityfunctions_k_a, densityfunction.maxValue());

            return densityfunctions_k_a != DensityFunctions.k.a.ABS && densityfunctions_k_a != DensityFunctions.k.a.SQUARE ? new DensityFunctions.k(densityfunctions_k_a, densityfunction, d1, d2) : new DensityFunctions.k(densityfunctions_k_a, densityfunction, Math.max(0.0D, d0), Math.max(d1, d2));
        }

        private static double transform(DensityFunctions.k.a densityfunctions_k_a, double d0) {
            double d1;

            switch (densityfunctions_k_a) {
                case ABS:
                    d1 = Math.abs(d0);
                    break;
                case SQUARE:
                    d1 = d0 * d0;
                    break;
                case CUBE:
                    d1 = d0 * d0 * d0;
                    break;
                case HALF_NEGATIVE:
                    d1 = d0 > 0.0D ? d0 : d0 * 0.5D;
                    break;
                case QUARTER_NEGATIVE:
                    d1 = d0 > 0.0D ? d0 : d0 * 0.25D;
                    break;
                case SQUEEZE:
                    double d2 = MathHelper.clamp(d0, -1.0D, 1.0D);

                    d1 = d2 / 2.0D - d2 * d2 * d2 / 24.0D;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return d1;
        }

        @Override
        public double transform(double d0) {
            return transform(this.type, d0);
        }

        @Override
        public DensityFunctions.k mapAll(DensityFunction.e densityfunction_e) {
            return create(this.type, this.input.mapAll(densityfunction_e));
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return this.type.codec;
        }

        public DensityFunctions.k.a type() {
            return this.type;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }

        static enum a implements INamable {

            ABS("abs"), SQUARE("square"), CUBE("cube"), HALF_NEGATIVE("half_negative"), QUARTER_NEGATIVE("quarter_negative"), SQUEEZE("squeeze");

            private final String name;
            final Codec<DensityFunctions.k> codec = DensityFunctions.singleFunctionArgumentCodec((densityfunction) -> {
                return DensityFunctions.k.create(this, densityfunction);
            }, DensityFunctions.k::input);

            private a(String s) {
                this.name = s;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    protected static record w(@Nullable NoiseSettings e, DensityFunction f) implements DensityFunctions.z {

        @Nullable
        private final NoiseSettings settings;
        private final DensityFunction input;
        public static final Codec<DensityFunctions.w> CODEC = DensityFunctions.singleFunctionArgumentCodec((densityfunction) -> {
            return new DensityFunctions.w((NoiseSettings) null, densityfunction);
        }, DensityFunctions.w::input);

        protected w(@Nullable NoiseSettings noisesettings, DensityFunction densityfunction) {
            this.settings = noisesettings;
            this.input = densityfunction;
        }

        @Override
        public double transform(DensityFunction.b densityfunction_b, double d0) {
            return this.settings == null ? d0 : NoiseRouterData.applySlide(this.settings, d0, (double) densityfunction_b.blockY());
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.w(this.settings, this.input.mapAll(densityfunction_e)));
        }

        @Override
        public double minValue() {
            return this.settings == null ? this.input.minValue() : Math.min(this.input.minValue(), Math.min(this.settings.bottomSlideSettings().target(), this.settings.topSlideSettings().target()));
        }

        @Override
        public double maxValue() {
            return this.settings == null ? this.input.maxValue() : Math.max(this.input.maxValue(), Math.max(this.settings.bottomSlideSettings().target(), this.settings.topSlideSettings().target()));
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.w.CODEC;
        }

        @Nullable
        public NoiseSettings settings() {
            return this.settings;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }
    }

    interface aa extends DensityFunction {

        Logger LOGGER = LogUtils.getLogger();

        static DensityFunctions.aa create(DensityFunctions.aa.a densityfunctions_aa_a, DensityFunction densityfunction, DensityFunction densityfunction1) {
            double d0 = densityfunction.minValue();
            double d1 = densityfunction1.minValue();
            double d2 = densityfunction.maxValue();
            double d3 = densityfunction1.maxValue();

            if (densityfunctions_aa_a == DensityFunctions.aa.a.MIN || densityfunctions_aa_a == DensityFunctions.aa.a.MAX) {
                boolean flag = d0 >= d3;
                boolean flag1 = d1 >= d2;

                if (flag || flag1) {
                    DensityFunctions.aa.LOGGER.warn("Creating a " + densityfunctions_aa_a + " function between two non-overlapping inputs: " + densityfunction + " and " + densityfunction1);
                }
            }

            double d4;

            switch (densityfunctions_aa_a) {
                case ADD:
                    d4 = d0 + d1;
                    break;
                case MAX:
                    d4 = Math.max(d0, d1);
                    break;
                case MIN:
                    d4 = Math.min(d0, d1);
                    break;
                case MUL:
                    d4 = d0 > 0.0D && d1 > 0.0D ? d0 * d1 : (d2 < 0.0D && d3 < 0.0D ? d2 * d3 : Math.min(d0 * d3, d2 * d1));
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            double d5 = d4;

            switch (densityfunctions_aa_a) {
                case ADD:
                    d4 = d2 + d3;
                    break;
                case MAX:
                    d4 = Math.max(d2, d3);
                    break;
                case MIN:
                    d4 = Math.min(d2, d3);
                    break;
                case MUL:
                    d4 = d0 > 0.0D && d1 > 0.0D ? d2 * d3 : (d2 < 0.0D && d3 < 0.0D ? d0 * d1 : Math.max(d0 * d1, d2 * d3));
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            double d6 = d4;

            if (densityfunctions_aa_a == DensityFunctions.aa.a.MUL || densityfunctions_aa_a == DensityFunctions.aa.a.ADD) {
                DensityFunctions.h densityfunctions_h;

                if (densityfunction instanceof DensityFunctions.h) {
                    densityfunctions_h = (DensityFunctions.h) densityfunction;
                    return new DensityFunctions.n(densityfunctions_aa_a == DensityFunctions.aa.a.ADD ? DensityFunctions.n.a.ADD : DensityFunctions.n.a.MUL, densityfunction1, d5, d6, densityfunctions_h.value);
                }

                if (densityfunction1 instanceof DensityFunctions.h) {
                    densityfunctions_h = (DensityFunctions.h) densityfunction1;
                    return new DensityFunctions.n(densityfunctions_aa_a == DensityFunctions.aa.a.ADD ? DensityFunctions.n.a.ADD : DensityFunctions.n.a.MUL, densityfunction, d5, d6, densityfunctions_h.value);
                }
            }

            return new DensityFunctions.a(densityfunctions_aa_a, densityfunction, densityfunction1, d5, d6);
        }

        DensityFunctions.aa.a type();

        DensityFunction argument1();

        DensityFunction argument2();

        @Override
        default Codec<? extends DensityFunction> codec() {
            return this.type().codec;
        }

        public static enum a implements INamable {

            ADD("add"), MUL("mul"), MIN("min"), MAX("max");

            final Codec<DensityFunctions.aa> codec = DensityFunctions.doubleFunctionArgumentCodec((densityfunction, densityfunction1) -> {
                return DensityFunctions.aa.create(this, densityfunction, densityfunction1);
            }, DensityFunctions.aa::argument1, DensityFunctions.aa::argument2);
            private final String name;

            private a(String s) {
                this.name = s;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }

    public static record x(CubicSpline<TerrainShaper.d> e, double f, double g) implements DensityFunction {

        private final CubicSpline<TerrainShaper.d> spline;
        private final double minValue;
        private final double maxValue;
        private static final MapCodec<DensityFunctions.x> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(TerrainShaper.SPLINE_CUSTOM_CODEC.fieldOf("spline").forGetter(DensityFunctions.x::spline), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_value").forGetter(DensityFunctions.x::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_value").forGetter(DensityFunctions.x::maxValue)).apply(instance, DensityFunctions.x::new);
        });
        public static final Codec<DensityFunctions.x> CODEC = DensityFunctions.makeCodec(DensityFunctions.x.DATA_CODEC);

        public x(CubicSpline<TerrainShaper.d> cubicspline, double d0, double d1) {
            this.spline = cubicspline;
            this.minValue = d0;
            this.maxValue = d1;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return MathHelper.clamp((double) this.spline.apply(TerrainShaper.makePoint(densityfunction_b)), this.minValue, this.maxValue);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.x(this.spline.mapAll((tofloatfunction) -> {
                Object object;

                if (tofloatfunction instanceof TerrainShaper.b) {
                    TerrainShaper.b terrainshaper_b = (TerrainShaper.b) tofloatfunction;

                    object = terrainshaper_b.mapAll(densityfunction_e);
                } else {
                    object = tofloatfunction;
                }

                return (ToFloatFunction) object;
            }), this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.x.CODEC;
        }

        public CubicSpline<TerrainShaper.d> spline() {
            return this.spline;
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }
    }

    /** @deprecated */
    @Deprecated
    public static record y(DensityFunction e, DensityFunction f, DensityFunction g, @Nullable TerrainShaper h, DensityFunctions.y.b i, double j, double k) implements DensityFunction {

        private final DensityFunction continentalness;
        private final DensityFunction erosion;
        private final DensityFunction weirdness;
        @Nullable
        private final TerrainShaper shaper;
        private final DensityFunctions.y.b spline;
        private final double minValue;
        private final double maxValue;
        private static final MapCodec<DensityFunctions.y> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("continentalness").forGetter(DensityFunctions.y::continentalness), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("erosion").forGetter(DensityFunctions.y::erosion), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("weirdness").forGetter(DensityFunctions.y::weirdness), DensityFunctions.y.b.CODEC.fieldOf("spline").forGetter(DensityFunctions.y::spline), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_value").forGetter(DensityFunctions.y::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_value").forGetter(DensityFunctions.y::maxValue)).apply(instance, DensityFunctions.y::createUnseeded);
        });
        public static final Codec<DensityFunctions.y> CODEC = DensityFunctions.makeCodec(DensityFunctions.y.DATA_CODEC);

        public y(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, @Nullable TerrainShaper terrainshaper, DensityFunctions.y.b densityfunctions_y_b, double d0, double d1) {
            this.continentalness = densityfunction;
            this.erosion = densityfunction1;
            this.weirdness = densityfunction2;
            this.shaper = terrainshaper;
            this.spline = densityfunctions_y_b;
            this.minValue = d0;
            this.maxValue = d1;
        }

        public static DensityFunctions.y createUnseeded(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunctions.y.b densityfunctions_y_b, double d0, double d1) {
            return new DensityFunctions.y(densityfunction, densityfunction1, densityfunction2, (TerrainShaper) null, densityfunctions_y_b, d0, d1);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.shaper == null ? 0.0D : MathHelper.clamp((double) this.spline.spline.apply(this.shaper, TerrainShaper.makePoint((float) this.continentalness.compute(densityfunction_b), (float) this.erosion.compute(densityfunction_b), (float) this.weirdness.compute(densityfunction_b))), this.minValue, this.maxValue);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            for (int i = 0; i < adouble.length; ++i) {
                adouble[i] = this.compute(densityfunction_a.forIndex(i));
            }

        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.y(this.continentalness.mapAll(densityfunction_e), this.erosion.mapAll(densityfunction_e), this.weirdness.mapAll(densityfunction_e), this.shaper, this.spline, this.minValue, this.maxValue));
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.y.CODEC;
        }

        public DensityFunction continentalness() {
            return this.continentalness;
        }

        public DensityFunction erosion() {
            return this.erosion;
        }

        public DensityFunction weirdness() {
            return this.weirdness;
        }

        @Nullable
        public TerrainShaper shaper() {
            return this.shaper;
        }

        public DensityFunctions.y.b spline() {
            return this.spline;
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }

        public static enum b implements INamable {

            OFFSET("offset", TerrainShaper::offset), FACTOR("factor", TerrainShaper::factor), JAGGEDNESS("jaggedness", TerrainShaper::jaggedness);

            private static final Map<String, DensityFunctions.y.b> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(DensityFunctions.y.b::getSerializedName, (densityfunctions_y_b) -> {
                return densityfunctions_y_b;
            }));
            public static final Codec<DensityFunctions.y.b> CODEC;
            private final String name;
            final DensityFunctions.y.a spline;

            private b(String s, DensityFunctions.y.a densityfunctions_y_a) {
                this.name = s;
                this.spline = densityfunctions_y_a;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }

            static {
                Supplier supplier = DensityFunctions.y.b::values;
                Map map = DensityFunctions.y.b.BY_NAME;

                Objects.requireNonNull(map);
                CODEC = INamable.fromEnum(supplier, map::get);
            }
        }

        interface a {

            float apply(TerrainShaper terrainshaper, TerrainShaper.c terrainshaper_c);
        }
    }

    private static record h(double a) implements DensityFunction.c {

        final double value;
        static final Codec<DensityFunctions.h> CODEC = DensityFunctions.singleArgumentCodec(DensityFunctions.NOISE_VALUE_CODEC, DensityFunctions.h::new, DensityFunctions.h::value);
        static final DensityFunctions.h ZERO = new DensityFunctions.h(0.0D);

        h(double d0) {
            this.value = d0;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return this.value;
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            Arrays.fill(adouble, this.value);
        }

        @Override
        public double minValue() {
            return this.value;
        }

        @Override
        public double maxValue() {
            return this.value;
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.h.CODEC;
        }

        public double value() {
            return this.value;
        }
    }

    private static record ac(int e, int f, double g, double h) implements DensityFunction.c {

        private final int fromY;
        private final int toY;
        private final double fromValue;
        private final double toValue;
        private static final MapCodec<DensityFunctions.ac> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.intRange(DimensionManager.MIN_Y * 2, DimensionManager.MAX_Y * 2).fieldOf("from_y").forGetter(DensityFunctions.ac::fromY), Codec.intRange(DimensionManager.MIN_Y * 2, DimensionManager.MAX_Y * 2).fieldOf("to_y").forGetter(DensityFunctions.ac::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(DensityFunctions.ac::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(DensityFunctions.ac::toValue)).apply(instance, DensityFunctions.ac::new);
        });
        public static final Codec<DensityFunctions.ac> CODEC = DensityFunctions.makeCodec(DensityFunctions.ac.DATA_CODEC);

        ac(int i, int j, double d0, double d1) {
            this.fromY = i;
            this.toY = j;
            this.fromValue = d0;
            this.toValue = d1;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return MathHelper.clampedMap((double) densityfunction_b.blockY(), (double) this.fromY, (double) this.toY, this.fromValue, this.toValue);
        }

        @Override
        public double minValue() {
            return Math.min(this.fromValue, this.toValue);
        }

        @Override
        public double maxValue() {
            return Math.max(this.fromValue, this.toValue);
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            return DensityFunctions.ac.CODEC;
        }

        public int fromY() {
            return this.fromY;
        }

        public int toY() {
            return this.toY;
        }

        public double fromValue() {
            return this.fromValue;
        }

        public double toValue() {
            return this.toValue;
        }
    }

    private static record a(DensityFunctions.aa.a e, DensityFunction f, DensityFunction g, double h, double i) implements DensityFunctions.aa {

        private final DensityFunctions.aa.a type;
        private final DensityFunction argument1;
        private final DensityFunction argument2;
        private final double minValue;
        private final double maxValue;

        a(DensityFunctions.aa.a densityfunctions_aa_a, DensityFunction densityfunction, DensityFunction densityfunction1, double d0, double d1) {
            this.type = densityfunctions_aa_a;
            this.argument1 = densityfunction;
            this.argument2 = densityfunction1;
            this.minValue = d0;
            this.maxValue = d1;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            double d0 = this.argument1.compute(densityfunction_b);
            double d1;

            switch (this.type) {
                case ADD:
                    d1 = d0 + this.argument2.compute(densityfunction_b);
                    break;
                case MAX:
                    d1 = d0 > this.argument2.maxValue() ? d0 : Math.max(d0, this.argument2.compute(densityfunction_b));
                    break;
                case MIN:
                    d1 = d0 < this.argument2.minValue() ? d0 : Math.min(d0, this.argument2.compute(densityfunction_b));
                    break;
                case MUL:
                    d1 = d0 == 0.0D ? 0.0D : d0 * this.argument2.compute(densityfunction_b);
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return d1;
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.argument1.fillArray(adouble, densityfunction_a);
            double d0;
            double d1;
            int i;

            switch (this.type) {
                case ADD:
                    double[] adouble1 = new double[adouble.length];

                    this.argument2.fillArray(adouble1, densityfunction_a);

                    for (int j = 0; j < adouble.length; ++j) {
                        adouble[j] += adouble1[j];
                    }

                    return;
                case MAX:
                    d0 = this.argument2.maxValue();

                    for (i = 0; i < adouble.length; ++i) {
                        d1 = adouble[i];
                        adouble[i] = d1 > d0 ? d1 : Math.max(d1, this.argument2.compute(densityfunction_a.forIndex(i)));
                    }

                    return;
                case MIN:
                    d0 = this.argument2.minValue();

                    for (i = 0; i < adouble.length; ++i) {
                        d1 = adouble[i];
                        adouble[i] = d1 < d0 ? d1 : Math.min(d1, this.argument2.compute(densityfunction_a.forIndex(i)));
                    }

                    return;
                case MUL:
                    for (int k = 0; k < adouble.length; ++k) {
                        double d2 = adouble[k];

                        adouble[k] = d2 == 0.0D ? 0.0D : d2 * this.argument2.compute(densityfunction_a.forIndex(k));
                    }
            }

        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(DensityFunctions.aa.create(this.type, this.argument1.mapAll(densityfunction_e), this.argument2.mapAll(densityfunction_e)));
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }

        @Override
        public DensityFunctions.aa.a type() {
            return this.type;
        }

        @Override
        public DensityFunction argument1() {
            return this.argument1;
        }

        @Override
        public DensityFunction argument2() {
            return this.argument2;
        }
    }

    private static record n(DensityFunctions.n.a e, DensityFunction f, double g, double h, double i) implements DensityFunctions.aa, DensityFunctions.p {

        private final DensityFunctions.n.a specificType;
        private final DensityFunction input;
        private final double minValue;
        private final double maxValue;
        private final double argument;

        n(DensityFunctions.n.a densityfunctions_n_a, DensityFunction densityfunction, double d0, double d1, double d2) {
            this.specificType = densityfunctions_n_a;
            this.input = densityfunction;
            this.minValue = d0;
            this.maxValue = d1;
            this.argument = d2;
        }

        @Override
        public DensityFunctions.aa.a type() {
            return this.specificType == DensityFunctions.n.a.MUL ? DensityFunctions.aa.a.MUL : DensityFunctions.aa.a.ADD;
        }

        @Override
        public DensityFunction argument1() {
            return DensityFunctions.constant(this.argument);
        }

        @Override
        public DensityFunction argument2() {
            return this.input;
        }

        @Override
        public double transform(double d0) {
            double d1;

            switch (this.specificType) {
                case MUL:
                    d1 = d0 * this.argument;
                    break;
                case ADD:
                    d1 = d0 + this.argument;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return d1;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            DensityFunction densityfunction = this.input.mapAll(densityfunction_e);
            double d0 = densityfunction.minValue();
            double d1 = densityfunction.maxValue();
            double d2;
            double d3;

            if (this.specificType == DensityFunctions.n.a.ADD) {
                d2 = d0 + this.argument;
                d3 = d1 + this.argument;
            } else if (this.argument >= 0.0D) {
                d2 = d0 * this.argument;
                d3 = d1 * this.argument;
            } else {
                d2 = d1 * this.argument;
                d3 = d0 * this.argument;
            }

            return new DensityFunctions.n(this.specificType, densityfunction, d2, d3, this.argument);
        }

        public DensityFunctions.n.a specificType() {
            return this.specificType;
        }

        @Override
        public DensityFunction input() {
            return this.input;
        }

        @Override
        public double minValue() {
            return this.minValue;
        }

        @Override
        public double maxValue() {
            return this.maxValue;
        }

        public double argument() {
            return this.argument;
        }

        static enum a {

            MUL, ADD;

            private a() {}
        }
    }

    interface u extends DensityFunction.c {

        Holder<NoiseGeneratorNormal.a> noiseData();

        @Nullable
        NoiseGeneratorNormal offsetNoise();

        @Override
        default double minValue() {
            return -this.maxValue();
        }

        @Override
        default double maxValue() {
            NoiseGeneratorNormal noisegeneratornormal = this.offsetNoise();

            return (noisegeneratornormal == null ? 2.0D : noisegeneratornormal.maxValue()) * 4.0D;
        }

        default double compute(double d0, double d1, double d2) {
            NoiseGeneratorNormal noisegeneratornormal = this.offsetNoise();

            return noisegeneratornormal == null ? 0.0D : noisegeneratornormal.getValue(d0 * 0.25D, d1 * 0.25D, d2 * 0.25D) * 4.0D;
        }

        DensityFunctions.u withNewNoise(NoiseGeneratorNormal noisegeneratornormal);
    }

    public interface m extends DensityFunction {

        DensityFunctions.l.a type();

        DensityFunction wrapped();

        @Override
        default Codec<? extends DensityFunction> codec() {
            return this.type().codec;
        }
    }

    protected static record j(Holder<DensityFunction> a) implements DensityFunction {

        private final Holder<DensityFunction> function;

        protected j(Holder<DensityFunction> holder) {
            this.function = holder;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return ((DensityFunction) this.function.value()).compute(densityfunction_b);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            ((DensityFunction) this.function.value()).fillArray(adouble, densityfunction_a);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(new DensityFunctions.j(new Holder.a<>(((DensityFunction) this.function.value()).mapAll(densityfunction_e))));
        }

        @Override
        public double minValue() {
            return ((DensityFunction) this.function.value()).minValue();
        }

        @Override
        public double maxValue() {
            return ((DensityFunction) this.function.value()).maxValue();
        }

        @Override
        public Codec<? extends DensityFunction> codec() {
            throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
        }

        public Holder<DensityFunction> function() {
            return this.function;
        }
    }

    public interface c extends DensityFunction.c {

        Codec<DensityFunction> CODEC = Codec.unit(DensityFunctions.b.INSTANCE);

        @Override
        default Codec<? extends DensityFunction> codec() {
            return DensityFunctions.c.CODEC;
        }
    }

    private interface p extends DensityFunction {

        DensityFunction input();

        @Override
        default double compute(DensityFunction.b densityfunction_b) {
            return this.transform(this.input().compute(densityfunction_b));
        }

        @Override
        default void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.input().fillArray(adouble, densityfunction_a);

            for (int i = 0; i < adouble.length; ++i) {
                adouble[i] = this.transform(adouble[i]);
            }

        }

        double transform(double d0);
    }

    private interface z extends DensityFunction {

        DensityFunction input();

        @Override
        default double compute(DensityFunction.b densityfunction_b) {
            return this.transform(densityfunction_b, this.input().compute(densityfunction_b));
        }

        @Override
        default void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.input().fillArray(adouble, densityfunction_a);

            for (int i = 0; i < adouble.length; ++i) {
                adouble[i] = this.transform(densityfunction_a.forIndex(i), adouble[i]);
            }

        }

        double transform(DensityFunction.b densityfunction_b, double d0);
    }
}
