package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorSolidTopHeight extends WorldGenDecorator<WorldGenDecoratorRangeConfiguration> {

    public WorldGenDecoratorSolidTopHeight(Codec<WorldGenDecoratorRangeConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorRangeConfiguration worldgendecoratorrangeconfiguration, BlockPosition blockposition) {
        int i = random.nextInt(worldgendecoratorrangeconfiguration.c - worldgendecoratorrangeconfiguration.b) + worldgendecoratorrangeconfiguration.b;

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
