package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CanyonCarverConfiguration extends WorldGenCarverConfiguration {

    public static final Codec<CanyonCarverConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenCarverConfiguration.CODEC.forGetter((canyoncarverconfiguration) -> {
            return canyoncarverconfiguration;
        }), FloatProvider.CODEC.fieldOf("vertical_rotation").forGetter((canyoncarverconfiguration) -> {
            return canyoncarverconfiguration.verticalRotation;
        }), CanyonCarverConfiguration.a.CODEC.fieldOf("shape").forGetter((canyoncarverconfiguration) -> {
            return canyoncarverconfiguration.shape;
        })).apply(instance, CanyonCarverConfiguration::new);
    });
    public final FloatProvider verticalRotation;
    public final CanyonCarverConfiguration.a shape;

    public CanyonCarverConfiguration(float f, HeightProvider heightprovider, FloatProvider floatprovider, VerticalAnchor verticalanchor, CarverDebugSettings carverdebugsettings, HolderSet<Block> holderset, FloatProvider floatprovider1, CanyonCarverConfiguration.a canyoncarverconfiguration_a) {
        super(f, heightprovider, floatprovider, verticalanchor, carverdebugsettings, holderset);
        this.verticalRotation = floatprovider1;
        this.shape = canyoncarverconfiguration_a;
    }

    public CanyonCarverConfiguration(WorldGenCarverConfiguration worldgencarverconfiguration, FloatProvider floatprovider, CanyonCarverConfiguration.a canyoncarverconfiguration_a) {
        this(worldgencarverconfiguration.probability, worldgencarverconfiguration.y, worldgencarverconfiguration.yScale, worldgencarverconfiguration.lavaLevel, worldgencarverconfiguration.debugSettings, worldgencarverconfiguration.replaceable, floatprovider, canyoncarverconfiguration_a);
    }

    public static class a {

        public static final Codec<CanyonCarverConfiguration.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(FloatProvider.CODEC.fieldOf("distance_factor").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.distanceFactor;
            }), FloatProvider.CODEC.fieldOf("thickness").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.thickness;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("width_smoothness").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.widthSmoothness;
            }), FloatProvider.CODEC.fieldOf("horizontal_radius_factor").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.horizontalRadiusFactor;
            }), Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.verticalRadiusDefaultFactor;
            }), Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter((canyoncarverconfiguration_a) -> {
                return canyoncarverconfiguration_a.verticalRadiusCenterFactor;
            })).apply(instance, CanyonCarverConfiguration.a::new);
        });
        public final FloatProvider distanceFactor;
        public final FloatProvider thickness;
        public final int widthSmoothness;
        public final FloatProvider horizontalRadiusFactor;
        public final float verticalRadiusDefaultFactor;
        public final float verticalRadiusCenterFactor;

        public a(FloatProvider floatprovider, FloatProvider floatprovider1, int i, FloatProvider floatprovider2, float f, float f1) {
            this.widthSmoothness = i;
            this.horizontalRadiusFactor = floatprovider2;
            this.verticalRadiusDefaultFactor = f;
            this.verticalRadiusCenterFactor = f1;
            this.distanceFactor = floatprovider;
            this.thickness = floatprovider1;
        }
    }
}
