package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public abstract class WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTree> CODEC = BuiltInRegistries.TREE_DECORATOR_TYPE.byNameCodec().dispatch(WorldGenFeatureTree::type, WorldGenFeatureTrees::codec);

    public WorldGenFeatureTree() {}

    protected abstract WorldGenFeatureTrees<?> type();

    public abstract void place(WorldGenFeatureTree.a worldgenfeaturetree_a);

    public static final class a {

        private final VirtualLevelReadable level;
        private final BiConsumer<BlockPosition, IBlockData> decorationSetter;
        private final RandomSource random;
        private final ObjectArrayList<BlockPosition> logs;
        private final ObjectArrayList<BlockPosition> leaves;
        private final ObjectArrayList<BlockPosition> roots;

        public a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, Set<BlockPosition> set, Set<BlockPosition> set1, Set<BlockPosition> set2) {
            this.level = virtuallevelreadable;
            this.decorationSetter = biconsumer;
            this.random = randomsource;
            this.roots = new ObjectArrayList(set2);
            this.logs = new ObjectArrayList(set);
            this.leaves = new ObjectArrayList(set1);
            this.logs.sort(Comparator.comparingInt(BaseBlockPosition::getY));
            this.leaves.sort(Comparator.comparingInt(BaseBlockPosition::getY));
            this.roots.sort(Comparator.comparingInt(BaseBlockPosition::getY));
        }

        public void placeVine(BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
            this.setBlock(blockposition, (IBlockData) Blocks.VINE.defaultBlockState().setValue(blockstateboolean, true));
        }

        public void setBlock(BlockPosition blockposition, IBlockData iblockdata) {
            this.decorationSetter.accept(blockposition, iblockdata);
        }

        public boolean isAir(BlockPosition blockposition) {
            return this.level.isStateAtPosition(blockposition, BlockBase.BlockData::isAir);
        }

        public VirtualLevelReadable level() {
            return this.level;
        }

        public RandomSource random() {
            return this.random;
        }

        public ObjectArrayList<BlockPosition> logs() {
            return this.logs;
        }

        public ObjectArrayList<BlockPosition> leaves() {
            return this.leaves;
        }

        public ObjectArrayList<BlockPosition> roots() {
            return this.roots;
        }
    }
}
