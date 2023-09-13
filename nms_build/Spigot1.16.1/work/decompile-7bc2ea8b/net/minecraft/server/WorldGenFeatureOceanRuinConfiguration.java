package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureOceanRuinConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureOceanRuinConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureOceanRuin.Temperature.c.fieldOf("biome_temp").forGetter((worldgenfeatureoceanruinconfiguration) -> {
            return worldgenfeatureoceanruinconfiguration.b;
        }), Codec.FLOAT.fieldOf("large_probability").forGetter((worldgenfeatureoceanruinconfiguration) -> {
            return worldgenfeatureoceanruinconfiguration.c;
        }), Codec.FLOAT.fieldOf("cluster_probability").forGetter((worldgenfeatureoceanruinconfiguration) -> {
            return worldgenfeatureoceanruinconfiguration.d;
        })).apply(instance, WorldGenFeatureOceanRuinConfiguration::new);
    });
    public final WorldGenFeatureOceanRuin.Temperature b;
    public final float c;
    public final float d;

    public WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature worldgenfeatureoceanruin_temperature, float f, float f1) {
        this.b = worldgenfeatureoceanruin_temperature;
        this.c = f;
        this.d = f1;
    }
}
