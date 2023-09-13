package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureSeaGrassConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureSeaGrassConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgenfeatureseagrassconfiguration) -> {
            return worldgenfeatureseagrassconfiguration.b;
        }), Codec.DOUBLE.fieldOf("probability").forGetter((worldgenfeatureseagrassconfiguration) -> {
            return worldgenfeatureseagrassconfiguration.c;
        })).apply(instance, WorldGenFeatureSeaGrassConfiguration::new);
    });
    public final int b;
    public final double c;

    public WorldGenFeatureSeaGrassConfiguration(int i, double d0) {
        this.b = i;
        this.c = d0;
    }
}
