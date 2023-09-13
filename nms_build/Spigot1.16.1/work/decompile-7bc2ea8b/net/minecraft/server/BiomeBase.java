package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeBase {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<BiomeBase> b = RecordCodecBuilder.create((instance) -> {
        RecordCodecBuilder recordcodecbuilder = BiomeBase.Precipitation.d.fieldOf("precipitation").forGetter((biomebase) -> {
            return biomebase.o;
        });
        RecordCodecBuilder recordcodecbuilder1 = BiomeBase.Geography.r.fieldOf("category").forGetter((biomebase) -> {
            return biomebase.n;
        });
        RecordCodecBuilder recordcodecbuilder2 = Codec.FLOAT.fieldOf("depth").forGetter((biomebase) -> {
            return biomebase.h;
        });
        RecordCodecBuilder recordcodecbuilder3 = Codec.FLOAT.fieldOf("scale").forGetter((biomebase) -> {
            return biomebase.i;
        });
        RecordCodecBuilder recordcodecbuilder4 = Codec.FLOAT.fieldOf("temperature").forGetter((biomebase) -> {
            return biomebase.j;
        });
        RecordCodecBuilder recordcodecbuilder5 = Codec.FLOAT.fieldOf("downfall").forGetter((biomebase) -> {
            return biomebase.k;
        });
        RecordCodecBuilder recordcodecbuilder6 = BiomeFog.a.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.p;
        });
        RecordCodecBuilder recordcodecbuilder7 = Codec.INT.fieldOf("sky_color").forGetter((biomebase) -> {
            return biomebase.t;
        });
        RecordCodecBuilder recordcodecbuilder8 = WorldGenSurfaceComposite.a.fieldOf("surface_builder").forGetter((biomebase) -> {
            return biomebase.m;
        });
        Codec codec = WorldGenStage.Features.c;
        Codec codec1 = WorldGenCarverWrapper.a.listOf();
        Logger logger = BiomeBase.LOGGER;

        logger.getClass();
        RecordCodecBuilder recordcodecbuilder9 = Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.a("Carver: ", logger::error)), INamable.a(WorldGenStage.Features.values())).fieldOf("carvers").forGetter((biomebase) -> {
            return biomebase.q;
        });

        codec1 = WorldGenStage.Decoration.k;
        Codec codec2 = WorldGenFeatureConfigured.b.listOf();
        Logger logger1 = BiomeBase.LOGGER;

        logger1.getClass();
        RecordCodecBuilder recordcodecbuilder10 = Codec.simpleMap(codec1, codec2.promotePartial(SystemUtils.a("Feature: ", logger1::error)), INamable.a(WorldGenStage.Decoration.values())).fieldOf("features").forGetter((biomebase) -> {
            return biomebase.r;
        });

        codec2 = StructureFeature.a.listOf();
        logger1 = BiomeBase.LOGGER;
        logger1.getClass();
        RecordCodecBuilder recordcodecbuilder11 = codec2.promotePartial(SystemUtils.a("Structure start: ", logger1::error)).fieldOf("starts").forGetter((biomebase) -> {
            return (List) biomebase.u.values().stream().sorted(Comparator.comparing((structurefeature) -> {
                return IRegistry.STRUCTURE_FEATURE.getKey(structurefeature.b);
            })).collect(Collectors.toList());
        });
        Codec codec3 = EnumCreatureType.g;
        Codec codec4 = BiomeBase.BiomeMeta.b.listOf();
        Logger logger2 = BiomeBase.LOGGER;

        logger2.getClass();
        return instance.group(recordcodecbuilder, recordcodecbuilder1, recordcodecbuilder2, recordcodecbuilder3, recordcodecbuilder4, recordcodecbuilder5, recordcodecbuilder6, recordcodecbuilder7, recordcodecbuilder8, recordcodecbuilder9, recordcodecbuilder10, recordcodecbuilder11, Codec.simpleMap(codec3, codec4.promotePartial(SystemUtils.a("Spawn data: ", logger2::error)), INamable.a(EnumCreatureType.values())).fieldOf("spawners").forGetter((biomebase) -> {
            return biomebase.v;
        }), BiomeBase.d.a.listOf().fieldOf("climate_parameters").forGetter((biomebase) -> {
            return biomebase.x;
        }), Codec.STRING.optionalFieldOf("parent").forGetter((biomebase) -> {
            return Optional.ofNullable(biomebase.l);
        })).apply(instance, BiomeBase::new);
    });
    public static final Set<BiomeBase> c = Sets.newHashSet();
    public static final RegistryBlockID<BiomeBase> d = new RegistryBlockID<>();
    protected static final NoiseGenerator3 e = new NoiseGenerator3(new SeededRandom(1234L), ImmutableList.of(0));
    public static final NoiseGenerator3 f = new NoiseGenerator3(new SeededRandom(2345L), ImmutableList.of(0));
    @Nullable
    protected String g;
    protected final float h;
    protected final float i;
    protected final float j;
    protected final float k;
    private final int t;
    @Nullable
    protected final String l;
    protected final WorldGenSurfaceComposite<?> m;
    protected final BiomeBase.Geography n;
    protected final BiomeBase.Precipitation o;
    protected final BiomeFog p;
    protected final Map<WorldGenStage.Features, List<WorldGenCarverWrapper<?>>> q;
    protected final Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?, ?>>> r;
    protected final List<WorldGenFeatureConfigured<?, ?>> s = Lists.newArrayList();
    private final Map<StructureGenerator<?>, StructureFeature<?, ?>> u;
    private final Map<EnumCreatureType, List<BiomeBase.BiomeMeta>> v;
    private final Map<EntityTypes<?>, BiomeBase.e> w = Maps.newHashMap();
    private final List<BiomeBase.d> x;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> y = ThreadLocal.withInitial(() -> {
        return (Long2FloatLinkedOpenHashMap) SystemUtils.a(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
                protected void rehash(int i) {}
            };

            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });
    });

    @Nullable
    public static BiomeBase a(BiomeBase biomebase) {
        return (BiomeBase) BiomeBase.d.fromId(IRegistry.BIOME.a((Object) biomebase));
    }

    public static <C extends WorldGenCarverConfiguration> WorldGenCarverWrapper<C> a(WorldGenCarverAbstract<C> worldgencarverabstract, C c0) {
        return new WorldGenCarverWrapper<>(worldgencarverabstract, c0);
    }

    protected BiomeBase(BiomeBase.a biomebase_a) {
        if (biomebase_a.a != null && biomebase_a.b != null && biomebase_a.c != null && biomebase_a.d != null && biomebase_a.e != null && biomebase_a.f != null && biomebase_a.g != null && biomebase_a.j != null) {
            this.m = biomebase_a.a;
            this.o = biomebase_a.b;
            this.n = biomebase_a.c;
            this.h = biomebase_a.d;
            this.i = biomebase_a.e;
            this.j = biomebase_a.f;
            this.k = biomebase_a.g;
            this.t = this.D();
            this.l = biomebase_a.h;
            this.x = (List) (biomebase_a.i != null ? biomebase_a.i : ImmutableList.of());
            this.p = biomebase_a.j;
            this.q = Maps.newHashMap();
            this.u = Maps.newHashMap();
            this.r = Maps.newHashMap();
            WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
            int i = aworldgenstage_decoration.length;

            int j;

            for (j = 0; j < i; ++j) {
                WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[j];

                this.r.put(worldgenstage_decoration, Lists.newArrayList());
            }

            this.v = Maps.newHashMap();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();

            i = aenumcreaturetype.length;

            for (j = 0; j < i; ++j) {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[j];

                this.v.put(enumcreaturetype, Lists.newArrayList());
            }

        } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + biomebase_a);
        }
    }

    private BiomeBase(BiomeBase.Precipitation biomebase_precipitation, BiomeBase.Geography biomebase_geography, float f, float f1, float f2, float f3, BiomeFog biomefog, int i, WorldGenSurfaceComposite<?> worldgensurfacecomposite, Map<WorldGenStage.Features, List<WorldGenCarverWrapper<?>>> map, Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?, ?>>> map1, List<StructureFeature<?, ?>> list, Map<EnumCreatureType, List<BiomeBase.BiomeMeta>> map2, List<BiomeBase.d> list1, Optional<String> optional) {
        this.o = biomebase_precipitation;
        this.n = biomebase_geography;
        this.h = f;
        this.i = f1;
        this.j = f2;
        this.k = f3;
        this.p = biomefog;
        this.t = i;
        this.m = worldgensurfacecomposite;
        this.q = map;
        this.r = map1;
        this.u = (Map) list.stream().collect(Collectors.toMap((structurefeature) -> {
            return structurefeature.b;
        }, Function.identity()));
        this.v = map2;
        this.x = list1;
        this.l = (String) optional.orElse((Object) null);
        Stream stream = map1.values().stream().flatMap(Collection::stream).filter((worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.d == WorldGenerator.DECORATED_FLOWER;
        });
        List list2 = this.s;

        this.s.getClass();
        stream.forEach(list2::add);
    }

    public boolean b() {
        return this.l != null;
    }

    private int D() {
        float f = this.j;

        f /= 3.0F;
        f = MathHelper.a(f, -1.0F, 1.0F);
        return MathHelper.f(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    protected void a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta) {
        ((List) this.v.get(enumcreaturetype)).add(biomebase_biomemeta);
    }

    protected void a(EntityTypes<?> entitytypes, double d0, double d1) {
        this.w.put(entitytypes, new BiomeBase.e(d1, d0));
    }

    public List<BiomeBase.BiomeMeta> getMobs(EnumCreatureType enumcreaturetype) {
        return (List) this.v.get(enumcreaturetype);
    }

    @Nullable
    public BiomeBase.e a(EntityTypes<?> entitytypes) {
        return (BiomeBase.e) this.w.get(entitytypes);
    }

    public BiomeBase.Precipitation d() {
        return this.o;
    }

    public boolean e() {
        return this.getHumidity() > 0.85F;
    }

    public float f() {
        return 0.1F;
    }

    protected float a(BlockPosition blockposition) {
        if (blockposition.getY() > 64) {
            float f = (float) (BiomeBase.e.a((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F), false) * 4.0D);

            return this.getTemperature() - (f + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.getTemperature();
        }
    }

    public final float getAdjustedTemperature(BlockPosition blockposition) {
        long i = blockposition.asLong();
        Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = (Long2FloatLinkedOpenHashMap) this.y.get();
        float f = long2floatlinkedopenhashmap.get(i);

        if (!Float.isNaN(f)) {
            return f;
        } else {
            float f1 = this.a(blockposition);

            if (long2floatlinkedopenhashmap.size() == 1024) {
                long2floatlinkedopenhashmap.removeFirstFloat();
            }

            long2floatlinkedopenhashmap.put(i, f1);
            return f1;
        }
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.a(iworldreader, blockposition, true);
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        if (this.getAdjustedTemperature(blockposition) >= 0.15F) {
            return false;
        } else {
            if (blockposition.getY() >= 0 && blockposition.getY() < 256 && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);
                Fluid fluid = iworldreader.getFluid(blockposition);

                if (fluid.getType() == FluidTypes.WATER && iblockdata.getBlock() instanceof BlockFluids) {
                    if (!flag) {
                        return true;
                    }

                    boolean flag1 = iworldreader.A(blockposition.west()) && iworldreader.A(blockposition.east()) && iworldreader.A(blockposition.north()) && iworldreader.A(blockposition.south());

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean b(IWorldReader iworldreader, BlockPosition blockposition) {
        if (this.getAdjustedTemperature(blockposition) >= 0.15F) {
            return false;
        } else {
            if (blockposition.getY() >= 0 && blockposition.getY() < 256 && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);

                if (iblockdata.isAir() && Blocks.SNOW.getBlockData().canPlace(iworldreader, blockposition)) {
                    return true;
                }
            }

            return false;
        }
    }

    public void a(WorldGenStage.Decoration worldgenstage_decoration, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        if (worldgenfeatureconfigured.d == WorldGenerator.DECORATED_FLOWER) {
            this.s.add(worldgenfeatureconfigured);
        }

        ((List) this.r.get(worldgenstage_decoration)).add(worldgenfeatureconfigured);
    }

    public <C extends WorldGenCarverConfiguration> void a(WorldGenStage.Features worldgenstage_features, WorldGenCarverWrapper<C> worldgencarverwrapper) {
        ((List) this.q.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return Lists.newArrayList();
        })).add(worldgencarverwrapper);
    }

    public List<WorldGenCarverWrapper<?>> a(WorldGenStage.Features worldgenstage_features) {
        return (List) this.q.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return Lists.newArrayList();
        });
    }

    public void a(StructureFeature<?, ?> structurefeature) {
        this.u.put(structurefeature.b, structurefeature);
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return this.u.containsKey(structuregenerator);
    }

    public Iterable<StructureFeature<?, ?>> g() {
        return this.u.values();
    }

    public StructureFeature<?, ?> b(StructureFeature<?, ?> structurefeature) {
        return (StructureFeature) this.u.getOrDefault(structurefeature.b, structurefeature);
    }

    public List<WorldGenFeatureConfigured<?, ?>> h() {
        return this.s;
    }

    public List<WorldGenFeatureConfigured<?, ?>> a(WorldGenStage.Decoration worldgenstage_decoration) {
        return (List) this.r.get(worldgenstage_decoration);
    }

    public void a(WorldGenStage.Decoration worldgenstage_decoration, StructureManager structuremanager, ChunkGenerator chunkgenerator, GeneratorAccessSeed generatoraccessseed, long i, SeededRandom seededrandom, BlockPosition blockposition) {
        int j = 0;
        Iterator iterator;

        if (structuremanager.a()) {
            iterator = IRegistry.STRUCTURE_FEATURE.iterator();

            while (iterator.hasNext()) {
                StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

                if (structuregenerator.f() == worldgenstage_decoration) {
                    seededrandom.b(i, j, worldgenstage_decoration.ordinal());
                    int k = blockposition.getX() >> 4;
                    int l = blockposition.getZ() >> 4;
                    int i1 = k << 4;
                    int j1 = l << 4;

                    try {
                        structuremanager.a(SectionPosition.a(blockposition), structuregenerator).forEach((structurestart) -> {
                            structurestart.a(generatoraccessseed, structuremanager, chunkgenerator, seededrandom, new StructureBoundingBox(i1, j1, i1 + 15, j1 + 15), new ChunkCoordIntPair(k, l));
                        });
                    } catch (Exception exception) {
                        CrashReport crashreport = CrashReport.a(exception, "Feature placement");

                        crashreport.a("Feature").a("Id", (Object) IRegistry.STRUCTURE_FEATURE.getKey(structuregenerator)).a("Description", () -> {
                            return structuregenerator.toString();
                        });
                        throw new ReportedException(crashreport);
                    }

                    ++j;
                }
            }
        }

        for (iterator = ((List) this.r.get(worldgenstage_decoration)).iterator(); iterator.hasNext(); ++j) {
            WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) iterator.next();

            seededrandom.b(i, j, worldgenstage_decoration.ordinal());

            try {
                worldgenfeatureconfigured.a(generatoraccessseed, structuremanager, chunkgenerator, seededrandom, blockposition);
            } catch (Exception exception1) {
                CrashReport crashreport1 = CrashReport.a(exception1, "Feature placement");

                crashreport1.a("Feature").a("Id", (Object) IRegistry.FEATURE.getKey(worldgenfeatureconfigured.d)).a("Config", (Object) worldgenfeatureconfigured.e).a("Description", () -> {
                    return worldgenfeatureconfigured.d.toString();
                });
                throw new ReportedException(crashreport1);
            }
        }

    }

    public void a(Random random, IChunkAccess ichunkaccess, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1) {
        this.m.a(i1);
        this.m.a(random, ichunkaccess, this, i, j, k, d0, iblockdata, iblockdata1, l, i1);
    }

    public BiomeBase.EnumTemperature j() {
        return this.n == BiomeBase.Geography.OCEAN ? BiomeBase.EnumTemperature.OCEAN : ((double) this.getTemperature() < 0.2D ? BiomeBase.EnumTemperature.COLD : ((double) this.getTemperature() < 1.0D ? BiomeBase.EnumTemperature.MEDIUM : BiomeBase.EnumTemperature.WARM));
    }

    public final float k() {
        return this.h;
    }

    public final float getHumidity() {
        return this.k;
    }

    public String n() {
        if (this.g == null) {
            this.g = SystemUtils.a("biome", IRegistry.BIOME.getKey(this));
        }

        return this.g;
    }

    public final float o() {
        return this.i;
    }

    public final float getTemperature() {
        return this.j;
    }

    public BiomeFog q() {
        return this.p;
    }

    public final BiomeBase.Geography y() {
        return this.n;
    }

    public WorldGenSurfaceComposite<?> z() {
        return this.m;
    }

    public WorldGenSurfaceConfiguration A() {
        return this.m.a();
    }

    public Stream<BiomeBase.d> B() {
        return this.x.stream();
    }

    @Nullable
    public String C() {
        return this.l;
    }

    public static class d {

        public static final Codec<BiomeBase.d> a = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.FLOAT.fieldOf("temperature").forGetter((biomebase_d) -> {
                return biomebase_d.b;
            }), Codec.FLOAT.fieldOf("humidity").forGetter((biomebase_d) -> {
                return biomebase_d.c;
            }), Codec.FLOAT.fieldOf("altitude").forGetter((biomebase_d) -> {
                return biomebase_d.d;
            }), Codec.FLOAT.fieldOf("weirdness").forGetter((biomebase_d) -> {
                return biomebase_d.e;
            }), Codec.FLOAT.fieldOf("offset").forGetter((biomebase_d) -> {
                return biomebase_d.f;
            })).apply(instance, BiomeBase.d::new);
        });
        private final float b;
        private final float c;
        private final float d;
        private final float e;
        private final float f;

        public d(float f, float f1, float f2, float f3, float f4) {
            this.b = f;
            this.c = f1;
            this.d = f2;
            this.e = f3;
            this.f = f4;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                BiomeBase.d biomebase_d = (BiomeBase.d) object;

                return Float.compare(biomebase_d.b, this.b) != 0 ? false : (Float.compare(biomebase_d.c, this.c) != 0 ? false : (Float.compare(biomebase_d.d, this.d) != 0 ? false : Float.compare(biomebase_d.e, this.e) == 0));
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.b != 0.0F ? Float.floatToIntBits(this.b) : 0;

            i = 31 * i + (this.c != 0.0F ? Float.floatToIntBits(this.c) : 0);
            i = 31 * i + (this.d != 0.0F ? Float.floatToIntBits(this.d) : 0);
            i = 31 * i + (this.e != 0.0F ? Float.floatToIntBits(this.e) : 0);
            return i;
        }

        public float a(BiomeBase.d biomebase_d) {
            return (this.b - biomebase_d.b) * (this.b - biomebase_d.b) + (this.c - biomebase_d.c) * (this.c - biomebase_d.c) + (this.d - biomebase_d.d) * (this.d - biomebase_d.d) + (this.e - biomebase_d.e) * (this.e - biomebase_d.e) + (this.f - biomebase_d.f) * (this.f - biomebase_d.f);
        }
    }

    public static class a {

        @Nullable
        private WorldGenSurfaceComposite<?> a;
        @Nullable
        private BiomeBase.Precipitation b;
        @Nullable
        private BiomeBase.Geography c;
        @Nullable
        private Float d;
        @Nullable
        private Float e;
        @Nullable
        private Float f;
        @Nullable
        private Float g;
        @Nullable
        private String h;
        @Nullable
        private List<BiomeBase.d> i;
        @Nullable
        private BiomeFog j;

        public a() {}

        public <SC extends WorldGenSurfaceConfiguration> BiomeBase.a a(WorldGenSurface<SC> worldgensurface, SC sc) {
            this.a = new WorldGenSurfaceComposite<>(worldgensurface, sc);
            return this;
        }

        public BiomeBase.a a(WorldGenSurfaceComposite<?> worldgensurfacecomposite) {
            this.a = worldgensurfacecomposite;
            return this;
        }

        public BiomeBase.a a(BiomeBase.Precipitation biomebase_precipitation) {
            this.b = biomebase_precipitation;
            return this;
        }

        public BiomeBase.a a(BiomeBase.Geography biomebase_geography) {
            this.c = biomebase_geography;
            return this;
        }

        public BiomeBase.a a(float f) {
            this.d = f;
            return this;
        }

        public BiomeBase.a b(float f) {
            this.e = f;
            return this;
        }

        public BiomeBase.a c(float f) {
            this.f = f;
            return this;
        }

        public BiomeBase.a d(float f) {
            this.g = f;
            return this;
        }

        public BiomeBase.a a(@Nullable String s) {
            this.h = s;
            return this;
        }

        public BiomeBase.a a(List<BiomeBase.d> list) {
            this.i = list;
            return this;
        }

        public BiomeBase.a a(BiomeFog biomefog) {
            this.j = biomefog;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.a + ",\nprecipitation=" + this.b + ",\nbiomeCategory=" + this.c + ",\ndepth=" + this.d + ",\nscale=" + this.e + ",\ntemperature=" + this.f + ",\ndownfall=" + this.g + ",\nspecialEffects=" + this.j + ",\nparent='" + this.h + '\'' + "\n" + '}';
        }
    }

    public static class BiomeMeta extends WeightedRandom.WeightedRandomChoice {

        public static final Codec<BiomeBase.BiomeMeta> b = RecordCodecBuilder.create((instance) -> {
            return instance.group(IRegistry.ENTITY_TYPE.fieldOf("type").forGetter((biomebase_biomemeta) -> {
                return biomebase_biomemeta.c;
            }), Codec.INT.fieldOf("weight").forGetter((biomebase_biomemeta) -> {
                return biomebase_biomemeta.a;
            }), Codec.INT.fieldOf("minCount").forGetter((biomebase_biomemeta) -> {
                return biomebase_biomemeta.d;
            }), Codec.INT.fieldOf("maxCount").forGetter((biomebase_biomemeta) -> {
                return biomebase_biomemeta.e;
            })).apply(instance, BiomeBase.BiomeMeta::new);
        });
        public final EntityTypes<?> c;
        public final int d;
        public final int e;

        public BiomeMeta(EntityTypes<?> entitytypes, int i, int j, int k) {
            super(i);
            this.c = entitytypes.e() == EnumCreatureType.MISC ? EntityTypes.PIG : entitytypes;
            this.d = j;
            this.e = k;
        }

        public String toString() {
            return EntityTypes.getName(this.c) + "*(" + this.d + "-" + this.e + "):" + this.a;
        }
    }

    public static class e {

        private final double a;
        private final double b;

        public e(double d0, double d1) {
            this.a = d0;
            this.b = d1;
        }

        public double a() {
            return this.a;
        }

        public double b() {
            return this.b;
        }
    }

    public static enum Precipitation implements INamable {

        NONE("none"), RAIN("rain"), SNOW("snow");

        public static final Codec<BiomeBase.Precipitation> d = INamable.a(BiomeBase.Precipitation::values, BiomeBase.Precipitation::a);
        private static final Map<String, BiomeBase.Precipitation> e = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Precipitation::b, (biomebase_precipitation) -> {
            return biomebase_precipitation;
        }));
        private final String f;

        private Precipitation(String s) {
            this.f = s;
        }

        public String b() {
            return this.f;
        }

        public static BiomeBase.Precipitation a(String s) {
            return (BiomeBase.Precipitation) BiomeBase.Precipitation.e.get(s);
        }

        @Override
        public String getName() {
            return this.f;
        }
    }

    public static enum Geography implements INamable {

        NONE("none"), TAIGA("taiga"), EXTREME_HILLS("extreme_hills"), JUNGLE("jungle"), MESA("mesa"), PLAINS("plains"), SAVANNA("savanna"), ICY("icy"), THEEND("the_end"), BEACH("beach"), FOREST("forest"), OCEAN("ocean"), DESERT("desert"), RIVER("river"), SWAMP("swamp"), MUSHROOM("mushroom"), NETHER("nether");

        public static final Codec<BiomeBase.Geography> r = INamable.a(BiomeBase.Geography::values, BiomeBase.Geography::a);
        private static final Map<String, BiomeBase.Geography> s = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Geography::b, (biomebase_geography) -> {
            return biomebase_geography;
        }));
        private final String t;

        private Geography(String s) {
            this.t = s;
        }

        public String b() {
            return this.t;
        }

        public static BiomeBase.Geography a(String s) {
            return (BiomeBase.Geography) BiomeBase.Geography.s.get(s);
        }

        @Override
        public String getName() {
            return this.t;
        }
    }

    public static enum EnumTemperature {

        OCEAN("ocean"), COLD("cold"), MEDIUM("medium"), WARM("warm");

        private static final Map<String, BiomeBase.EnumTemperature> e = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.EnumTemperature::a, (biomebase_enumtemperature) -> {
            return biomebase_enumtemperature;
        }));
        private final String f;

        private EnumTemperature(String s) {
            this.f = s;
        }

        public String a() {
            return this.f;
        }
    }
}
