package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureComposite<F extends WorldGenFeatureConfiguration, D extends WorldGenFeatureDecoratorConfiguration> extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    protected final WorldGenerator<F> a;
    protected final F b;
    protected final WorldGenDecorator<D> c;
    protected final D d;

    public WorldGenFeatureComposite(WorldGenerator<F> worldgenerator, F f0, WorldGenDecorator<D> worldgendecorator, D d0) {
        this.b = f0;
        this.d = d0;
        this.c = worldgendecorator;
        this.a = worldgenerator;
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return this.c.a(generatoraccess, chunkgenerator, random, blockposition, this.d, this.a, this.b);
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), this.c, this.a);
    }

    public WorldGenerator<F> a() {
        return this.a;
    }
}
