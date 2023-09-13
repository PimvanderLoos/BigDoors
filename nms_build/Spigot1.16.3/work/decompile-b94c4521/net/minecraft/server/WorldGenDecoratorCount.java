package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorCount extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorCount(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, worldgendecoratorfrequencyconfiguration.a().a(random)).mapToObj((i) -> {
            return blockposition;
        });
    }
}
