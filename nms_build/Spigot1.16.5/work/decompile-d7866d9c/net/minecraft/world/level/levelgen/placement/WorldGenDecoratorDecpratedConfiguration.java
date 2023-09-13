package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorDecpratedConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorDecpratedConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenDecoratorConfigured.a.fieldOf("outer").forGetter(WorldGenDecoratorDecpratedConfiguration::a), WorldGenDecoratorConfigured.a.fieldOf("inner").forGetter(WorldGenDecoratorDecpratedConfiguration::b)).apply(instance, WorldGenDecoratorDecpratedConfiguration::new);
    });
    private final WorldGenDecoratorConfigured<?> c;
    private final WorldGenDecoratorConfigured<?> d;

    public WorldGenDecoratorDecpratedConfiguration(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured1) {
        this.c = worldgendecoratorconfigured;
        this.d = worldgendecoratorconfigured1;
    }

    public WorldGenDecoratorConfigured<?> a() {
        return this.c;
    }

    public WorldGenDecoratorConfigured<?> b() {
        return this.d;
    }
}
