package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.IDecoratable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorConfigured<DC extends WorldGenFeatureDecoratorConfiguration> implements IDecoratable<WorldGenDecoratorConfigured<?>> {

    public static final Codec<WorldGenDecoratorConfigured<?>> CODEC = IRegistry.DECORATOR.dispatch("type", (worldgendecoratorconfigured) -> {
        return worldgendecoratorconfigured.decorator;
    }, WorldGenDecorator::a);
    private final WorldGenDecorator<DC> decorator;
    private final DC config;

    public WorldGenDecoratorConfigured(WorldGenDecorator<DC> worldgendecorator, DC dc) {
        this.decorator = worldgendecorator;
        this.config = dc;
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, BlockPosition blockposition) {
        return this.decorator.a(worldgendecoratorcontext, random, this.config, blockposition);
    }

    public String toString() {
        return String.format("[%s %s]", IRegistry.DECORATOR.getKey(this.decorator), this.config);
    }

    @Override
    public WorldGenDecoratorConfigured<?> a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        return new WorldGenDecoratorConfigured<>(WorldGenDecorator.DECORATED, new WorldGenDecoratorDecpratedConfiguration(worldgendecoratorconfigured, this));
    }

    public DC b() {
        return this.config;
    }
}
