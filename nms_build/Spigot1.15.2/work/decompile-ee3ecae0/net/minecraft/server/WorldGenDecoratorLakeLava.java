package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorLakeLava extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorLakeLava(Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        if (random.nextInt(worldgendecoratordungeonconfiguration.a / 10) == 0) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = random.nextInt(random.nextInt(chunkgenerator.getGenerationDepth() - 8) + 8);

            if (k < generatoraccess.getSeaLevel() || random.nextInt(worldgendecoratordungeonconfiguration.a / 8) == 0) {
                return Stream.of(new BlockPosition(i, k, j));
            }
        }

        return Stream.empty();
    }
}
