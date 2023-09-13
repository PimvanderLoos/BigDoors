package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BlockVine;

public class WorldGenFeatureTreeVineTrunk extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineTrunk> CODEC = Codec.unit(() -> {
        return WorldGenFeatureTreeVineTrunk.INSTANCE;
    });
    public static final WorldGenFeatureTreeVineTrunk INSTANCE = new WorldGenFeatureTreeVineTrunk();

    public WorldGenFeatureTreeVineTrunk() {}

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.TRUNK_VINE;
    }

    @Override
    public void place(WorldGenFeatureTree.a worldgenfeaturetree_a) {
        RandomSource randomsource = worldgenfeaturetree_a.random();

        worldgenfeaturetree_a.logs().forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (randomsource.nextInt(3) > 0) {
                blockposition1 = blockposition.west();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    worldgenfeaturetree_a.placeVine(blockposition1, BlockVine.EAST);
                }
            }

            if (randomsource.nextInt(3) > 0) {
                blockposition1 = blockposition.east();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    worldgenfeaturetree_a.placeVine(blockposition1, BlockVine.WEST);
                }
            }

            if (randomsource.nextInt(3) > 0) {
                blockposition1 = blockposition.north();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    worldgenfeaturetree_a.placeVine(blockposition1, BlockVine.SOUTH);
                }
            }

            if (randomsource.nextInt(3) > 0) {
                blockposition1 = blockposition.south();
                if (worldgenfeaturetree_a.isAir(blockposition1)) {
                    worldgenfeaturetree_a.placeVine(blockposition1, BlockVine.NORTH);
                }
            }

        });
    }
}
