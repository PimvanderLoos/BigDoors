package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenFeatureChanceDecorator extends WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> {

    public WorldGenFeatureChanceDecorator(Codec<WorldGenDecoratorFrequencyChanceConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorFrequencyChanceConfiguration worldgendecoratorfrequencychanceconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, worldgendecoratorfrequencychanceconfiguration.b).filter((i) -> {
            return random.nextFloat() < worldgendecoratorfrequencychanceconfiguration.c;
        }).mapToObj((i) -> {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, j, k);

            return new BlockPosition(j, l, k);
        });
    }
}
