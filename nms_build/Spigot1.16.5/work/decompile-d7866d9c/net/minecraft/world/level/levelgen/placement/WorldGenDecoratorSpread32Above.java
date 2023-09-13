package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorSpread32Above extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSpread32Above(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        int i = random.nextInt(blockposition.getY() + 32);

        return Stream.of(new BlockPosition(blockposition.getX(), i, blockposition.getZ()));
    }
}
