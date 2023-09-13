package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BiomeBase {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<BiomeBase> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.c.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeBase.Geography.CODEC.fieldOf("category").forGetter((biomebase) -> {
            return biomebase.biomeCategory;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        }), BiomeSettingsGeneration.CODEC.forGetter((biomebase) -> {
            return biomebase.generationSettings;
        }), BiomeSettingsMobs.CODEC.forGetter((biomebase) -> {
            return biomebase.mobSettings;
        })).apply(instance, BiomeBase::new);
    });
    public static final Codec<BiomeBase> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.c.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeBase.Geography.CODEC.fieldOf("category").forGetter((biomebase) -> {
            return biomebase.biomeCategory;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        })).apply(instance, (biomebase_c, biomebase_geography, biomefog) -> {
            return new BiomeBase(biomebase_c, biomebase_geography, biomefog, BiomeSettingsGeneration.EMPTY, BiomeSettingsMobs.EMPTY);
        });
    });
    public static final Codec<Supplier<BiomeBase>> CODEC = RegistryFileCodec.create(IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC);
    public static final Codec<List<Supplier<BiomeBase>>> LIST_CODEC = RegistryFileCodec.homogeneousList(IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC);
    private static final NoiseGenerator3 TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
    static final NoiseGenerator3 FROZEN_TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0));
    /** @deprecated */
    @Deprecated(forRemoval = true)
    public static final NoiseGenerator3 BIOME_INFO_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
    private static final int TEMPERATURE_CACHE_SIZE = 1024;
    private final BiomeBase.c climateSettings;
    private final BiomeSettingsGeneration generationSettings;
    private final BiomeSettingsMobs mobSettings;
    private final BiomeBase.Geography biomeCategory;
    private final BiomeFog specialEffects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
        return (Long2FloatLinkedOpenHashMap) SystemUtils.make(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
                protected void rehash(int i) {}
            };

            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });
    });

    BiomeBase(BiomeBase.c biomebase_c, BiomeBase.Geography biomebase_geography, BiomeFog biomefog, BiomeSettingsGeneration biomesettingsgeneration, BiomeSettingsMobs biomesettingsmobs) {
        this.climateSettings = biomebase_c;
        this.generationSettings = biomesettingsgeneration;
        this.mobSettings = biomesettingsmobs;
        this.biomeCategory = biomebase_geography;
        this.specialEffects = biomefog;
    }

    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    public BiomeSettingsMobs getMobSettings() {
        return this.mobSettings;
    }

    public BiomeBase.Precipitation getPrecipitation() {
        return this.climateSettings.precipitation;
    }

    public boolean isHumid() {
        return this.getDownfall() > 0.85F;
    }

    private float getHeightAdjustedTemperature(BlockPosition blockposition) {
        float f = this.climateSettings.temperatureModifier.modifyTemperature(blockposition, this.getBaseTemperature());

        if (blockposition.getY() > 80) {
            float f1 = (float) (BiomeBase.TEMPERATURE_NOISE.getValue((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F), false) * 8.0D);

            return f - (f1 + (float) blockposition.getY() - 80.0F) * 0.05F / 40.0F;
        } else {
            return f;
        }
    }

    /** @deprecated */
    @Deprecated
    public float getTemperature(BlockPosition blockposition) {
        long i = blockposition.asLong();
        Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = (Long2FloatLinkedOpenHashMap) this.temperatureCache.get();
        float f = long2floatlinkedopenhashmap.get(i);

        if (!Float.isNaN(f)) {
            return f;
        } else {
            float f1 = this.getHeightAdjustedTemperature(blockposition);

            if (long2floatlinkedopenhashmap.size() == 1024) {
                long2floatlinkedopenhashmap.removeFirstFloat();
            }

            long2floatlinkedopenhashmap.put(i, f1);
            return f1;
        }
    }

    public boolean shouldFreeze(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.shouldFreeze(iworldreader, blockposition, true);
    }

    public boolean shouldFreeze(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        if (this.warmEnoughToRain(blockposition)) {
            return false;
        } else {
            if (blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getBlockState(blockposition);
                Fluid fluid = iworldreader.getFluidState(blockposition);

                if (fluid.getType() == FluidTypes.WATER && iblockdata.getBlock() instanceof BlockFluids) {
                    if (!flag) {
                        return true;
                    }

                    boolean flag1 = iworldreader.isWaterAt(blockposition.west()) && iworldreader.isWaterAt(blockposition.east()) && iworldreader.isWaterAt(blockposition.north()) && iworldreader.isWaterAt(blockposition.south());

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean coldEnoughToSnow(BlockPosition blockposition) {
        return !this.warmEnoughToRain(blockposition);
    }

    public boolean warmEnoughToRain(BlockPosition blockposition) {
        return this.getTemperature(blockposition) >= 0.15F;
    }

    public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPosition blockposition) {
        return this.getTemperature(blockposition) > 0.1F;
    }

    public boolean shouldSnowGolemBurn(BlockPosition blockposition) {
        return this.getTemperature(blockposition) > 1.0F;
    }

    public boolean shouldSnow(IWorldReader iworldreader, BlockPosition blockposition) {
        if (this.warmEnoughToRain(blockposition)) {
            return false;
        } else {
            if (blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getBlockState(blockposition);

                if (iblockdata.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(iworldreader, blockposition)) {
                    return true;
                }
            }

            return false;
        }
    }

    public BiomeSettingsGeneration getGenerationSettings() {
        return this.generationSettings;
    }

    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    public int getGrassColor(double d0, double d1) {
        int i = (Integer) this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);

        return this.specialEffects.getGrassColorModifier().modifyColor(d0, d1, i);
    }

    private int getGrassColorFromTexture() {
        double d0 = (double) MathHelper.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
        double d1 = (double) MathHelper.clamp(this.climateSettings.downfall, 0.0F, 1.0F);

        return GrassColor.get(d0, d1);
    }

    public int getFoliageColor() {
        return (Integer) this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
    }

    private int getFoliageColorFromTexture() {
        double d0 = (double) MathHelper.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
        double d1 = (double) MathHelper.clamp(this.climateSettings.downfall, 0.0F, 1.0F);

        return FoliageColor.get(d0, d1);
    }

    public final float getDownfall() {
        return this.climateSettings.downfall;
    }

    public final float getBaseTemperature() {
        return this.climateSettings.temperature;
    }

    public BiomeFog getSpecialEffects() {
        return this.specialEffects;
    }

    public final int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    public final int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    public Optional<BiomeParticles> getAmbientParticle() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    public Optional<SoundEffect> getAmbientLoop() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    public Optional<CaveSoundSettings> getAmbientMood() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    public Optional<CaveSound> getAmbientAdditions() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    public Optional<Music> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }

    public final BiomeBase.Geography getBiomeCategory() {
        return this.biomeCategory;
    }

    public String toString() {
        MinecraftKey minecraftkey = RegistryGeneration.BIOME.getKey(this);

        return minecraftkey == null ? super.toString() : minecraftkey.toString();
    }

    private static class c {

        public static final MapCodec<BiomeBase.c> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(BiomeBase.Precipitation.CODEC.fieldOf("precipitation").forGetter((biomebase_c) -> {
                return biomebase_c.precipitation;
            }), Codec.FLOAT.fieldOf("temperature").forGetter((biomebase_c) -> {
                return biomebase_c.temperature;
            }), BiomeBase.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", BiomeBase.TemperatureModifier.NONE).forGetter((biomebase_c) -> {
                return biomebase_c.temperatureModifier;
            }), Codec.FLOAT.fieldOf("downfall").forGetter((biomebase_c) -> {
                return biomebase_c.downfall;
            })).apply(instance, BiomeBase.c::new);
        });
        final BiomeBase.Precipitation precipitation;
        final float temperature;
        final BiomeBase.TemperatureModifier temperatureModifier;
        final float downfall;

        c(BiomeBase.Precipitation biomebase_precipitation, float f, BiomeBase.TemperatureModifier biomebase_temperaturemodifier, float f1) {
            this.precipitation = biomebase_precipitation;
            this.temperature = f;
            this.temperatureModifier = biomebase_temperaturemodifier;
            this.downfall = f1;
        }
    }

    public static enum Geography implements INamable {

        NONE("none"), TAIGA("taiga"), EXTREME_HILLS("extreme_hills"), JUNGLE("jungle"), MESA("mesa"), PLAINS("plains"), SAVANNA("savanna"), ICY("icy"), THEEND("the_end"), BEACH("beach"), FOREST("forest"), OCEAN("ocean"), DESERT("desert"), RIVER("river"), SWAMP("swamp"), MUSHROOM("mushroom"), NETHER("nether"), UNDERGROUND("underground"), MOUNTAIN("mountain");

        public static final Codec<BiomeBase.Geography> CODEC = INamable.fromEnum(BiomeBase.Geography::values, BiomeBase.Geography::byName);
        private static final Map<String, BiomeBase.Geography> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Geography::getName, (biomebase_geography) -> {
            return biomebase_geography;
        }));
        private final String name;

        private Geography(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static BiomeBase.Geography byName(String s) {
            return (BiomeBase.Geography) BiomeBase.Geography.BY_NAME.get(s);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static enum Precipitation implements INamable {

        NONE("none"), RAIN("rain"), SNOW("snow");

        public static final Codec<BiomeBase.Precipitation> CODEC = INamable.fromEnum(BiomeBase.Precipitation::values, BiomeBase.Precipitation::byName);
        private static final Map<String, BiomeBase.Precipitation> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Precipitation::getName, (biomebase_precipitation) -> {
            return biomebase_precipitation;
        }));
        private final String name;

        private Precipitation(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static BiomeBase.Precipitation byName(String s) {
            return (BiomeBase.Precipitation) BiomeBase.Precipitation.BY_NAME.get(s);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static enum TemperatureModifier implements INamable {

        NONE("none") {
            @Override
            public float modifyTemperature(BlockPosition blockposition, float f) {
                return f;
            }
        },
        FROZEN("frozen") {
            @Override
            public float modifyTemperature(BlockPosition blockposition, float f) {
                double d0 = BiomeBase.FROZEN_TEMPERATURE_NOISE.getValue((double) blockposition.getX() * 0.05D, (double) blockposition.getZ() * 0.05D, false) * 7.0D;
                double d1 = BiomeBase.BIOME_INFO_NOISE.getValue((double) blockposition.getX() * 0.2D, (double) blockposition.getZ() * 0.2D, false);
                double d2 = d0 + d1;

                if (d2 < 0.3D) {
                    double d3 = BiomeBase.BIOME_INFO_NOISE.getValue((double) blockposition.getX() * 0.09D, (double) blockposition.getZ() * 0.09D, false);

                    if (d3 < 0.8D) {
                        return 0.2F;
                    }
                }

                return f;
            }
        };

        private final String name;
        public static final Codec<BiomeBase.TemperatureModifier> CODEC = INamable.fromEnum(BiomeBase.TemperatureModifier::values, BiomeBase.TemperatureModifier::byName);
        private static final Map<String, BiomeBase.TemperatureModifier> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.TemperatureModifier::getName, (biomebase_temperaturemodifier) -> {
            return biomebase_temperaturemodifier;
        }));

        public abstract float modifyTemperature(BlockPosition blockposition, float f);

        TemperatureModifier(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static BiomeBase.TemperatureModifier byName(String s) {
            return (BiomeBase.TemperatureModifier) BiomeBase.TemperatureModifier.BY_NAME.get(s);
        }
    }

    public static class a {

        @Nullable
        private BiomeBase.Precipitation precipitation;
        @Nullable
        private BiomeBase.Geography biomeCategory;
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

        public BiomeBase.a precipitation(BiomeBase.Precipitation biomebase_precipitation) {
            this.precipitation = biomebase_precipitation;
            return this;
        }

        public BiomeBase.a biomeCategory(BiomeBase.Geography biomebase_geography) {
            this.biomeCategory = biomebase_geography;
            return this;
        }

        public BiomeBase.a temperature(float f) {
            this.temperature = f;
            return this;
        }

        public BiomeBase.a downfall(float f) {
            this.downfall = f;
            return this;
        }

        public BiomeBase.a specialEffects(BiomeFog biomefog) {
            this.specialEffects = biomefog;
            return this;
        }

        public BiomeBase.a mobSpawnSettings(BiomeSettingsMobs biomesettingsmobs) {
            this.mobSpawnSettings = biomesettingsmobs;
            return this;
        }

        public BiomeBase.a generationSettings(BiomeSettingsGeneration biomesettingsgeneration) {
            this.generationSettings = biomesettingsgeneration;
            return this;
        }

        public BiomeBase.a temperatureAdjustment(BiomeBase.TemperatureModifier biomebase_temperaturemodifier) {
            this.temperatureModifier = biomebase_temperaturemodifier;
            return this;
        }

        public BiomeBase build() {
            if (this.precipitation != null && this.biomeCategory != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
                return new BiomeBase(new BiomeBase.c(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
            } else {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
        }

        public String toString() {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
        }
    }
}
