package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class WorldGenFeatureRandomConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.listOf().fieldOf("features").forGetter((worldgenfeaturerandomconfiguration) -> {
            return worldgenfeaturerandomconfiguration.b;
        }), Codec.INT.fieldOf("count").withDefault(0).forGetter((worldgenfeaturerandomconfiguration) -> {
            return worldgenfeaturerandomconfiguration.c;
        })).apply(instance, WorldGenFeatureRandomConfiguration::new);
    });
    public final List<WorldGenFeatureConfigured<?, ?>> b;
    public final int c;

    public WorldGenFeatureRandomConfiguration(List<WorldGenFeatureConfigured<?, ?>> list, int i) {
        this.b = list;
        this.c = i;
    }
}
