package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenDecorator<T extends WorldGenFeatureDecoratorConfiguration> {

    public WorldGenDecorator() {}

    public abstract <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, T t0, WorldGenerator<C> worldgenerator, C c0);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}
