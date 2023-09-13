package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherMagma extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorNetherMagma(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        int i = worldgendecoratorcontext.b();
        int j = i - 5 + random.nextInt(10);

        return Stream.of(new BlockPosition(blockposition.getX(), j, blockposition.getZ()));
    }
}
