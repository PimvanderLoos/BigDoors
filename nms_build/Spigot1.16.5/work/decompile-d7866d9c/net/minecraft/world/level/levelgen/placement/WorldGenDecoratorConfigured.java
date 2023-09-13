package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.IDecoratable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorConfigured<DC extends WorldGenFeatureDecoratorConfiguration> implements IDecoratable<WorldGenDecoratorConfigured<?>> {

    public static final Codec<WorldGenDecoratorConfigured<?>> a = IRegistry.DECORATOR.dispatch("type", (worldgendecoratorconfigured) -> {
        return worldgendecoratorconfigured.b;
    }, WorldGenDecorator::a);
    private final WorldGenDecorator<DC> b;
    private final DC c;

    public WorldGenDecoratorConfigured(WorldGenDecorator<DC> worldgendecorator, DC dc) {
        this.b = worldgendecorator;
        this.c = dc;
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, BlockPosition blockposition) {
        return this.b.a(worldgendecoratorcontext, random, this.c, blockposition);
    }

    public String toString() {
        return String.format("[%s %s]", IRegistry.DECORATOR.getKey(this.b), this.c);
    }

    @Override
    public WorldGenDecoratorConfigured<?> a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        return new WorldGenDecoratorConfigured<>(WorldGenDecorator.B, new WorldGenDecoratorDecpratedConfiguration(worldgendecoratorconfigured, this));
    }

    public DC b() {
        return this.c;
    }
}
