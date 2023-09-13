package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherChance extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenDecoratorNetherChance(Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorRangeConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, BlockPosition blockposition) {
        if (random.nextFloat() < worldgenfeaturechancedecoratorrangeconfiguration.a) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = random.nextInt(worldgenfeaturechancedecoratorrangeconfiguration.d - worldgenfeaturechancedecoratorrangeconfiguration.c) + worldgenfeaturechancedecoratorrangeconfiguration.b;

            return Stream.of(new BlockPosition(i, k, j));
        } else {
            return Stream.empty();
        }
    }
}
