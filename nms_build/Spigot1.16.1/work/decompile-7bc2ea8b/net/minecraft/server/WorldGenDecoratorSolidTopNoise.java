package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorSolidTopNoise extends WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> {

    public WorldGenDecoratorSolidTopNoise(Codec<WorldGenDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorNoiseConfiguration worldgendecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.f.a((double) blockposition.getX() / worldgendecoratornoiseconfiguration.c, (double) blockposition.getZ() / worldgendecoratornoiseconfiguration.c, false);
        int i = (int) Math.ceil((d0 + worldgendecoratornoiseconfiguration.d) * (double) worldgendecoratornoiseconfiguration.b);

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(worldgendecoratornoiseconfiguration.e, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
