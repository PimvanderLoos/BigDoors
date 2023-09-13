package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public abstract class WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTree> CODEC = IRegistry.TREE_DECORATOR_TYPES.byNameCodec().dispatch(WorldGenFeatureTree::type, WorldGenFeatureTrees::codec);

    public WorldGenFeatureTree() {}

    protected abstract WorldGenFeatureTrees<?> type();

    public abstract void place(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1);

    protected static void placeVine(BiConsumer<BlockPosition, IBlockData> biconsumer, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
        biconsumer.accept(blockposition, (IBlockData) Blocks.VINE.defaultBlockState().setValue(blockstateboolean, true));
    }
}
