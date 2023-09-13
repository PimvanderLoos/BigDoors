package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorRange extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenDecoratorRange(Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = random.nextInt(worldgenfeaturechancedecoratorrangeconfiguration.e - worldgenfeaturechancedecoratorrangeconfiguration.d) + worldgenfeaturechancedecoratorrangeconfiguration.c;

        return Stream.of(new BlockPosition(i, k, j));
    }
}
