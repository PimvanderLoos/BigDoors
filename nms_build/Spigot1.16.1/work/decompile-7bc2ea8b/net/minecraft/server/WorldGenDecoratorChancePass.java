package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorChancePass extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorChancePass(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        return random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.b ? Stream.of(blockposition) : Stream.empty();
    }
}
