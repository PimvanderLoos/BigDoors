package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;

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
