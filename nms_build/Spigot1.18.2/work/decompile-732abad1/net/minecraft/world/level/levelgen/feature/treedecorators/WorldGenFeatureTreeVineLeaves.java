package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenFeatureTreeVineLeaves extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineLeaves> CODEC = Codec.unit(() -> {
        return WorldGenFeatureTreeVineLeaves.INSTANCE;
    });
    public static final WorldGenFeatureTreeVineLeaves INSTANCE = new WorldGenFeatureTreeVineLeaves();

    public WorldGenFeatureTreeVineLeaves() {}

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.LEAVE_VINE;
    }

    @Override
    public void place(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        list1.forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.west();
                if (WorldGenerator.isAir(virtuallevelreadable, blockposition1)) {
                    addHangingVine(virtuallevelreadable, blockposition1, BlockVine.EAST, biconsumer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.east();
                if (WorldGenerator.isAir(virtuallevelreadable, blockposition1)) {
                    addHangingVine(virtuallevelreadable, blockposition1, BlockVine.WEST, biconsumer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.north();
                if (WorldGenerator.isAir(virtuallevelreadable, blockposition1)) {
                    addHangingVine(virtuallevelreadable, blockposition1, BlockVine.SOUTH, biconsumer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.south();
                if (WorldGenerator.isAir(virtuallevelreadable, blockposition1)) {
                    addHangingVine(virtuallevelreadable, blockposition1, BlockVine.NORTH, biconsumer);
                }
            }

        });
    }

    private static void addHangingVine(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, BlockStateBoolean blockstateboolean, BiConsumer<BlockPosition, IBlockData> biconsumer) {
        placeVine(biconsumer, blockposition, blockstateboolean);
        int i = 4;

        for (blockposition = blockposition.below(); WorldGenerator.isAir(virtuallevelreadable, blockposition) && i > 0; --i) {
            placeVine(biconsumer, blockposition, blockstateboolean);
            blockposition = blockposition.below();
        }

    }
}
