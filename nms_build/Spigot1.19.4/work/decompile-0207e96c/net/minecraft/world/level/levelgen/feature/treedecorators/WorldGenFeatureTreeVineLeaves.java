package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public class WorldGenFeatureTreeVineLeaves extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineLeaves> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WorldGenFeatureTreeVineLeaves::new, (worldgenfeaturetreevineleaves) -> {
        return worldgenfeaturetreevineleaves.probability;
    }).codec();
    private final float probability;

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.LEAVE_VINE;
    }

    public WorldGenFeatureTreeVineLeaves(float f) {
        this.probability = f;
    }

    @Override
    public void place(WorldGenFeatureTree.a worldgenfeaturetree_a) {
        RandomSource randomsource = worldgenfeaturetree_a.random();

        worldgenfeaturetree_a.leaves().forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (randomsource.nextFloat() < this.probability) {
                blockposition1 = blockposition.west();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    addHangingVine(blockposition1, BlockVine.EAST, worldgenfeaturetree_a);
                }
            }

            if (randomsource.nextFloat() < this.probability) {
                blockposition1 = blockposition.east();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    addHangingVine(blockposition1, BlockVine.WEST, worldgenfeaturetree_a);
                }
            }

            if (randomsource.nextFloat() < this.probability) {
                blockposition1 = blockposition.north();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    addHangingVine(blockposition1, BlockVine.SOUTH, worldgenfeaturetree_a);
                }
            }

            if (randomsource.nextFloat() < this.probability) {
                blockposition1 = blockposition.south();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    addHangingVine(blockposition1, BlockVine.NORTH, worldgenfeaturetree_a);
                }
            }

        });
    }

    private static void addHangingVine(BlockPosition blockposition, BlockStateBoolean blockstateboolean, WorldGenFeatureTree.a worldgenfeaturetree_a) {
        worldgenfeaturetree_a.placeVine(blockposition, blockstateboolean);
        int i = 4;

        for (blockposition = blockposition.below(); worldgenfeaturetree_a.isAir(blockposition) && i > 0; --i) {
            worldgenfeaturetree_a.placeVine(blockposition, blockstateboolean);
            blockposition = blockposition.below();
        }

    }
}
