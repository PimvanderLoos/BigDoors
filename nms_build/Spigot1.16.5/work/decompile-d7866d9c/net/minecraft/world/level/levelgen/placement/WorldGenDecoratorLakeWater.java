package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorLakeWater extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorLakeWater(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        if (random.nextInt(worldgendecoratordungeonconfiguration.c) == 0) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = random.nextInt(worldgendecoratorcontext.a());

            return Stream.of(new BlockPosition(i, k, j));
        } else {
            return Stream.empty();
        }
    }
}
