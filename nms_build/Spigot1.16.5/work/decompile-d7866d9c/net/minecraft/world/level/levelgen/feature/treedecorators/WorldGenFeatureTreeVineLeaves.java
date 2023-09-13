package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFeatureTreeVineLeaves extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeVineLeaves> a = Codec.unit(() -> {
        return WorldGenFeatureTreeVineLeaves.b;
    });
    public static final WorldGenFeatureTreeVineLeaves b = new WorldGenFeatureTreeVineLeaves();

    public WorldGenFeatureTreeVineLeaves() {}

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.b;
    }

    @Override
    public void a(GeneratorAccessSeed generatoraccessseed, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        list1.forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.west();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccessseed, blockposition1, BlockVine.EAST, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.east();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccessseed, blockposition1, BlockVine.WEST, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.north();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccessseed, blockposition1, BlockVine.SOUTH, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.south();
                if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccessseed, blockposition1, BlockVine.NORTH, set, structureboundingbox);
                }
            }

        });
    }

    private void a(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition, BlockStateBoolean blockstateboolean, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        this.a((IWorldWriter) virtuallevelwritable, blockposition, blockstateboolean, set, structureboundingbox);
        int i = 4;

        for (blockposition = blockposition.down(); WorldGenerator.b(virtuallevelwritable, blockposition) && i > 0; --i) {
            this.a((IWorldWriter) virtuallevelwritable, blockposition, blockstateboolean, set, structureboundingbox);
            blockposition = blockposition.down();
        }

    }
}
