package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0D, 1.0D);
    public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter((geodeconfiguration) -> {
            return geodeconfiguration.geodeBlockSettings;
        }), GeodeLayerSettings.CODEC.fieldOf("layers").forGetter((geodeconfiguration) -> {
            return geodeconfiguration.geodeLayerSettings;
        }), GeodeCrackSettings.CODEC.fieldOf("crack").forGetter((geodeconfiguration) -> {
            return geodeconfiguration.geodeCrackSettings;
        }), GeodeConfiguration.CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35D).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.usePotentialPlacementsChance;
        }), GeodeConfiguration.CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0D).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.useAlternateLayer0Chance;
        }), Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.placementsRequireLayer0Alternate;
        }), IntProvider.codec(1, 20).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.outerWallDistance;
        }), IntProvider.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.distributionPoints;
        }), IntProvider.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.pointOffset;
        }), Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.minGenOffset;
        }), Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.maxGenOffset;
        }), GeodeConfiguration.CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05D).forGetter((geodeconfiguration) -> {
            return geodeconfiguration.noiseMultiplier;
        }), Codec.INT.fieldOf("invalid_blocks_threshold").forGetter((geodeconfiguration) -> {
            return geodeconfiguration.invalidBlocksThreshold;
        })).apply(instance, GeodeConfiguration::new);
    });
    public final GeodeBlockSettings geodeBlockSettings;
    public final GeodeLayerSettings geodeLayerSettings;
    public final GeodeCrackSettings geodeCrackSettings;
    public final double usePotentialPlacementsChance;
    public final double useAlternateLayer0Chance;
    public final boolean placementsRequireLayer0Alternate;
    public final IntProvider outerWallDistance;
    public final IntProvider distributionPoints;
    public final IntProvider pointOffset;
    public final int minGenOffset;
    public final int maxGenOffset;
    public final double noiseMultiplier;
    public final int invalidBlocksThreshold;

    public GeodeConfiguration(GeodeBlockSettings geodeblocksettings, GeodeLayerSettings geodelayersettings, GeodeCrackSettings geodecracksettings, double d0, double d1, boolean flag, IntProvider intprovider, IntProvider intprovider1, IntProvider intprovider2, int i, int j, double d2, int k) {
        this.geodeBlockSettings = geodeblocksettings;
        this.geodeLayerSettings = geodelayersettings;
        this.geodeCrackSettings = geodecracksettings;
        this.usePotentialPlacementsChance = d0;
        this.useAlternateLayer0Chance = d1;
        this.placementsRequireLayer0Alternate = flag;
        this.outerWallDistance = intprovider;
        this.distributionPoints = intprovider1;
        this.pointOffset = intprovider2;
        this.minGenOffset = i;
        this.maxGenOffset = j;
        this.noiseMultiplier = d2;
        this.invalidBlocksThreshold = k;
    }
}
