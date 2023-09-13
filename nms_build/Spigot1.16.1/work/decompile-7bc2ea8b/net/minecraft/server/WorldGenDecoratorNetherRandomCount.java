package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherRandomCount extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorNetherRandomCount(Codec<WorldGenFeatureChanceDecoratorCountConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, BlockPosition blockposition) {
        int i = random.nextInt(Math.max(worldgenfeaturechancedecoratorcountconfiguration.b, 1));

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.e - worldgenfeaturechancedecoratorcountconfiguration.d) + worldgenfeaturechancedecoratorcountconfiguration.c;

            return new BlockPosition(k, i1, l);
        });
    }
}
