package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomChoiceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.apply2(WorldGenFeatureRandomChoiceConfiguration::new, WorldGenFeatureRandomChoiceConfigurationWeight.a.listOf().fieldOf("features").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.b;
        }), WorldGenFeatureConfigured.b.fieldOf("default").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.c;
        }));
    });
    public final List<WorldGenFeatureRandomChoiceConfigurationWeight<?>> b;
    public final WorldGenFeatureConfigured<?, ?> c;

    public WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight<?>> list, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        this.b = list;
        this.c = worldgenfeatureconfigured;
    }
}
