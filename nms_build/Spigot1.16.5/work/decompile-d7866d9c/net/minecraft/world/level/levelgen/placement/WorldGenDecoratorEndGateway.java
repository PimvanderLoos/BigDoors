package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorEndGateway extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorEndGateway(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        if (random.nextInt(700) == 0) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = worldgendecoratorcontext.a(HeightMap.Type.MOTION_BLOCKING, i, j);

            if (k > 0) {
                int l = k + 3 + random.nextInt(7);

                return Stream.of(new BlockPosition(i, l, j));
            }
        }

        return Stream.empty();
    }
}
