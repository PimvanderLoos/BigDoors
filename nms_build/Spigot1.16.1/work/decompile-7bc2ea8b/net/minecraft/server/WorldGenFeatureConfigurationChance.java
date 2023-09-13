package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureConfigurationChance implements WorldGenCarverConfiguration, WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureConfigurationChance> b = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("probability").withDefault(0.0F).forGetter((worldgenfeatureconfigurationchance) -> {
            return worldgenfeatureconfigurationchance.c;
        })).apply(instance, WorldGenFeatureConfigurationChance::new);
    });
    public final float c;

    public WorldGenFeatureConfigurationChance(float f) {
        this.c = f;
    }
}
