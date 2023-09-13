package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorDepthAverage extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorHeightAverageConfiguration> {

    public WorldGenDecoratorDepthAverage(Codec<WorldGenDecoratorHeightAverageConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorHeightAverageConfiguration worldgendecoratorheightaverageconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorheightaverageconfiguration.c;
        int j = worldgendecoratorheightaverageconfiguration.d;
        int k = blockposition.getX();
        int l = blockposition.getZ();
        int i1 = random.nextInt(j) + random.nextInt(j) - j + i;

        return Stream.of(new BlockPosition(k, i1, l));
    }
}
