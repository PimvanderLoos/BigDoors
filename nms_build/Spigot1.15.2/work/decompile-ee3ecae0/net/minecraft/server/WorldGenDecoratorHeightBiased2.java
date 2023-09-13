package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightBiased2 extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorHeightBiased2(Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorCountConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, worldgenfeaturechancedecoratorcountconfiguration.a).mapToObj((i) -> {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = random.nextInt(random.nextInt(random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.d - worldgenfeaturechancedecoratorcountconfiguration.c) + worldgenfeaturechancedecoratorcountconfiguration.b) + worldgenfeaturechancedecoratorcountconfiguration.b);

            return new BlockPosition(j, l, k);
        });
    }
}
