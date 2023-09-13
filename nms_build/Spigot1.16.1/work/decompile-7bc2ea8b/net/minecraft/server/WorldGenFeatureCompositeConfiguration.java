package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureCompositeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCompositeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.b;
        }), WorldGenDecoratorConfigured.a.fieldOf("decorator").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.c;
        })).apply(instance, WorldGenFeatureCompositeConfiguration::new);
    });
    public final WorldGenFeatureConfigured<?, ?> b;
    public final WorldGenDecoratorConfigured<?> c;

    public WorldGenFeatureCompositeConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        this.b = worldgenfeatureconfigured;
        this.c = worldgendecoratorconfigured;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(this.b.d), IRegistry.DECORATOR.getKey(this.c.b));
    }
}
