package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerSettings {

    private static final Codec<Double> LAYER_RANGE = Codec.doubleRange(0.01D, 50.0D);
    public static final Codec<GeodeLayerSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(GeodeLayerSettings.LAYER_RANGE.fieldOf("filling").orElse(1.7D).forGetter((geodelayersettings) -> {
            return geodelayersettings.filling;
        }), GeodeLayerSettings.LAYER_RANGE.fieldOf("inner_layer").orElse(2.2D).forGetter((geodelayersettings) -> {
            return geodelayersettings.innerLayer;
        }), GeodeLayerSettings.LAYER_RANGE.fieldOf("middle_layer").orElse(3.2D).forGetter((geodelayersettings) -> {
            return geodelayersettings.middleLayer;
        }), GeodeLayerSettings.LAYER_RANGE.fieldOf("outer_layer").orElse(4.2D).forGetter((geodelayersettings) -> {
            return geodelayersettings.outerLayer;
        })).apply(instance, GeodeLayerSettings::new);
    });
    public final double filling;
    public final double innerLayer;
    public final double middleLayer;
    public final double outerLayer;

    public GeodeLayerSettings(double d0, double d1, double d2, double d3) {
        this.filling = d0;
        this.innerLayer = d1;
        this.middleLayer = d2;
        this.outerLayer = d3;
    }
}
