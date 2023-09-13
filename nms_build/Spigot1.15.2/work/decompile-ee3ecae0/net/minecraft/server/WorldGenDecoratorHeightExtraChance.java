package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightExtraChance extends WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> {

    public WorldGenDecoratorHeightExtraChance(Function<Dynamic<?>, ? extends WorldGenDecoratorFrequencyExtraChanceConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenDecoratorFrequencyExtraChanceConfiguration worldgendecoratorfrequencyextrachanceconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorfrequencyextrachanceconfiguration.a;

        if (random.nextFloat() < worldgendecoratorfrequencyextrachanceconfiguration.b) {
            i += worldgendecoratorfrequencyextrachanceconfiguration.c;
        }

        return IntStream.range(0, i).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, k, l);

            return new BlockPosition(k, i1, l);
        });
    }
}
