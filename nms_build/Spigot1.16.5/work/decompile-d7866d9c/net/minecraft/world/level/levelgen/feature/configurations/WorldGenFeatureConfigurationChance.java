package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverConfiguration;

public class WorldGenFeatureConfigurationChance implements WorldGenCarverConfiguration, WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureConfigurationChance> b = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((worldgenfeatureconfigurationchance) -> {
            return worldgenfeatureconfigurationchance.c;
        })).apply(instance, WorldGenFeatureConfigurationChance::new);
    });
    public final float c;

    public WorldGenFeatureConfigurationChance(float f) {
        this.c = f;
    }
}
