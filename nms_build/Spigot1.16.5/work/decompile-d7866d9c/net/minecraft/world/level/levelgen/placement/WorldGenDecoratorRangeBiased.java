package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;

public class WorldGenDecoratorRangeBiased extends WorldGenDecoratorFeatureSimple<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenDecoratorRangeBiased(Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = random.nextInt(random.nextInt(worldgenfeaturechancedecoratorrangeconfiguration.e - worldgenfeaturechancedecoratorrangeconfiguration.d) + worldgenfeaturechancedecoratorrangeconfiguration.c);

        return Stream.of(new BlockPosition(i, k, j));
    }
}
