package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorNoiseHeightDouble extends WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> {

    public WorldGenDecoratorNoiseHeightDouble(Codec<WorldGenFeatureDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenFeatureDecoratorNoiseConfiguration worldgenfeaturedecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.f.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);
        int i = d0 < worldgenfeaturedecoratornoiseconfiguration.b ? worldgenfeaturedecoratornoiseconfiguration.c : worldgenfeaturedecoratornoiseconfiguration.d;

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, k, l) * 2;

            return i1 <= 0 ? null : new BlockPosition(k, random.nextInt(i1), l);
        }).filter(Objects::nonNull);
    }
}
