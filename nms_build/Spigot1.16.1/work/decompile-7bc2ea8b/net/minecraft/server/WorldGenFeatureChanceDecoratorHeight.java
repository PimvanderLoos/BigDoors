package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenFeatureChanceDecoratorHeight extends WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> {

    public WorldGenFeatureChanceDecoratorHeight(Codec<WorldGenDecoratorFrequencyChanceConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorFrequencyChanceConfiguration worldgendecoratorfrequencychanceconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, worldgendecoratorfrequencychanceconfiguration.b).filter((i) -> {
            return random.nextFloat() < worldgendecoratorfrequencychanceconfiguration.c;
        }).mapToObj((i) -> {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, j, k) * 2;

            return l <= 0 ? null : new BlockPosition(j, random.nextInt(l), k);
        }).filter(Objects::nonNull);
    }
}
