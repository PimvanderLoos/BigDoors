package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorChancePass extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorChancePass(Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        return random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.a ? Stream.of(blockposition) : Stream.empty();
    }
}
