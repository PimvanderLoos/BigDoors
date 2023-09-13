package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorNoiseHeight32 extends WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> {

    public WorldGenDecoratorNoiseHeight32(Function<Dynamic<?>, ? extends WorldGenFeatureDecoratorNoiseConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenFeatureDecoratorNoiseConfiguration worldgenfeaturedecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.e.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);
        int i = d0 < worldgenfeaturedecoratornoiseconfiguration.a ? worldgenfeaturedecoratornoiseconfiguration.b : worldgenfeaturedecoratornoiseconfiguration.c;

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, k, l) + 32;

            return i1 <= 0 ? null : new BlockPosition(k, random.nextInt(i1), l);
        }).filter(Objects::nonNull);
    }
}
