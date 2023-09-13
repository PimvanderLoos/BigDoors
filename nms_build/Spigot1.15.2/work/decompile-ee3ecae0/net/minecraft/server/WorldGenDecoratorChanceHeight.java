package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorChanceHeight extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorChanceHeight(Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        if (random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.a) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, i, j) * 2;

            return k <= 0 ? Stream.empty() : Stream.of(new BlockPosition(i, random.nextInt(k), j));
        } else {
            return Stream.empty();
        }
    }
}
