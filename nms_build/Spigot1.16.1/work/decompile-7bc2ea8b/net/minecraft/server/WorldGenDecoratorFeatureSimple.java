package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public abstract class WorldGenDecoratorFeatureSimple<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecorator<DC> {

    public WorldGenDecoratorFeatureSimple(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public final Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, DC dc, BlockPosition blockposition) {
        return this.a(random, dc, blockposition);
    }

    protected abstract Stream<BlockPosition> a(Random random, DC dc, BlockPosition blockposition);
}
