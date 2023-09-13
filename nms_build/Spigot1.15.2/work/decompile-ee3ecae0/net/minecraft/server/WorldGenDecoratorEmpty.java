package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorEmpty extends WorldGenDecoratorFeatureSimple<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorEmpty(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration2> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        return Stream.of(blockposition);
    }
}
