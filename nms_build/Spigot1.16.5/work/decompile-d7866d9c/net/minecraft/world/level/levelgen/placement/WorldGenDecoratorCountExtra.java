package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorCountExtra extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorFrequencyExtraChanceConfiguration> {

    public WorldGenDecoratorCountExtra(Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorFrequencyExtraChanceConfiguration worldgendecoratorfrequencyextrachanceconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorfrequencyextrachanceconfiguration.c + (random.nextFloat() < worldgendecoratorfrequencyextrachanceconfiguration.d ? worldgendecoratorfrequencyextrachanceconfiguration.e : 0);

        return IntStream.range(0, i).mapToObj((j) -> {
            return blockposition;
        });
    }
}
