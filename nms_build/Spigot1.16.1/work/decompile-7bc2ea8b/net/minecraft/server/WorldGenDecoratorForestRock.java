package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorForestRock extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorForestRock(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        int i = random.nextInt(worldgendecoratorfrequencyconfiguration.b);

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
