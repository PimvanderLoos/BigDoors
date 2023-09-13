package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorCountNoiseBiased extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorNoiseConfiguration> {

    public WorldGenDecoratorCountNoiseBiased(Codec<WorldGenDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorNoiseConfiguration worldgendecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.f.a((double) blockposition.getX() / worldgendecoratornoiseconfiguration.d, (double) blockposition.getZ() / worldgendecoratornoiseconfiguration.d, false);
        int i = (int) Math.ceil((d0 + worldgendecoratornoiseconfiguration.e) * (double) worldgendecoratornoiseconfiguration.c);

        return IntStream.range(0, i).mapToObj((j) -> {
            return blockposition;
        });
    }
}
