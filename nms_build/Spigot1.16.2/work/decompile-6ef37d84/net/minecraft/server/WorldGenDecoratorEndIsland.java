package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorEndIsland extends WorldGenDecoratorFeatureSimple<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorEndIsland(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        Stream<BlockPosition> stream = Stream.empty();

        if (random.nextInt(14) == 0) {
            stream = Stream.concat(stream, Stream.of(blockposition.b(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            if (random.nextInt(4) == 0) {
                stream = Stream.concat(stream, Stream.of(blockposition.b(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
            }

            return stream;
        } else {
            return Stream.empty();
        }
    }
}
