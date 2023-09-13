package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class SurfaceRules {

    public static final SurfaceRules.f ON_FLOOR = stoneDepthCheck(0, false, false, CaveSurface.FLOOR);
    public static final SurfaceRules.f UNDER_FLOOR = stoneDepthCheck(0, true, false, CaveSurface.FLOOR);
    public static final SurfaceRules.f ON_CEILING = stoneDepthCheck(0, false, false, CaveSurface.CEILING);
    public static final SurfaceRules.f UNDER_CEILING = stoneDepthCheck(0, true, false, CaveSurface.CEILING);

    public SurfaceRules() {}

    public static SurfaceRules.f stoneDepthCheck(int i, boolean flag, boolean flag1, CaveSurface cavesurface) {
        return new SurfaceRules.t(i, flag, flag1, cavesurface);
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

    private static record t(int a, boolean c, boolean d, CaveSurface e) implements SurfaceRules.f {

        final int offset;
        final boolean addSurfaceDepth;
        final boolean addSurfaceSecondaryDepth;
        private final CaveSurface surfaceType;
        static final Codec<SurfaceRules.t> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.t::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(SurfaceRules.t::addSurfaceDepth), Codec.BOOL.fieldOf("add_surface_secondary_depth").forGetter(SurfaceRules.t::addSurfaceSecondaryDepth), CaveSurface.CODEC.fieldOf("surface_type").forGetter(SurfaceRules.t::surfaceType)).apply(instance, SurfaceRules.t::new);
        });

        t(int i, boolean flag, boolean flag1, CaveSurface cavesurface) {
            this.offset = i;
            this.addSurfaceDepth = flag;
            this.addSurfaceSecondaryDepth = flag1;
            this.surfaceType = cavesurface;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
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
                    return (flag ? this.context.stoneDepthBelow : this.context.stoneDepthAbove) <= 1 + t.this.offset + (t.this.addSurfaceDepth ? this.context.surfaceDepth : 0) + (t.this.addSurfaceSecondaryDepth ? this.context.getSurfaceSecondaryDepth() : 0);
                }
            }

            return new a();
        }

        public int offset() {
            return this.offset;
        }

        public boolean addSurfaceDepth() {
            return this.addSurfaceDepth;
        }

        public boolean addSurfaceSecondaryDepth() {
            return this.addSurfaceSecondaryDepth;
        }

        public CaveSurface surfaceType() {
            return this.surfaceType;
        }
    }

    private static record n(SurfaceRules.f a) implements SurfaceRules.f {

        private final SurfaceRules.f target;
        static final Codec<SurfaceRules.n> CODEC = SurfaceRules.f.CODEC.xmap(SurfaceRules.n::new, SurfaceRules.n::target).fieldOf("invert").codec();

        n(SurfaceRules.f surfacerules_f) {
            this.target = surfacerules_f;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.n.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return new SurfaceRules.m((SurfaceRules.e) this.target.apply(surfacerules_g));
        }

        public SurfaceRules.f target() {
            return this.target;
        }
    }

    public interface f extends Function<SurfaceRules.g, SurfaceRules.e> {

        Codec<SurfaceRules.f> CODEC = IRegistry.CONDITION.byNameCodec().dispatch(SurfaceRules.f::codec, Function.identity());

        static Codec<? extends SurfaceRules.f> bootstrap() {
            IRegistry.register(IRegistry.CONDITION, "biome", SurfaceRules.c.CODEC);
            IRegistry.register(IRegistry.CONDITION, "noise_threshold", SurfaceRules.l.CODEC);
            IRegistry.register(IRegistry.CONDITION, "vertical_gradient", SurfaceRules.y.CODEC);
            IRegistry.register(IRegistry.CONDITION, "y_above", SurfaceRules.aa.CODEC);
            IRegistry.register(IRegistry.CONDITION, "water", SurfaceRules.z.CODEC);
            IRegistry.register(IRegistry.CONDITION, "temperature", SurfaceRules.v.CODEC);
            IRegistry.register(IRegistry.CONDITION, "steep", SurfaceRules.s.CODEC);
            IRegistry.register(IRegistry.CONDITION, "not", SurfaceRules.n.CODEC);
            IRegistry.register(IRegistry.CONDITION, "hole", SurfaceRules.h.CODEC);
            IRegistry.register(IRegistry.CONDITION, "above_preliminary_surface", SurfaceRules.a.CODEC);
            IRegistry.register(IRegistry.CONDITION, "stone_depth", SurfaceRules.t.CODEC);
            return (Codec) IRegistry.CONDITION.iterator().next();
        }

        Codec<? extends SurfaceRules.f> codec();
    }

    private static record aa(VerticalAnchor a, int c, boolean d) implements SurfaceRules.f {

        final VerticalAnchor anchor;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        static final Codec<SurfaceRules.aa> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(SurfaceRules.aa::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.aa::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.aa::addStoneDepth)).apply(instance, SurfaceRules.aa::new);
        });

        aa(VerticalAnchor verticalanchor, int i, boolean flag) {
            this.anchor = verticalanchor;
            this.surfaceDepthMultiplier = i;
            this.addStoneDepth = flag;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
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

        public VerticalAnchor anchor() {
            return this.anchor;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }
    }

    private static record z(int a, int c, boolean d) implements SurfaceRules.f {

        final int offset;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        static final Codec<SurfaceRules.z> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.z::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.z::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.z::addStoneDepth)).apply(instance, SurfaceRules.z::new);
        });

        z(int i, int j, boolean flag) {
            this.offset = i;
            this.surfaceDepthMultiplier = j;
            this.addStoneDepth = flag;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
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

        public int offset() {
            return this.offset;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }
    }

    private static record c(List<ResourceKey<BiomeBase>> a) implements SurfaceRules.f {

        private final List<ResourceKey<BiomeBase>> biomes;
        static final Codec<SurfaceRules.c> CODEC = ResourceKey.codec(IRegistry.BIOME_REGISTRY).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, SurfaceRules.c::biomes).codec();

        c(List<ResourceKey<BiomeBase>> list) {
            this.biomes = list;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.c.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final Set<ResourceKey<BiomeBase>> set = Set.copyOf(this.biomes);

            class a extends SurfaceRules.k {

                a() {
                    super(surfacerules_g);
                }

                @Override
                protected boolean compute() {
                    return set.contains(this.context.biomeKey.get());
                }
            }

            return new a();
        }

        public List<ResourceKey<BiomeBase>> biomes() {
            return this.biomes;
        }
    }

    private static record l(ResourceKey<NoiseGeneratorNormal.a> a, double c, double d) implements SurfaceRules.f {

        private final ResourceKey<NoiseGeneratorNormal.a> noise;
        final double minThreshold;
        final double maxThreshold;
        static final Codec<SurfaceRules.l> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ResourceKey.codec(IRegistry.NOISE_REGISTRY).fieldOf("noise").forGetter(SurfaceRules.l::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(SurfaceRules.l::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(SurfaceRules.l::maxThreshold)).apply(instance, SurfaceRules.l::new);
        });

        l(ResourceKey<NoiseGeneratorNormal.a> resourcekey, double d0, double d1) {
            this.noise = resourcekey;
            this.minThreshold = d0;
            this.maxThreshold = d1;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.l.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final NoiseGeneratorNormal noisegeneratornormal = surfacerules_g.system.getOrCreateNoise(this.noise);

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

        public ResourceKey<NoiseGeneratorNormal.a> noise() {
            return this.noise;
        }

        public double minThreshold() {
            return this.minThreshold;
        }

        public double maxThreshold() {
            return this.maxThreshold;
        }
    }

    private static record y(MinecraftKey a, VerticalAnchor c, VerticalAnchor d) implements SurfaceRules.f {

        private final MinecraftKey randomName;
        private final VerticalAnchor trueAtAndBelow;
        private final VerticalAnchor falseAtAndAbove;
        static final Codec<SurfaceRules.y> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(MinecraftKey.CODEC.fieldOf("random_name").forGetter(SurfaceRules.y::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(SurfaceRules.y::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(SurfaceRules.y::falseAtAndAbove)).apply(instance, SurfaceRules.y::new);
        });

        y(MinecraftKey minecraftkey, VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
            this.randomName = minecraftkey;
            this.trueAtAndBelow = verticalanchor;
            this.falseAtAndAbove = verticalanchor1;
        }

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.y.CODEC;
        }

        public SurfaceRules.e apply(final SurfaceRules.g surfacerules_g) {
            final int i = this.trueAtAndBelow().resolveY(surfacerules_g.context);
            final int j = this.falseAtAndAbove().resolveY(surfacerules_g.context);
            final PositionalRandomFactory positionalrandomfactory = surfacerules_g.system.getOrCreateRandomFactory(this.randomName());

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

        public MinecraftKey randomName() {
            return this.randomName;
        }

        public VerticalAnchor trueAtAndBelow() {
            return this.trueAtAndBelow;
        }

        public VerticalAnchor falseAtAndAbove() {
            return this.falseAtAndAbove;
        }
    }

    private static enum s implements SurfaceRules.f {

        INSTANCE;

        static final Codec<SurfaceRules.s> CODEC = Codec.unit(SurfaceRules.s.INSTANCE);

        private s() {}

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.s.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.steep;
        }
    }

    private static enum h implements SurfaceRules.f {

        INSTANCE;

        static final Codec<SurfaceRules.h> CODEC = Codec.unit(SurfaceRules.h.INSTANCE);

        private h() {}

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.h.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.hole;
        }
    }

    private static enum a implements SurfaceRules.f {

        INSTANCE;

        static final Codec<SurfaceRules.a> CODEC = Codec.unit(SurfaceRules.a.INSTANCE);

        private a() {}

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.a.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.abovePreliminarySurface;
        }
    }

    private static enum v implements SurfaceRules.f {

        INSTANCE;

        static final Codec<SurfaceRules.v> CODEC = Codec.unit(SurfaceRules.v.INSTANCE);

        private v() {}

        @Override
        public Codec<? extends SurfaceRules.f> codec() {
            return SurfaceRules.v.CODEC;
        }

        public SurfaceRules.e apply(SurfaceRules.g surfacerules_g) {
            return surfacerules_g.temperature;
        }
    }

    private static record x(SurfaceRules.f a, SurfaceRules.o c) implements SurfaceRules.o {

        private final SurfaceRules.f ifTrue;
        private final SurfaceRules.o thenRun;
        static final Codec<SurfaceRules.x> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(SurfaceRules.f.CODEC.fieldOf("if_true").forGetter(SurfaceRules.x::ifTrue), SurfaceRules.o.CODEC.fieldOf("then_run").forGetter(SurfaceRules.x::thenRun)).apply(instance, SurfaceRules.x::new);
        });

        x(SurfaceRules.f surfacerules_f, SurfaceRules.o surfacerules_o) {
            this.ifTrue = surfacerules_f;
            this.thenRun = surfacerules_o;
        }

        @Override
        public Codec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.x.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            return new SurfaceRules.w((SurfaceRules.e) this.ifTrue.apply(surfacerules_g), (SurfaceRules.u) this.thenRun.apply(surfacerules_g));
        }

        public SurfaceRules.f ifTrue() {
            return this.ifTrue;
        }

        public SurfaceRules.o thenRun() {
            return this.thenRun;
        }
    }

    public interface o extends Function<SurfaceRules.g, SurfaceRules.u> {

        Codec<SurfaceRules.o> CODEC = IRegistry.RULE.byNameCodec().dispatch(SurfaceRules.o::codec, Function.identity());

        static Codec<? extends SurfaceRules.o> bootstrap() {
            IRegistry.register(IRegistry.RULE, "bandlands", SurfaceRules.b.CODEC);
            IRegistry.register(IRegistry.RULE, "block", SurfaceRules.d.CODEC);
            IRegistry.register(IRegistry.RULE, "sequence", SurfaceRules.q.CODEC);
            IRegistry.register(IRegistry.RULE, "condition", SurfaceRules.x.CODEC);
            return (Codec) IRegistry.RULE.iterator().next();
        }

        Codec<? extends SurfaceRules.o> codec();
    }

    private static record q(List<SurfaceRules.o> a) implements SurfaceRules.o {

        private final List<SurfaceRules.o> sequence;
        static final Codec<SurfaceRules.q> CODEC = SurfaceRules.o.CODEC.listOf().xmap(SurfaceRules.q::new, SurfaceRules.q::sequence).fieldOf("sequence").codec();

        q(List<SurfaceRules.o> list) {
            this.sequence = list;
        }

        @Override
        public Codec<? extends SurfaceRules.o> codec() {
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

        public List<SurfaceRules.o> sequence() {
            return this.sequence;
        }
    }

    private static record d(IBlockData a, SurfaceRules.r c) implements SurfaceRules.o {

        private final IBlockData resultState;
        private final SurfaceRules.r rule;
        static final Codec<SurfaceRules.d> CODEC = IBlockData.CODEC.xmap(SurfaceRules.d::new, SurfaceRules.d::resultState).fieldOf("result_state").codec();

        d(IBlockData iblockdata) {
            this(iblockdata, new SurfaceRules.r(iblockdata));
        }

        private d(IBlockData iblockdata, SurfaceRules.r surfacerules_r) {
            this.resultState = iblockdata;
            this.rule = surfacerules_r;
        }

        @Override
        public Codec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.d.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            return this.rule;
        }

        public IBlockData resultState() {
            return this.resultState;
        }

        public SurfaceRules.r rule() {
            return this.rule;
        }
    }

    private static enum b implements SurfaceRules.o {

        INSTANCE;

        static final Codec<SurfaceRules.b> CODEC = Codec.unit(SurfaceRules.b.INSTANCE);

        private b() {}

        @Override
        public Codec<? extends SurfaceRules.o> codec() {
            return SurfaceRules.b.CODEC;
        }

        public SurfaceRules.u apply(SurfaceRules.g surfacerules_g) {
            SurfaceSystem surfacesystem = surfacerules_g.system;

            Objects.requireNonNull(surfacerules_g.system);
            return surfacesystem::getBand;
        }
    }

    private static record p(List<SurfaceRules.u> a) implements SurfaceRules.u {

        private final List<SurfaceRules.u> rules;

        p(List<SurfaceRules.u> list) {
            this.rules = list;
        }

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

        public List<SurfaceRules.u> rules() {
            return this.rules;
        }
    }

    private static record w(SurfaceRules.e a, SurfaceRules.u b) implements SurfaceRules.u {

        private final SurfaceRules.e condition;
        private final SurfaceRules.u followup;

        w(SurfaceRules.e surfacerules_e, SurfaceRules.u surfacerules_u) {
            this.condition = surfacerules_e;
            this.followup = surfacerules_u;
        }

        @Nullable
        @Override
        public IBlockData tryApply(int i, int j, int k) {
            return !this.condition.test() ? null : this.followup.tryApply(i, j, k);
        }

        public SurfaceRules.e condition() {
            return this.condition;
        }

        public SurfaceRules.u followup() {
            return this.followup;
        }
    }

    private static record r(IBlockData a) implements SurfaceRules.u {

        private final IBlockData state;

        r(IBlockData iblockdata) {
            this.state = iblockdata;
        }

        @Override
        public IBlockData tryApply(int i, int j, int k) {
            return this.state;
        }

        public IBlockData state() {
            return this.state;
        }
    }

    protected interface u {

        @Nullable
        IBlockData tryApply(int i, int j, int k);
    }

    private static record m(SurfaceRules.e a) implements SurfaceRules.e {

        private final SurfaceRules.e target;

        m(SurfaceRules.e surfacerules_e) {
            this.target = surfacerules_e;
        }

        @Override
        public boolean test() {
            return !this.target.test();
        }

        public SurfaceRules.e target() {
            return this.target;
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
        final IChunkAccess chunk;
        private final NoiseChunk noiseChunk;
        private final Function<BlockPosition, BiomeBase> biomeGetter;
        private final IRegistry<BiomeBase> biomes;
        final WorldGenerationContext context;
        private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
        private final int[] preliminarySurfaceCache = new int[4];
        long lastUpdateXZ = -9223372036854775807L;
        int blockX;
        int blockZ;
        int surfaceDepth;
        private long lastSurfaceDepth2Update;
        private int surfaceSecondaryDepth;
        private long lastMinSurfaceLevelUpdate;
        private int minSurfaceLevel;
        long lastUpdateY;
        final BlockPosition.MutableBlockPosition pos;
        Supplier<BiomeBase> biome;
        Supplier<ResourceKey<BiomeBase>> biomeKey;
        int blockY;
        int waterHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected g(SurfaceSystem surfacesystem, IChunkAccess ichunkaccess, NoiseChunk noisechunk, Function<BlockPosition, BiomeBase> function, IRegistry<BiomeBase> iregistry, WorldGenerationContext worldgenerationcontext) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
            this.lastUpdateY = -9223372036854775807L;
            this.pos = new BlockPosition.MutableBlockPosition();
            this.system = surfacesystem;
            this.chunk = ichunkaccess;
            this.noiseChunk = noisechunk;
            this.biomeGetter = function;
            this.biomes = iregistry;
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
                return (BiomeBase) this.biomeGetter.apply(this.pos.set(l, i1, j1));
            });
            this.biomeKey = Suppliers.memoize(() -> {
                return (ResourceKey) this.biomes.getResourceKey((BiomeBase) this.biome.get()).orElseThrow(() -> {
                    return new IllegalStateException("Unregistered biome: " + this.biome);
                });
            });
            this.blockY = i1;
            this.waterHeight = k;
            this.stoneDepthBelow = j;
            this.stoneDepthAbove = i;
        }

        protected int getSurfaceSecondaryDepth() {
            if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
                this.lastSurfaceDepth2Update = this.lastUpdateXZ;
                this.surfaceSecondaryDepth = this.system.getSurfaceSecondaryDepth(this.blockX, this.blockZ);
            }

            return this.surfaceSecondaryDepth;
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
                return ((BiomeBase) this.context.biome.get()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
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
