package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureTreeAlterGround extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeAlterGround> CODEC = WorldGenFeatureStateProvider.CODEC.fieldOf("provider").xmap(WorldGenFeatureTreeAlterGround::new, (worldgenfeaturetreealterground) -> {
        return worldgenfeaturetreealterground.provider;
    }).codec();
    private final WorldGenFeatureStateProvider provider;

    public WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        this.provider = worldgenfeaturestateprovider;
    }

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.ALTER_GROUND;
    }

    @Override
    public void place(WorldGenFeatureTree.a worldgenfeaturetree_a) {
        List<BlockPosition> list = Lists.newArrayList();
        List<BlockPosition> list1 = worldgenfeaturetree_a.roots();
        List<BlockPosition> list2 = worldgenfeaturetree_a.logs();

        if (list1.isEmpty()) {
            list.addAll(list2);
        } else if (!list2.isEmpty() && ((BlockPosition) list1.get(0)).getY() == ((BlockPosition) list2.get(0)).getY()) {
            list.addAll(list2);
            list.addAll(list1);
        } else {
            list.addAll(list1);
        }

        if (!list.isEmpty()) {
            int i = ((BlockPosition) list.get(0)).getY();

            list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).forEach((blockposition) -> {
                this.placeCircle(worldgenfeaturetree_a, blockposition.west().north());
                this.placeCircle(worldgenfeaturetree_a, blockposition.east(2).north());
                this.placeCircle(worldgenfeaturetree_a, blockposition.west().south(2));
                this.placeCircle(worldgenfeaturetree_a, blockposition.east(2).south(2));

                for (int j = 0; j < 5; ++j) {
                    int k = worldgenfeaturetree_a.random().nextInt(64);
                    int l = k % 8;
                    int i1 = k / 8;

                    if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                        this.placeCircle(worldgenfeaturetree_a, blockposition.offset(-3 + l, 0, -3 + i1));
                    }
                }

            });
        }
    }

    private void placeCircle(WorldGenFeatureTree.a worldgenfeaturetree_a, BlockPosition blockposition) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.placeBlockAt(worldgenfeaturetree_a, blockposition.offset(i, 0, j));
                }
            }
        }

    }

    private void placeBlockAt(WorldGenFeatureTree.a worldgenfeaturetree_a, BlockPosition blockposition) {
        for (int i = 2; i >= -3; --i) {
            BlockPosition blockposition1 = blockposition.above(i);

            if (WorldGenerator.isGrassOrDirt(worldgenfeaturetree_a.level(), blockposition1)) {
                worldgenfeaturetree_a.setBlock(blockposition1, this.provider.getState(worldgenfeaturetree_a.random(), blockposition));
                break;
            }

            if (!worldgenfeaturetree_a.isAir(blockposition1) && i < 0) {
                break;
            }
        }

    }
}
