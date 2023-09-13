package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public abstract class WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTree> c = IRegistry.TREE_DECORATOR_TYPE.dispatch(WorldGenFeatureTree::a, WorldGenFeatureTrees::a);

    public WorldGenFeatureTree() {}

    protected abstract WorldGenFeatureTrees<?> a();

    public abstract void a(GeneratorAccessSeed generatoraccessseed, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox);

    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, BlockStateBoolean blockstateboolean, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        this.a(iworldwriter, blockposition, (IBlockData) Blocks.VINE.getBlockData().set(blockstateboolean, true), set, structureboundingbox);
    }

    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        iworldwriter.setTypeAndData(blockposition, iblockdata, 19);
        set.add(blockposition);
        structureboundingbox.c(new StructureBoundingBox(blockposition, blockposition));
    }
}
