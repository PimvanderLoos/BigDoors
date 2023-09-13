package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorSquare extends WorldGenDecoratorFeatureSimple<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSquare(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        int i = random.nextInt(16) + blockposition.getX();
        int j = random.nextInt(16) + blockposition.getZ();
        int k = blockposition.getY();

        return Stream.of(new BlockPosition(i, k, j));
    }
}
