package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenFeatureTreeVineTrunk extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineTrunk> CODEC = Codec.unit(() -> {
        return WorldGenFeatureTreeVineTrunk.INSTANCE;
    });
    public static final WorldGenFeatureTreeVineTrunk INSTANCE = new WorldGenFeatureTreeVineTrunk();

    public WorldGenFeatureTreeVineTrunk() {}

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.TRUNK_VINE;
    }

    @Override
    public void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        list.forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.west();
                if (WorldGenerator.b(virtuallevelreadable, blockposition1)) {
                    a(biconsumer, blockposition1, BlockVine.EAST);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.east();
                if (WorldGenerator.b(virtuallevelreadable, blockposition1)) {
                    a(biconsumer, blockposition1, BlockVine.WEST);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.north();
                if (WorldGenerator.b(virtuallevelreadable, blockposition1)) {
                    a(biconsumer, blockposition1, BlockVine.SOUTH);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.south();
                if (WorldGenerator.b(virtuallevelreadable, blockposition1)) {
                    a(biconsumer, blockposition1, BlockVine.NORTH);
                }
            }

        });
    }
}
