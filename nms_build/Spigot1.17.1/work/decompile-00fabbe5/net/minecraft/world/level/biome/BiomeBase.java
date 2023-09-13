package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BiomeBase {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<BiomeBase> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.d.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeBase.Geography.CODEC.fieldOf("category").forGetter((biomebase) -> {
            return biomebase.biomeCategory;
        }), Codec.FLOAT.fieldOf("depth").forGetter((biomebase) -> {
            return biomebase.depth;
        }), Codec.FLOAT.fieldOf("scale").forGetter((biomebase) -> {
            return biomebase.scale;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        }), BiomeSettingsGeneration.CODEC.forGetter((biomebase) -> {
            return biomebase.generationSettings;
        }), BiomeSettingsMobs.CODEC.forGetter((biomebase) -> {
            return biomebase.mobSettings;
        })).apply(instance, BiomeBase::new);
    });
    public static final Codec<BiomeBase> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.d.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeBase.Geography.CODEC.fieldOf("category").forGetter((biomebase) -> {
            return biomebase.biomeCategory;
        }), Codec.FLOAT.fieldOf("depth").forGetter((biomebase) -> {
            return biomebase.depth;
        }), Codec.FLOAT.fieldOf("scale").forGetter((biomebase) -> {
            return biomebase.scale;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        })).apply(instance, (biomebase_d, biomebase_geography, ofloat, ofloat1, biomefog) -> {
            return new BiomeBase(biomebase_d, biomebase_geography, ofloat, ofloat1, biomefog, BiomeSettingsGeneration.EMPTY, BiomeSettingsMobs.EMPTY);
        });
    });
    public static final Codec<Supplier<BiomeBase>> CODEC = RegistryFileCodec.a(IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC);
    public static final Codec<List<Supplier<BiomeBase>>> LIST_CODEC = RegistryFileCodec.b(IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC);
    private final Map<Integer, List<StructureGenerator<?>>> structuresByStep;
    private static final NoiseGenerator3 TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(1234L), ImmutableList.of(0));
    static final NoiseGenerator3 FROZEN_TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(3456L), ImmutableList.of(-2, -1, 0));
    public static final NoiseGenerator3 BIOME_INFO_NOISE = new NoiseGenerator3(new SeededRandom(2345L), ImmutableList.of(0));
    private static final int TEMPERATURE_CACHE_SIZE = 1024;
    private final BiomeBase.d climateSettings;
    private final BiomeSettingsGeneration generationSettings;
    private final BiomeSettingsMobs mobSettings;
    private final float depth;
    private final float scale;
    private final BiomeBase.Geography biomeCategory;
    private final BiomeFog specialEffects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache;

    BiomeBase(BiomeBase.d biomebase_d, BiomeBase.Geography biomebase_geography, float f, float f1, BiomeFog biomefog, BiomeSettingsGeneration biomesettingsgeneration, BiomeSettingsMobs biomesettingsmobs) {
        this.structuresByStep = (Map) IRegistry.STRUCTURE_FEATURE.g().collect(Collectors.groupingBy((structuregenerator) -> {
            return structuregenerator.d().ordinal();
        }));
        this.temperatureCache = ThreadLocal.withInitial(() -> {
            return (Long2FloatLinkedOpenHashMap) SystemUtils.a(() -> {
                Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
                    protected void rehash(int i) {}
                };

                long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
                return long2floatlinkedopenhashmap;
            });
        });
        this.climateSettings = biomebase_d;
        this.generationSettings = biomesettingsgeneration;
        this.mobSettings = biomesettingsmobs;
        this.biomeCategory = biomebase_geography;
        this.depth = f;
        this.scale = f1;
        this.specialEffects = biomefog;
    }

    public int a() {
        return this.specialEffects.d();
    }

    public BiomeSettingsMobs b() {
        return this.mobSettings;
    }

    public BiomeBase.Precipitation c() {
        return this.climateSettings.precipitation;
    }

    public boolean d() {
        return this.getHumidity() > 0.85F;
    }

    private float c(BlockPosition blockposition) {
        float f = this.climateSettings.temperatureModifier.a(blockposition, this.k());

        if (blockposition.getY() > 64) {
            float f1 = (float) (BiomeBase.TEMPERATURE_NOISE.a((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F), false) * 4.0D);

            return f - (f1 + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return f;
        }
    }

    public final float getAdjustedTemperature(BlockPosition blockposition) {
        long i = blockposition.asLong();
        Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = (Long2FloatLinkedOpenHashMap) this.temperatureCache.get();
        float f = long2floatlinkedopenhashmap.get(i);

        if (!Float.isNaN(f)) {
            return f;
        } else {
            float f1 = this.c(blockposition);

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
            if (blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);
                Fluid fluid = iworldreader.getFluid(blockposition);

                if (fluid.getType() == FluidTypes.WATER && iblockdata.getBlock() instanceof BlockFluids) {
                    if (!flag) {
                        return true;
                    }

                    boolean flag1 = iworldreader.B(blockposition.west()) && iworldreader.B(blockposition.east()) && iworldreader.B(blockposition.north()) && iworldreader.B(blockposition.south());

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean b(BlockPosition blockposition) {
        return this.getAdjustedTemperature(blockposition) < 0.15F;
    }

    public boolean b(IWorldReader iworldreader, BlockPosition blockposition) {
        if (!this.b(blockposition)) {
            return false;
        } else {
            if (blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);

                if (iblockdata.isAir() && Blocks.SNOW.getBlockData().canPlace(iworldreader, blockposition)) {
                    return true;
                }
            }

            return false;
        }
    }

    public BiomeSettingsGeneration e() {
        return this.generationSettings;
    }

    public void a(StructureManager structuremanager, ChunkGenerator chunkgenerator, RegionLimitedWorldAccess regionlimitedworldaccess, long i, SeededRandom seededrandom, BlockPosition blockposition) {
        List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> list = this.generationSettings.c();
        IRegistry<WorldGenFeatureConfigured<?, ?>> iregistry = regionlimitedworldaccess.t().d(IRegistry.CONFIGURED_FEATURE_REGISTRY);
        IRegistry<StructureGenerator<?>> iregistry1 = regionlimitedworldaccess.t().d(IRegistry.STRUCTURE_FEATURE_REGISTRY);
        int j = WorldGenStage.Decoration.values().length;

        for (int k = 0; k < j; ++k) {
            int l = 0;
            CrashReportSystemDetails crashreportsystemdetails;

            if (structuremanager.a()) {
                List<StructureGenerator<?>> list1 = (List) this.structuresByStep.getOrDefault(k, Collections.emptyList());

                for (Iterator iterator = list1.iterator(); iterator.hasNext(); ++l) {
                    StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

                    seededrandom.b(i, l, k);
                    int i1 = SectionPosition.a(blockposition.getX());
                    int j1 = SectionPosition.a(blockposition.getZ());
                    int k1 = SectionPosition.c(i1);
                    int l1 = SectionPosition.c(j1);
                    Supplier supplier = () -> {
                        Optional optional = iregistry1.c((Object) structuregenerator).map(Object::toString);

                        Objects.requireNonNull(structuregenerator);
                        return (String) optional.orElseGet(structuregenerator::toString);
                    };

                    try {
                        int i2 = regionlimitedworldaccess.getMinBuildHeight() + 1;
                        int j2 = regionlimitedworldaccess.getMaxBuildHeight() - 1;

                        regionlimitedworldaccess.a(supplier);
                        structuremanager.a(SectionPosition.a(blockposition), structuregenerator).forEach((structurestart) -> {
                            structurestart.a(regionlimitedworldaccess, structuremanager, chunkgenerator, seededrandom, new StructureBoundingBox(k1, i2, l1, k1 + 15, j2, l1 + 15), new ChunkCoordIntPair(i1, j1));
                        });
                    } catch (Exception exception) {
                        CrashReport crashreport = CrashReport.a(exception, "Feature placement");

                        crashreportsystemdetails = crashreport.a("Feature");
                        Objects.requireNonNull(supplier);
                        crashreportsystemdetails.a("Description", supplier::get);
                        throw new ReportedException(crashreport);
                    }
                }
            }

            if (list.size() > k) {
                for (Iterator iterator1 = ((List) list.get(k)).iterator(); iterator1.hasNext(); ++l) {
                    Supplier<WorldGenFeatureConfigured<?, ?>> supplier1 = (Supplier) iterator1.next();
                    WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) supplier1.get();
                    Supplier<String> supplier2 = () -> {
                        Optional optional = iregistry.c((Object) worldgenfeatureconfigured).map(Object::toString);

                        Objects.requireNonNull(worldgenfeatureconfigured);
                        return (String) optional.orElseGet(worldgenfeatureconfigured::toString);
                    };

                    seededrandom.b(i, l, k);

                    try {
                        regionlimitedworldaccess.a(supplier2);
                        worldgenfeatureconfigured.a(regionlimitedworldaccess, chunkgenerator, seededrandom, blockposition);
                    } catch (Exception exception1) {
                        CrashReport crashreport1 = CrashReport.a(exception1, "Feature placement");

                        crashreportsystemdetails = crashreport1.a("Feature");
                        Objects.requireNonNull(supplier2);
                        crashreportsystemdetails.a("Description", supplier2::get);
                        throw new ReportedException(crashreport1);
                    }
                }
            }
        }

        regionlimitedworldaccess.a((Supplier) null);
    }

    public int f() {
        return this.specialEffects.a();
    }

    public int a(double d0, double d1) {
        int i = (Integer) this.specialEffects.f().orElseGet(this::u);

        return this.specialEffects.g().a(d0, d1, i);
    }

    private int u() {
        double d0 = (double) MathHelper.a(this.climateSettings.temperature, 0.0F, 1.0F);
        double d1 = (double) MathHelper.a(this.climateSettings.downfall, 0.0F, 1.0F);

        return GrassColor.a(d0, d1);
    }

    public int g() {
        return (Integer) this.specialEffects.e().orElseGet(this::v);
    }

    private int v() {
        double d0 = (double) MathHelper.a(this.climateSettings.temperature, 0.0F, 1.0F);
        double d1 = (double) MathHelper.a(this.climateSettings.downfall, 0.0F, 1.0F);

        return FoliageColor.a(d0, d1);
    }

    public void a(Random random, IChunkAccess ichunkaccess, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1) {
        WorldGenSurfaceComposite<?> worldgensurfacecomposite = (WorldGenSurfaceComposite) this.generationSettings.d().get();

        worldgensurfacecomposite.a(j1);
        worldgensurfacecomposite.a(random, ichunkaccess, this, i, j, k, d0, iblockdata, iblockdata1, l, i1, j1);
    }

    public final float h() {
        return this.depth;
    }

    public final float getHumidity() {
        return this.climateSettings.downfall;
    }

    public final float j() {
        return this.scale;
    }

    public final float k() {
        return this.climateSettings.temperature;
    }

    public BiomeFog l() {
        return this.specialEffects;
    }

    public final int m() {
        return this.specialEffects.b();
    }

    public final int n() {
        return this.specialEffects.c();
    }

    public Optional<BiomeParticles> o() {
        return this.specialEffects.h();
    }

    public Optional<SoundEffect> p() {
        return this.specialEffects.i();
    }

    public Optional<CaveSoundSettings> q() {
        return this.specialEffects.j();
    }

    public Optional<CaveSound> r() {
        return this.specialEffects.k();
    }

    public Optional<Music> s() {
        return this.specialEffects.l();
    }

    public final BiomeBase.Geography t() {
        return this.biomeCategory;
    }

    public String toString() {
        MinecraftKey minecraftkey = RegistryGeneration.BIOME.getKey(this);

        return minecraftkey == null ? super.toString() : minecraftkey.toString();
    }

    private static class d {

        public static final MapCodec<BiomeBase.d> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(BiomeBase.Precipitation.CODEC.fieldOf("precipitation").forGetter((biomebase_d) -> {
                return biomebase_d.precipitation;
            }), Codec.FLOAT.fieldOf("temperature").forGetter((biomebase_d) -> {
                return biomebase_d.temperature;
            }), BiomeBase.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", BiomeBase.TemperatureModifier.NONE).forGetter((biomebase_d) -> {
                return biomebase_d.temperatureModifier;
            }), Codec.FLOAT.fieldOf("downfall").forGetter((biomebase_d) -> {
                return biomebase_d.downfall;
            })).apply(instance, BiomeBase.d::new);
        });
        final BiomeBase.Precipitation precipitation;
        final float temperature;
        final BiomeBase.TemperatureModifier temperatureModifier;
        final float downfall;

        d(BiomeBase.Precipitation biomebase_precipitation, float f, BiomeBase.TemperatureModifier biomebase_temperaturemodifier, float f1) {
            this.precipitation = biomebase_precipitation;
            this.temperature = f;
            this.temperatureModifier = biomebase_temperaturemodifier;
            this.downfall = f1;
        }
    }

    public static enum Geography implements INamable {

        NONE("none"), TAIGA("taiga"), EXTREME_HILLS("extreme_hills"), JUNGLE("jungle"), MESA("mesa"), PLAINS("plains"), SAVANNA("savanna"), ICY("icy"), THEEND("the_end"), BEACH("beach"), FOREST("forest"), OCEAN("ocean"), DESERT("desert"), RIVER("river"), SWAMP("swamp"), MUSHROOM("mushroom"), NETHER("nether"), UNDERGROUND("underground");

        public static final Codec<BiomeBase.Geography> CODEC = INamable.a(BiomeBase.Geography::values, BiomeBase.Geography::a);
        private static final Map<String, BiomeBase.Geography> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Geography::a, (biomebase_geography) -> {
            return biomebase_geography;
        }));
        private final String name;

        private Geography(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        public static BiomeBase.Geography a(String s) {
            return (BiomeBase.Geography) BiomeBase.Geography.BY_NAME.get(s);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static enum Precipitation implements INamable {

        NONE("none"), RAIN("rain"), SNOW("snow");

        public static final Codec<BiomeBase.Precipitation> CODEC = INamable.a(BiomeBase.Precipitation::values, BiomeBase.Precipitation::a);
        private static final Map<String, BiomeBase.Precipitation> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Precipitation::a, (biomebase_precipitation) -> {
            return biomebase_precipitation;
        }));
        private final String name;

        private Precipitation(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        public static BiomeBase.Precipitation a(String s) {
            return (BiomeBase.Precipitation) BiomeBase.Precipitation.BY_NAME.get(s);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static enum TemperatureModifier implements INamable {

        NONE("none") {
            @Override
            public float a(BlockPosition blockposition, float f) {
                return f;
            }
        },
        FROZEN("frozen") {
            @Override
            public float a(BlockPosition blockposition, float f) {
                double d0 = BiomeBase.FROZEN_TEMPERATURE_NOISE.a((double) blockposition.getX() * 0.05D, (double) blockposition.getZ() * 0.05D, false) * 7.0D;
                double d1 = BiomeBase.BIOME_INFO_NOISE.a((double) blockposition.getX() * 0.2D, (double) blockposition.getZ() * 0.2D, false);
                double d2 = d0 + d1;

                if (d2 < 0.3D) {
                    double d3 = BiomeBase.BIOME_INFO_NOISE.a((double) blockposition.getX() * 0.09D, (double) blockposition.getZ() * 0.09D, false);

                    if (d3 < 0.8D) {
                        return 0.2F;
                    }
                }

                return f;
            }
        };

        private final String name;
        public static final Codec<BiomeBase.TemperatureModifier> CODEC = INamable.a(BiomeBase.TemperatureModifier::values, BiomeBase.TemperatureModifier::a);
        private static final Map<String, BiomeBase.TemperatureModifier> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.TemperatureModifier::a, (biomebase_temperaturemodifier) -> {
            return biomebase_temperaturemodifier;
        }));

        public abstract float a(BlockPosition blockposition, float f);

        TemperatureModifier(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static BiomeBase.TemperatureModifier a(String s) {
            return (BiomeBase.TemperatureModifier) BiomeBase.TemperatureModifier.BY_NAME.get(s);
        }
    }

    public static class c {

        public static final Codec<BiomeBase.c> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.floatRange(-2.0F, 2.0F).fieldOf("temperature").forGetter((biomebase_c) -> {
                return biomebase_c.temperature;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("humidity").forGetter((biomebase_c) -> {
                return biomebase_c.humidity;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("altitude").forGetter((biomebase_c) -> {
                return biomebase_c.altitude;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("weirdness").forGetter((biomebase_c) -> {
                return biomebase_c.weirdness;
            }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter((biomebase_c) -> {
                return biomebase_c.offset;
            })).apply(instance, BiomeBase.c::new);
        });
        private final float temperature;
        private final float humidity;
        private final float altitude;
        private final float weirdness;
        private final float offset;

        public c(float f, float f1, float f2, float f3, float f4) {
            this.temperature = f;
            this.humidity = f1;
            this.altitude = f2;
            this.weirdness = f3;
            this.offset = f4;
        }

        public String toString() {
            return "temp: " + this.temperature + ", hum: " + this.humidity + ", alt: " + this.altitude + ", weird: " + this.weirdness + ", offset: " + this.offset;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                BiomeBase.c biomebase_c = (BiomeBase.c) object;

                return Float.compare(biomebase_c.temperature, this.temperature) != 0 ? false : (Float.compare(biomebase_c.humidity, this.humidity) != 0 ? false : (Float.compare(biomebase_c.altitude, this.altitude) != 0 ? false : Float.compare(biomebase_c.weirdness, this.weirdness) == 0));
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.temperature != 0.0F ? Float.floatToIntBits(this.temperature) : 0;

            i = 31 * i + (this.humidity != 0.0F ? Float.floatToIntBits(this.humidity) : 0);
            i = 31 * i + (this.altitude != 0.0F ? Float.floatToIntBits(this.altitude) : 0);
            i = 31 * i + (this.weirdness != 0.0F ? Float.floatToIntBits(this.weirdness) : 0);
            return i;
        }

        public float a(BiomeBase.c biomebase_c) {
            return (this.temperature - biomebase_c.temperature) * (this.temperature - biomebase_c.temperature) + (this.humidity - biomebase_c.humidity) * (this.humidity - biomebase_c.humidity) + (this.altitude - biomebase_c.altitude) * (this.altitude - biomebase_c.altitude) + (this.weirdness - biomebase_c.weirdness) * (this.weirdness - biomebase_c.weirdness) + (this.offset - biomebase_c.offset) * (this.offset - biomebase_c.offset);
        }
    }

    public static class a {

        @Nullable
        private BiomeBase.Precipitation precipitation;
        @Nullable
        private BiomeBase.Geography biomeCategory;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        private BiomeBase.TemperatureModifier temperatureModifier;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeFog specialEffects;
        @Nullable
        private BiomeSettingsMobs mobSpawnSettings;
        @Nullable
        private BiomeSettingsGeneration generationSettings;

        public a() {
            this.temperatureModifier = BiomeBase.TemperatureModifier.NONE;
        }

        public BiomeBase.a a(BiomeBase.Precipitation biomebase_precipitation) {
            this.precipitation = biomebase_precipitation;
            return this;
        }

        public BiomeBase.a a(BiomeBase.Geography biomebase_geography) {
            this.biomeCategory = biomebase_geography;
            return this;
        }

        public BiomeBase.a a(float f) {
            this.depth = f;
            return this;
        }

        public BiomeBase.a b(float f) {
            this.scale = f;
            return this;
        }

        public BiomeBase.a c(float f) {
            this.temperature = f;
            return this;
        }

        public BiomeBase.a d(float f) {
            this.downfall = f;
            return this;
        }

        public BiomeBase.a a(BiomeFog biomefog) {
            this.specialEffects = biomefog;
            return this;
        }

        public BiomeBase.a a(BiomeSettingsMobs biomesettingsmobs) {
            this.mobSpawnSettings = biomesettingsmobs;
            return this;
        }

        public BiomeBase.a a(BiomeSettingsGeneration biomesettingsgeneration) {
            this.generationSettings = biomesettingsgeneration;
            return this;
        }

        public BiomeBase.a a(BiomeBase.TemperatureModifier biomebase_temperaturemodifier) {
            this.temperatureModifier = biomebase_temperaturemodifier;
            return this;
        }

        public BiomeBase a() {
            if (this.precipitation != null && this.biomeCategory != null && this.depth != null && this.scale != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
                return new BiomeBase(new BiomeBase.d(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.depth, this.scale, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
            } else {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
        }

        public String toString() {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
        }
    }
}
