package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFeatureTreeAlterGround extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeAlterGround> a = WorldGenFeatureStateProvider.a.fieldOf("provider").xmap(WorldGenFeatureTreeAlterGround::new, (worldgenfeaturetreealterground) -> {
        return worldgenfeaturetreealterground.b;
    }).codec();
    private final WorldGenFeatureStateProvider b;

    public WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        this.b = worldgenfeaturestateprovider;
    }

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.e;
    }

    @Override
    public void a(GeneratorAccessSeed generatoraccessseed, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        int i = ((BlockPosition) list.get(0)).getY();

        list.stream().filter((blockposition) -> {
            return blockposition.getY() == i;
        }).forEach((blockposition) -> {
            this.a((VirtualLevelWritable) generatoraccessseed, random, blockposition.west().north());
            this.a((VirtualLevelWritable) generatoraccessseed, random, blockposition.east(2).north());
            this.a((VirtualLevelWritable) generatoraccessseed, random, blockposition.west().south(2));
            this.a((VirtualLevelWritable) generatoraccessseed, random, blockposition.east(2).south(2));

            for (int j = 0; j < 5; ++j) {
                int k = random.nextInt(64);
                int l = k % 8;
                int i1 = k / 8;

                if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                    this.a((VirtualLevelWritable) generatoraccessseed, random, blockposition.b(-3 + l, 0, -3 + i1));
                }
            }

        });
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.b(virtuallevelwritable, random, blockposition.b(i, 0, j));
                }
            }
        }

    }

    private void b(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition) {
        for (int i = 2; i >= -3; --i) {
            BlockPosition blockposition1 = blockposition.up(i);

            if (WorldGenerator.a((VirtualLevelReadable) virtuallevelwritable, blockposition1)) {
                virtuallevelwritable.setTypeAndData(blockposition1, this.b.a(random, blockposition), 19);
                break;
            }

            if (!WorldGenerator.b(virtuallevelwritable, blockposition1) && i < 0) {
                break;
            }
        }

    }
}
