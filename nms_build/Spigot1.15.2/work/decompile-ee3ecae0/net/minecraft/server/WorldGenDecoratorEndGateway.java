package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorEndGateway extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorEndGateway(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration2> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        if (random.nextInt(700) == 0) {
            int i = random.nextInt(16) + blockposition.getX();
            int j = random.nextInt(16) + blockposition.getZ();
            int k = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, i, j);

            if (k > 0) {
                int l = k + 3 + random.nextInt(7);

                return Stream.of(new BlockPosition(i, l, j));
            }
        }

        return Stream.empty();
    }
}
