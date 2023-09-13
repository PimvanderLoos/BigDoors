package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureCompositeFlower<D extends WorldGenFeatureDecoratorConfiguration> extends WorldGenFeatureComposite<WorldGenFeatureEmptyConfiguration, D> {

    public WorldGenFeatureCompositeFlower(WorldGenFlowers worldgenflowers, WorldGenDecorator<D> worldgendecorator, D d0) {
        super(worldgenflowers, WorldGenFeatureConfiguration.e, worldgendecorator, d0);
    }

    public IBlockData a(Random random, BlockPosition blockposition) {
        return ((WorldGenFlowers) this.a).a(random, blockposition);
    }
}
