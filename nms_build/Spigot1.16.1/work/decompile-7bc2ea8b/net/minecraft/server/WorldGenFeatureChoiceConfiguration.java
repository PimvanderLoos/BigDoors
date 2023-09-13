package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureChoiceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature_true").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.b;
        }), WorldGenFeatureConfigured.b.fieldOf("feature_false").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.c;
        })).apply(instance, WorldGenFeatureChoiceConfiguration::new);
    });
    public final WorldGenFeatureConfigured<?, ?> b;
    public final WorldGenFeatureConfigured<?, ?> c;

    public WorldGenFeatureChoiceConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured1) {
        this.b = worldgenfeatureconfigured;
        this.c = worldgenfeatureconfigured1;
    }
}
