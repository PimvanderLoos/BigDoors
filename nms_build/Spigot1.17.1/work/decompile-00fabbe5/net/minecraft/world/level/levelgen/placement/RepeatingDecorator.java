package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public abstract class RepeatingDecorator<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecorator<DC> {

    public RepeatingDecorator(Codec<DC> codec) {
        super(codec);
    }

    protected abstract int a(Random random, DC dc, BlockPosition blockposition);

    @Override
    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition) {
        return IntStream.range(0, this.a(random, dc, blockposition)).mapToObj((i) -> {
            return blockposition;
        });
    }
}
