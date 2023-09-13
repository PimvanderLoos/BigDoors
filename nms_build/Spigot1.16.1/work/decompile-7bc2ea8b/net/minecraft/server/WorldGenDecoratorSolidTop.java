package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorSolidTop extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSolidTop(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        int i = random.nextInt(16) + blockposition.getX();
        int j = random.nextInt(16) + blockposition.getZ();
        int k = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, i, j);

        return Stream.of(new BlockPosition(i, k, j));
    }
}
