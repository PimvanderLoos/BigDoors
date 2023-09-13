package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class SurfaceRules {

    public static final SurfaceRules.f ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
    public static final SurfaceRules.f UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
    public static final SurfaceRules.f DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
    public static final SurfaceRules.f VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
    public static final SurfaceRules.f ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
    public static final SurfaceRules.f UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);

    public SurfaceRules() {}

    public static SurfaceRules.f stoneDepthCheck(int i, boolean flag, CaveSurface cavesurface) {
        return new SurfaceRules.t(i, flag, 0, cavesurface);
    }

    public static SurfaceRules.f stoneDepthCheck(int i, boolean flag, int j, CaveSurface cavesurface) {
        return new SurfaceRules.t(i, flag, j, cavesurface);
    }

    public static SurfaceRules.f not(SurfaceRules.f surfacerules_f) {
        return new SurfaceRules.n(surfacerules_f);
    }

    public static SurfaceRules.f yBlockCheck(VerticalAnchor verticalanchor, int i) {
        return new SurfaceRules.aa(verticalanchor, i, false);
    }

    public static SurfaceRules.f yStartCheck(VerticalAnchor verticalanchor, int i) {
        return new SurfaceRules.aa(verticalanchor, i, true);
    }

    public static SurfaceRules.f waterBlockCheck(int i, int j) {
        return new SurfaceRules.z(i, j, false);
    }

    public static SurfaceRules.f waterStartCheck(int i, int j) {
        return new SurfaceRules.z(i, j, true);
    }

    @SafeVarargs
    public static SurfaceRules.f isBiome(ResourceKey<BiomeBase>... aresourcekey) {
        return isBiome(List.of(aresourcekey));
    }

    private static SurfaceRules.c isBiome(List<ResourceKey<BiomeBase>> list) {
        return new SurfaceRules.c(list);
    }

    public static SurfaceRules.f noiseCondition(ResourceKey<NoiseGeneratorNormal.a> resourcekey, double d0) {
        return noiseCondition(resourcekey, d0, Double.MAX_VALUE);
    }

    public static SurfaceRules.f noiseCondition(ResourceKey<NoiseGeneratorNormal.a> resourcekey, double d0, double d1) {
        return new SurfaceRules.l(resourcekey, d0, d1);
    }

    public static SurfaceRules.f verticalGradient(String s, VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return new SurfaceRules.y(new MinecraftKey(s), verticalanchor, verticalanchor1);
    }

    public static SurfaceRules.f steep() {
        return SurfaceRules.s.INSTANCE;
    }

    public static SurfaceRules.f hole() {
        return SurfaceRules.h.INSTANCE;
    }

    public static SurfaceRules.f abovePreliminarySurface() {
        return SurfaceRules.a.INSTANCE;
    }

    public static SurfaceRules.f temperature() {
        return SurfaceRules.v.INSTANCE;
    }

    public static SurfaceRules.o ifTrue(SurfaceRules.f surfacerules_f, SurfaceRules.o surfacerules_o) {
        return new SurfaceRules.x(surfacerules_f, surfacerules_o);
    }

    public static SurfaceRules.o sequence(SurfaceRules.o... asurfacerules_o) {
        if (asurfacerules_o.length == 0) {
            throw new IllegalArgumentException("Need at least 1 rule for a sequence");
        } else {
            return new SurfaceRules.q(Arrays.asList(asurfacerules_o));
        }
    }

    public static SurfaceRules.o state(IBlockData iblockdata) {
        return new SurfaceRules.d(iblockdata);
    }

    public static SurfaceRules.o bandlands() {
        return SurfaceRules.b.INSTANCE;
    }

    static <A> Codec<? extends A> register(IRegistry<Codec<? extends A>> iregistry, String s, KeyDispatchDataCodec<? extends A> keydispatchdatacodec) {
        return (Codec) IRegistry.register(iregistry, s, keydispatchdatacodec.codec());
    }

    private static record t(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.t> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.t::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(SurfaceRules.t::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(SurfaceRules.t::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(SurfaceRules.t::surfaceType)).apply(instance, SurfaceRules.t::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.t.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final boolean flag = this.surfaceType == CaveSurface.CEILING;

            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    int i = flag ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
                    int j = t.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
                    int k = t.this.secondaryDepthRange == 0 ? 0 : (int) MathHelper.map(this.context.getSurfaceSecondary(), -1.0D, 1.0D, 0.0D, (double) t.this.secondaryDepthRange);

                    return i <= 1 + t.this.offset + j + k;
                }
            }

            return new a();
        }
    }

    private static record n(SurfaceRules.f target) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.n> CODEC = KeyDispatchDataCodec.of(SurfaceRules.f.CODEC.xmap(SurfaceRules.n::new, SurfaceRules.n::target).fieldOf("invert"));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.n.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return new SurfaceRules.m((SurfaceRules.e) this.target.apply(surfacerules_g));
        }
    }

    public interface f extends Function<SurfaceRules.g, SurfaceRules.e> {

        Codec<SurfaceRules.f> CODEC = BuiltInRegistries.MATERIAL_CONDITION.byNameCodec().dispatch((surfacerules_f) -> {
            return surfacerules_f.codec().codec();
        }, Function.identity());

        static Codec<? extends SurfaceRules.f> bootstrap(IRegistry<Codec<? extends SurfaceRules.f>> iregistry) {
            SurfaceRules.register(iregistry, "biome", SurfaceRules.c.CODEC);
            SurfaceRules.register(iregistry, "noise_threshold", SurfaceRules.l.CODEC);
            SurfaceRules.register(iregistry, "vertical_gradient", SurfaceRules.y.CODEC);
            SurfaceRules.register(iregistry, "y_above", SurfaceRules.aa.CODEC);
            SurfaceRules.register(iregistry, "water", SurfaceRules.z.CODEC);
            SurfaceRules.register(iregistry, "temperature", SurfaceRules.v.CODEC);
            SurfaceRules.register(iregistry, "steep", SurfaceRules.s.CODEC);
            SurfaceRules.register(iregistry, "not", SurfaceRules.n.CODEC);
            SurfaceRules.register(iregistry, "hole", SurfaceRules.h.CODEC);
            SurfaceRules.register(iregistry, "above_preliminary_surface", SurfaceRules.a.CODEC);
            return SurfaceRules.register(iregistry, "stone_depth", SurfaceRules.t.CODEC);
        }

        KeyDispatchDataCodec<? extends SurfaceRules.f> codec();
    }

    private static record aa(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.aa> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(SurfaceRules.aa::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.aa::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.aa::addStoneDepth)).apply(instance, SurfaceRules.aa::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.aa.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    return this.context.blockY + (aa.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= aa.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * aa.this.surfaceDepthMultiplier;
                }
            }

            return new a();
        }
    }

    private static record z(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.z> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.z::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.z::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.z::addStoneDepth)).apply(instance, SurfaceRules.z::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.z.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    return this.context.waterHeight == Integer.MIN_VALUE || this.context.blockY + (z.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + z.this.offset + this.context.surfaceDepth * z.this.surfaceDepthMultiplier;
                }
            }

            return new a();
        }
    }

    private static final class c implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.c> CODEC = KeyDispatchDataCodec.of(ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, (surfacerules_c) -> {
            return surfacerules_c.biomes;
        }));
        private final List<ResourceKey<BiomeBase>> biomes;
        final Predicate<ResourceKey<BiomeBase>> biomeNameTest;

        c(List<ResourceKey<BiomeBase>> list) {
            this.biomes = list;
            Set set = Set.copyOf(list);

            Objects.requireNonNull(set);
            this.biomeNameTest = set::contains;
        }

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.c.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    return ((Holder) this.context.biome.get()).is(c.this.biomeNameTest);
                }
            }

            return new a();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object instanceof SurfaceRules.c) {
                SurfaceRules.c surfacerules_c = (SurfaceRules.c) object;

                return this.biomes.equals(surfacerules_c.biomes);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.biomes.hashCode();
        }

        public String toString() {
            return "BiomeConditionSource[biomes=" + this.biomes + "]";
        }
    }

    private static record l(ResourceKey<NoiseGeneratorNormal.a> noise, double minThreshold, double maxThreshold) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.l> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(SurfaceRules.l::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(SurfaceRules.l::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(SurfaceRules.l::maxThreshold)).apply(instance, SurfaceRules.l::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.l.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final NoiseGeneratorNormal noisegeneratornormal = surfacerules_g.randomState.getOrCreateNoise(this.noise);

            class a extends SurfaceRules.j {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    double d0 = noisegeneratornormal.getValue((double) this.context.blockX, 0.0D, (double) this.context.blockZ);

                    return d0 >= l.this.minThreshold && d0 <= l.this.maxThreshold;
                }
            }

            return new a();
        }
    }

    private static record y(MinecraftKey randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements SurfaceRules.f {

        static final KeyDispatchDataCodec<SurfaceRules.y> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.fieldOf("random_name").forGetter(SurfaceRules.y::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(SurfaceRules.y::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(SurfaceRules.y::falseAtAndAbove)).apply(instance, SurfaceRules.y::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.y.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final int i = this.trueAtAndBelow().resolveY(surfacerules_g.context);
            final int j = this.falseAtAndAbove().resolveY(surfacerules_g.context);
            final PositionalRandomFactory positionalrandomfactory = surfacerules_g.randomState.getOrCreateRandomFactory(this.randomName());

            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    int k = this.context.blockY;

                    if (k <= i) {
                        return true;
                    } else if (k >= j) {
                        return false;
                    } else {
                        double d0 = MathHelper.map((double) k, (double) i, (double) j, 1.0D, 0.0D);
                        RandomSource randomsource = positionalrandomfactory.at(this.context.blockX, k, this.context.blockZ);

                        return (double) randomsource.nextFloat() < d0;
                    }
                }
            }

            return new a();
        }
    }

    private static enum s implements SurfaceRules.f {

        INSTANCE;

        static final KeyDispatchDataCodec<SurfaceRules.s> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(SurfaceRules.s.INSTANCE));

        private s() {}

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.s.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.steep;
        }
    }

    private static enum h implements SurfaceRules.f {

        INSTANCE;

        static final KeyDispatchDataCodec<SurfaceRules.h> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(SurfaceRules.h.INSTANCE));

        private h() {}

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.h.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.hole;
        }
    }

    private static enum a implements SurfaceRules.f {

        INSTANCE;

        static final KeyDispatchDataCodec<SurfaceRules.a> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(SurfaceRules.a.INSTANCE));

        private a() {}

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.a.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.abovePreliminarySurface;
        }
    }

    private static enum v implements SurfaceRules.f {

        INSTANCE;

        static final KeyDispatchDataCodec<SurfaceRules.v> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(SurfaceRules.v.INSTANCE));

        private v() {}

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.v.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.temperature;
        }
    }

    private static record x(SurfaceRules.f ifTrue, SurfaceRules.o thenRun) implements SurfaceRules.o {

        static final KeyDispatchDataCodec<SurfaceRules.x> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(SurfaceRules.f.CODEC.fieldOf("if_true").forGetter(SurfaceRules.x::ifTrue), SurfaceRules.o.CODEC.fieldOf("then_run").forGetter(SurfaceRules.x::thenRun)).apply(instance, SurfaceRules.x::new);
        }));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.x.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            return new SurfaceRules.w((SurfaceRules.e) this.ifTrue.apply(surfacerules_g), (SurfaceRules.u) this.thenRun.apply(surfacerules_g));
        }
    }

    public interface o extends Function<SurfaceRules.g, SurfaceRules.u> {

        Codec<SurfaceRules.o> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch((surfacerules_o) -> {
            return surfacerules_o.codec().codec();
        }, Function.identity());

        static Codec<? extends SurfaceRules.o> bootstrap(IRegistry<Codec<? extends SurfaceRules.o>> iregistry) {
            SurfaceRules.register(iregistry, "bandlands", SurfaceRules.b.CODEC);
            SurfaceRules.register(iregistry, "block", SurfaceRules.d.CODEC);
            SurfaceRules.register(iregistry, "sequence", SurfaceRules.q.CODEC);
            return SurfaceRules.register(iregistry, "condition", SurfaceRules.x.CODEC);
        }

        KeyDispatchDataCodec<? extends SurfaceRules.o> codec();
    }

    private static record q(List<SurfaceRules.o> sequence) implements SurfaceRules.o {

        static final KeyDispatchDataCodec<SurfaceRules.q> CODEC = KeyDispatchDataCodec.of(SurfaceRules.o.CODEC.listOf().xmap(SurfaceRules.q::new, SurfaceRules.q::sequence).fieldOf("sequence"));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.q.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            if (this.sequence.size() == 1) {
                return (SurfaceRules.u) ((SurfaceRules.o) this.sequence.get(0)).apply(surfacerules_g);
            } else {
                Builder<SurfaceRules.u> builder = ImmutableList.builder();
                Iterator iterator = this.sequence.iterator();

                while (iterator.hasNext()) {
                    SurfaceRules.o surfacerules_o = (SurfaceRules.o) iterator.next();

                    builder.add((SurfaceRules.u) surfacerules_o.apply(surfacerules_g));
                }

                return new SurfaceRules.p(builder.build());
            }
        }
    }

    private static record d(IBlockData resultState, SurfaceRules.r rule) implements SurfaceRules.o {

        static final KeyDispatchDataCodec<SurfaceRules.d> CODEC = KeyDispatchDataCodec.of(IBlockData.CODEC.xmap(SurfaceRules.d::new, SurfaceRules.d::resultState).fieldOf("result_state"));

        d(IBlockData iblockdata) {
            this(iblockdata, new SurfaceRules.r(iblockdata));
        }

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.d.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            return this.rule;
        }
    }

    private static enum b implements SurfaceRules.o {

        INSTANCE;

        static final KeyDispatchDataCodec<SurfaceRules.b> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(SurfaceRules.b.INSTANCE));

        private b() {}

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.b.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            SurfaceSystem surfacesystem = surfacerules_g.system;

            Objects.requireNonNull(surfacerules_g.system);
            return surfacesystem::getBand;
        }
    }

    private static record p(List<SurfaceRules.u> rules) implements SurfaceRules.u {

        @Nullable
        @Override
        public IBlockData tryApply(int i, int j, int k) {
            Iterator iterator = this.rules.iterator();

            IBlockData iblockdata;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                SurfaceRules.u surfacerules_u = (SurfaceRules.u) iterator.next();

                iblockdata = surfacerules_u.tryApply(i, j, k);
            } while (iblockdata == null);

            return iblockdata;
        }
    }

    private static record w(SurfaceRules.e condition, SurfaceRules.u followup) implements SurfaceRules.u {

        @Nullable
        @Override
        public IBlockData tryApply(int i, int j, int k) {
            return !this.condition.test() ? null : this.followup.tryApply(i, j, k);
        }
    }

    private static record r(IBlockData state) implements SurfaceRules.u {

        @Override
        public IBlockData tryApply(int i, int j, int k) {
            return this.state;
        }
    }

    protected interface u {

        @Nullable
        IBlockData tryApply(int i, int j, int k);
    }

    private static record m(SurfaceRules.e target) implements SurfaceRules.e {

        @Override
        public boolean test() {
            return !this.target.test();
        }
    }

    private abstract static class k extends SurfaceRules.i {

        protected k(SurfaceRules.g surfacerules_g) {
            super(surfacerules_g);
        }

        @Override
        protected long getContextLastUpdate() {
            return this.context.lastUpdateY;
        }
    }

    private abstract static class j extends SurfaceRules.i {

        protected j(SurfaceRules.g surfacerules_g) {
            super(surfacerules_g);
        }

        @Override
        protected long getContextLastUpdate() {
            return this.context.lastUpdateXZ;
        }
    }

    private abstract static class i implements SurfaceRules.e {

        protected final SurfaceRules.g context;
        private long lastUpdate;
        @Nullable
        Boolean result;

        protected i(SurfaceRules.g surfacerules_g) {
            this.context = surfacerules_g;
            this.lastUpdate = this.getContextLastUpdate() - 1L;
        }

        @Override
        public boolean test() {
            long i = this.getContextLastUpdate();

            if (i == this.lastUpdate) {
                if (this.result == null) {
                    throw new IllegalStateException("Update triggered but the result is null");
                } else {
                    return this.result;
                }
            } else {
                this.lastUpdate = i;
                this.result = this.compute();
                return this.result;
            }
        }

        protected abstract long getContextLastUpdate();

        protected abstract boolean compute();
    }

    private interface e {

        boolean test();
    }

    protected static final class g {

        private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
        private static final int SURFACE_CELL_BITS = 4;
        private static final int SURFACE_CELL_SIZE = 16;
        private static final int SURFACE_CELL_MASK = 15;
        final SurfaceSystem system;
        final SurfaceRules.e temperature = new SurfaceRules.g.d(this);
        final SurfaceRules.e steep = new SurfaceRules.g.c(this);
        final SurfaceRules.e hole = new SurfaceRules.g.b(this);
        final SurfaceRules.e abovePreliminarySurface = new SurfaceRules.g.a();
        final RandomState randomState;
        final IChunkAccess chunk;
        private final NoiseChunk noiseChunk;
        private final Function<BlockPosition, Holder<BiomeBase>> biomeGetter;
        final WorldGenerationContext context;
        private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
        private final int[] preliminarySurfaceCache = new int[4];
        long lastUpdateXZ = -9223372036854775807L;
        int blockX;
        int blockZ;
        int surfaceDepth;
        private long lastSurfaceDepth2Update;
        private double surfaceSecondary;
        private long lastMinSurfaceLevelUpdate;
        private int minSurfaceLevel;
        long lastUpdateY;
        final BlockPosition.MutableBlockPosition pos;
        Supplier<Holder<BiomeBase>> biome;
        int blockY;
        int waterHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected g(SurfaceSystem surfacesystem, RandomState randomstate, IChunkAccess ichunkaccess, NoiseChunk noisechunk, Function<BlockPosition, Holder<BiomeBase>> function, IRegistry<BiomeBase> iregistry, WorldGenerationContext worldgenerationcontext) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
            this.lastUpdateY = -9223372036854775807L;
            this.pos = new BlockPosition.MutableBlockPosition();
            this.system = surfacesystem;
            this.randomState = randomstate;
            this.chunk = ichunkaccess;
            this.noiseChunk = noisechunk;
            this.biomeGetter = function;
            this.context = worldgenerationcontext;
        }

        protected void updateXZ(int i, int j) {
            ++this.lastUpdateXZ;
            ++this.lastUpdateY;
            this.blockX = i;
            this.blockZ = j;
            this.surfaceDepth = this.system.getSurfaceDepth(i, j);
        }

        protected void updateY(int i, int j, int k, int l, int i1, int j1) {
            ++this.lastUpdateY;
            this.biome = Suppliers.memoize(() -> {
                return (Holder) this.biomeGetter.apply(this.pos.set(l, i1, j1));
            });
            this.blockY = i1;
            this.waterHeight = k;
            this.stoneDepthBelow = j;
            this.stoneDepthAbove = i;
        }

        protected double getSurfaceSecondary() {
            if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
                this.lastSurfaceDepth2Update = this.lastUpdateXZ;
                this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
            }

            return this.surfaceSecondary;
        }

        private static int blockCoordToSurfaceCell(int i) {
            return i >> 4;
        }

        private static int surfaceCellToBlockCoord(int i) {
            return i << 4;
        }

        protected int getMinSurfaceLevel() {
            if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
                this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
                int i = blockCoordToSurfaceCell(this.blockX);
                int j = blockCoordToSurfaceCell(this.blockZ);
                long k = ChunkCoordIntPair.asLong(i, j);

                if (this.lastPreliminarySurfaceCellOrigin != k) {
                    this.lastPreliminarySurfaceCellOrigin = k;
                    this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j));
                    this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j));
                    this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j + 1));
                    this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j + 1));
                }

                int l = MathHelper.floor(MathHelper.lerp2((double) ((float) (this.blockX & 15) / 16.0F), (double) ((float) (this.blockZ & 15) / 16.0F), (double) this.preliminarySurfaceCache[0], (double) this.preliminarySurfaceCache[1], (double) this.preliminarySurfaceCache[2], (double) this.preliminarySurfaceCache[3]));

                this.minSurfaceLevel = l + this.surfaceDepth - 8;
            }

            return this.minSurfaceLevel;
        }

        private static class d extends SurfaceRules.k {

            d(SurfaceRules.g surfacerules_g) {
                super(surfacerules_g);
            }

            @Override
            protected boolean compute() {
                return ((BiomeBase) ((Holder) this.context.biome.get()).value()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
            }
        }

        private static class c extends SurfaceRules.j {

            c(SurfaceRules.g surfacerules_g) {
                super(surfacerules_g);
            }

            @Override
            protected boolean compute() {
                int i = this.context.blockX & 15;
                int j = this.context.blockZ & 15;
                int k = Math.max(j - 1, 0);
                int l = Math.min(j + 1, 15);
                IChunkAccess ichunkaccess = this.context.chunk;
                int i1 = ichunkaccess.getHeight(HeightMap.Type.WORLD_SURFACE_WG, i, k);
                int j1 = ichunkaccess.getHeight(HeightMap.Type.WORLD_SURFACE_WG, i, l);

                if (j1 >= i1 + 4) {
                    return true;
                } else {
                    int k1 = Math.max(i - 1, 0);
                    int l1 = Math.min(i + 1, 15);
                    int i2 = ichunkaccess.getHeight(HeightMap.Type.WORLD_SURFACE_WG, k1, j);
                    int j2 = ichunkaccess.getHeight(HeightMap.Type.WORLD_SURFACE_WG, l1, j);

                    return i2 >= j2 + 4;
                }
            }
        }

        private static final class b extends SurfaceRules.j {

            b(SurfaceRules.g surfacerules_g) {
                super(surfacerules_g);
            }

            @Override
            protected boolean compute() {
                return this.context.surfaceDepth <= 0;
            }
        }

        private final class a implements SurfaceRules.e {

            a() {}

            @Override
            public boolean test() {
                return g.this.blockY >= g.this.getMinSurfaceLevel();
            }
        }
    }
}
