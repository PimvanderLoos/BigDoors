package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
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
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.ALTER_GROUND;
    }

    @Override
    public void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        if (!list.isEmpty()) {
            int i = ((BlockPosition) list.get(0)).getY();

            list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).forEach((blockposition) -> {
                this.a(virtuallevelreadable, biconsumer, random, blockposition.west().north());
                this.a(virtuallevelreadable, biconsumer, random, blockposition.east(2).north());
                this.a(virtuallevelreadable, biconsumer, random, blockposition.west().south(2));
                this.a(virtuallevelreadable, biconsumer, random, blockposition.east(2).south(2));

                for (int j = 0; j < 5; ++j) {
                    int k = random.nextInt(64);
                    int l = k % 8;
                    int i1 = k / 8;

                    if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                        this.a(virtuallevelreadable, biconsumer, random, blockposition.c(-3 + l, 0, -3 + i1));
                    }
                }

            });
        }
    }

    private void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, BlockPosition blockposition) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.b(virtuallevelreadable, biconsumer, random, blockposition.c(i, 0, j));
                }
            }
        }

    }

    private void b(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, BlockPosition blockposition) {
        for (int i = 2; i >= -3; --i) {
            BlockPosition blockposition1 = blockposition.up(i);

            if (WorldGenerator.a(virtuallevelreadable, blockposition1)) {
                biconsumer.accept(blockposition1, this.provider.a(random, blockposition));
                break;
            }

            if (!WorldGenerator.b(virtuallevelreadable, blockposition1) && i < 0) {
                break;
            }
        }

    }
}
