package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorLakeLava extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorLakeLava(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        if (random.nextInt(worldgendecoratordungeonconfiguration.c / 10) == 0) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = random.nextInt(random.nextInt(worldgendecoratorcontext.a() - 8) + 8);

            if (k < worldgendecoratorcontext.b() || random.nextInt(worldgendecoratordungeonconfiguration.c / 8) == 0) {
                return Stream.of(new BlockPosition(i, k, j));
            }
        }

        return Stream.empty();
    }
}
