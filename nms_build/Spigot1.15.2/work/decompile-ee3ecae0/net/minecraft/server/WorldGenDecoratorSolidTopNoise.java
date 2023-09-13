package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorSolidTopNoise extends WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> {

    public WorldGenDecoratorSolidTopNoise(Function<Dynamic<?>, ? extends WorldGenDecoratorNoiseConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenDecoratorNoiseConfiguration worldgendecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.e.a((double) blockposition.getX() / worldgendecoratornoiseconfiguration.b, (double) blockposition.getZ() / worldgendecoratornoiseconfiguration.b, false);
        int i = (int) Math.ceil((d0 + worldgendecoratornoiseconfiguration.c) * (double) worldgendecoratornoiseconfiguration.a);

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(worldgendecoratornoiseconfiguration.d, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
