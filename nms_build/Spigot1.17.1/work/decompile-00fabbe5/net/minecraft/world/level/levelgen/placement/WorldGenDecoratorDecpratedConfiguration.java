package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorDecpratedConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorDecpratedConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenDecoratorConfigured.CODEC.fieldOf("outer").forGetter(WorldGenDecoratorDecpratedConfiguration::a), WorldGenDecoratorConfigured.CODEC.fieldOf("inner").forGetter(WorldGenDecoratorDecpratedConfiguration::b)).apply(instance, WorldGenDecoratorDecpratedConfiguration::new);
    });
    private final WorldGenDecoratorConfigured<?> outer;
    private final WorldGenDecoratorConfigured<?> inner;

    public WorldGenDecoratorDecpratedConfiguration(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured1) {
        this.outer = worldgendecoratorconfigured;
        this.inner = worldgendecoratorconfigured1;
    }

    public WorldGenDecoratorConfigured<?> a() {
        return this.outer;
    }

    public WorldGenDecoratorConfigured<?> b() {
        return this.inner;
    }
}
