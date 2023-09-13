package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorRoofedTree extends WorldGenDecoratorHeightAbstract<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorRoofedTree(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected HeightMap.Type a(WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2) {
        return HeightMap.Type.MOTION_BLOCKING;
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        return IntStream.range(0, 16).mapToObj((i) -> {
            int j = i / 4;
            int k = i % 4;
            int l = j * 4 + 1 + random.nextInt(3) + blockposition.getX();
            int i1 = k * 4 + 1 + random.nextInt(3) + blockposition.getZ();
            int j1 = worldgendecoratorcontext.a(this.a(worldgenfeatureemptyconfiguration2), l, i1);

            return new BlockPosition(l, j1, i1);
        });
    }
}
