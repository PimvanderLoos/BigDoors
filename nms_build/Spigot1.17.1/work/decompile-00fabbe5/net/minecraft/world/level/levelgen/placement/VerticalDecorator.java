package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public abstract class VerticalDecorator<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecorator<DC> {

    public VerticalDecorator(Codec<DC> codec) {
        super(codec);
    }

    protected abstract int a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, int i);

    @Override
    public final Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition) {
        return Stream.of(new BlockPosition(blockposition.getX(), this.a(worldgendecoratorcontext, random, dc, blockposition.getY()), blockposition.getZ()));
    }
}
