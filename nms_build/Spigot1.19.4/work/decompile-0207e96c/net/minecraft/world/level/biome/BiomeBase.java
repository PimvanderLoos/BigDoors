package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
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

public final class BiomeBase {

    public static final Codec<BiomeBase> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.ClimateSettings.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        }), BiomeSettingsGeneration.CODEC.forGetter((biomebase) -> {
            return biomebase.generationSettings;
        }), BiomeSettingsMobs.CODEC.forGetter((biomebase) -> {
            return biomebase.mobSettings;
        })).apply(instance, BiomeBase::new);
    });
    public static final Codec<BiomeBase> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.ClimateSettings.CODEC.forGetter((biomebase) -> {
            return biomebase.climateSettings;
        }), BiomeFog.CODEC.fieldOf("effects").forGetter((biomebase) -> {
            return biomebase.specialEffects;
        })).apply(instance, (biomebase_climatesettings, biomefog) -> {
            return new BiomeBase(biomebase_climatesettings, biomefog, BiomeSettingsGeneration.EMPTY, BiomeSettingsMobs.EMPTY);
        });
    });
    public static final Codec<Holder<BiomeBase>> CODEC = RegistryFileCodec.create(Registries.BIOME, BiomeBase.DIRECT_CODEC);
    public static final Codec<HolderSet<BiomeBase>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.BIOME, BiomeBase.DIRECT_CODEC);
    private static final NoiseGenerator3 TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
    static final NoiseGenerator3 FROZEN_TEMPERATURE_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0));
    /** @deprecated */
    @Deprecated(forRemoval = true)
    public static final NoiseGenerator3 BIOME_INFO_NOISE = new NoiseGenerator3(new SeededRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
    private static final int TEMPERATURE_CACHE_SIZE = 1024;
    public final BiomeBase.ClimateSettings climateSettings;
    private final BiomeSettingsGeneration generationSettings;
    private final BiomeSettingsMobs mobSettings;
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

    BiomeBase(BiomeBase.ClimateSettings biomebase_climatesettings, BiomeFog biomefog, BiomeSettingsGeneration biomesettingsgeneration, BiomeSettingsMobs biomesettingsmobs) {
        this.climateSettings = biomebase_climatesettings;
        this.generationSettings = biomesettingsgeneration;
        this.mobSettings = biomesettingsmobs;
        this.specialEffects = biomefog;
    }

    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    public BiomeSettingsMobs getMobSettings() {
        return this.mobSettings;
    }

    public boolean hasPrecipitation() {
        return this.climateSettings.hasPrecipitation();
    }

    public BiomeBase.Precipitation getPrecipitationAt(BlockPosition blockposition) {
        return !this.hasPrecipitation() ? BiomeBase.Precipitation.NONE : (this.coldEnoughToSnow(blockposition) ? BiomeBase.Precipitation.SNOW : BiomeBase.Precipitation.RAIN);
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

    public boolean shouldSnow(IWorldReader iworldreader, BlockPosition blockposition) {
        if (this.warmEnoughToRain(blockposition)) {
            return false;
        } else {
            if (blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getBlockState(blockposition);

                if ((iblockdata.isAir() || iblockdata.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive(iworldreader, blockposition)) {
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

    public float getBaseTemperature() {
        return this.climateSettings.temperature;
    }

    public BiomeFog getSpecialEffects() {
        return this.specialEffects;
    }

    public int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    public int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    public Optional<BiomeParticles> getAmbientParticle() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    public Optional<Holder<SoundEffect>> getAmbientLoop() {
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

    public static record ClimateSettings(boolean hasPrecipitation, float temperature, BiomeBase.TemperatureModifier temperatureModifier, float downfall) {

        public static final MapCodec<BiomeBase.ClimateSettings> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.BOOL.fieldOf("has_precipitation").forGetter((biomebase_climatesettings) -> {
                return biomebase_climatesettings.hasPrecipitation;
            }), Codec.FLOAT.fieldOf("temperature").forGetter((biomebase_climatesettings) -> {
                return biomebase_climatesettings.temperature;
            }), BiomeBase.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", BiomeBase.TemperatureModifier.NONE).forGetter((biomebase_climatesettings) -> {
                return biomebase_climatesettings.temperatureModifier;
            }), Codec.FLOAT.fieldOf("downfall").forGetter((biomebase_climatesettings) -> {
                return biomebase_climatesettings.downfall;
            })).apply(instance, BiomeBase.ClimateSettings::new);
        });
    }

    public static enum Precipitation {

        NONE, RAIN, SNOW;

        private Precipitation() {}
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
        public static final Codec<BiomeBase.TemperatureModifier> CODEC = INamable.fromEnum(BiomeBase.TemperatureModifier::values);

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
    }

    public static class a {

        private boolean hasPrecipitation = true;
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

        public BiomeBase.a hasPrecipitation(boolean flag) {
            this.hasPrecipitation = flag;
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
            if (this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
                return new BiomeBase(new BiomeBase.ClimateSettings(this.hasPrecipitation, this.temperature, this.temperatureModifier, this.downfall), this.specialEffects, this.generationSettings, this.mobSpawnSettings);
            } else {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
        }

        public String toString() {
            return "BiomeBuilder{\nhasPrecipitation=" + this.hasPrecipitation + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
        }
    }
}
