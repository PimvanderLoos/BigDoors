package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightBiased extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorHeightBiased(Codec<WorldGenFeatureChanceDecoratorCountConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, worldgenfeaturechancedecoratorcountconfiguration.b).mapToObj((i) -> {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = random.nextInt(random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.e - worldgenfeaturechancedecoratorcountconfiguration.d) + worldgenfeaturechancedecoratorcountconfiguration.c);

            return new BlockPosition(j, l, k);
        });
    }
}
