package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorChance extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorChance(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        return random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.c ? Stream.of(blockposition) : Stream.empty();
    }
}
