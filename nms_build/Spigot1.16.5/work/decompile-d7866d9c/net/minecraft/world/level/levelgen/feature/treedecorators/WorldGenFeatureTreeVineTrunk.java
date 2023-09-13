package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFeatureTreeVineTrunk extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineTrunk> a = Codec.unit(() -> {
        return WorldGenFeatureTreeVineTrunk.b;
    });
    public static final WorldGenFeatureTreeVineTrunk b = new WorldGenFeatureTreeVineTrunk();

    public WorldGenFeatureTreeVineTrunk() {}

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.a;
    }

    @Override
    public void a(GeneratorAccessSeed generatoraccessseed, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        list.forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.west();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition1, BlockVine.EAST, set, structureboundingbox);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.east();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition1, BlockVine.WEST, set, structureboundingbox);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.north();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition1, BlockVine.SOUTH, set, structureboundingbox);
                }
            }

            if (random.nextInt(3) > 0) {
                blockposition1 = blockposition.south();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition1, BlockVine.NORTH, set, structureboundingbox);
                }
            }

        });
    }
}
