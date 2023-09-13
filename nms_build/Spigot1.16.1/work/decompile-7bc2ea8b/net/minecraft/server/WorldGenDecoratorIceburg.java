package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorIceburg extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorIceburg(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        if (random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.b) {
            int i = random.nextInt(8) + 4 + blockposition.getX();
            int j = random.nextInt(8) + 4 + blockposition.getZ();
            int k = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, i, j);

            return Stream.of(new BlockPosition(i, k, j));
        } else {
            return Stream.empty();
        }
    }
}
