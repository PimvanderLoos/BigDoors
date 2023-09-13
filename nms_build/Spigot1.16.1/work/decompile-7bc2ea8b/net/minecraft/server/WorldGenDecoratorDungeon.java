package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorDungeon extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorDungeon(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratordungeonconfiguration.b;

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = random.nextInt(chunkgenerator.getGenerationDepth());

            return new BlockPosition(k, i1, l);
        });
    }
}
