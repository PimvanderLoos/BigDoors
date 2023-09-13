package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightExtraChance extends WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> {

    public WorldGenDecoratorHeightExtraChance(Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorFrequencyExtraChanceConfiguration worldgendecoratorfrequencyextrachanceconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorfrequencyextrachanceconfiguration.b;

        if (random.nextFloat() < worldgendecoratorfrequencyextrachanceconfiguration.c) {
            i += worldgendecoratorfrequencyextrachanceconfiguration.d;
        }

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
