package net.minecraft.world.level.redstone;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import org.slf4j.Logger;

public class CollectingNeighborUpdater implements NeighborUpdater {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final World level;
    private final int maxChainedNeighborUpdates;
    private final ArrayDeque<CollectingNeighborUpdater.c> stack = new ArrayDeque();
    private final List<CollectingNeighborUpdater.c> addedThisLayer = new ArrayList();
    private int count = 0;

    public CollectingNeighborUpdater(World world, int i) {
        this.level = world;
        this.maxChainedNeighborUpdates = i;
    }

    @Override
    public void shapeUpdate(EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition, BlockPosition blockposition1, int i, int j) {
        this.addAndRun(blockposition, new CollectingNeighborUpdater.d(enumdirection, iblockdata, blockposition.immutable(), blockposition1.immutable(), i));
    }

    @Override
    public void neighborChanged(BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.addAndRun(blockposition, new CollectingNeighborUpdater.e(blockposition, block, blockposition1.immutable()));
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        this.addAndRun(blockposition, new CollectingNeighborUpdater.a(iblockdata, blockposition.immutable(), block, blockposition1.immutable(), flag));
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPosition blockposition, Block block, @Nullable EnumDirection enumdirection) {
        this.addAndRun(blockposition, new CollectingNeighborUpdater.b(blockposition.immutable(), block, enumdirection));
    }

    private void addAndRun(BlockPosition blockposition, CollectingNeighborUpdater.c collectingneighborupdater_c) {
        boolean flag = this.count > 0;
        boolean flag1 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;

        ++this.count;
        if (!flag1) {
            if (flag) {
                this.addedThisLayer.add(collectingneighborupdater_c);
            } else {
                this.stack.push(collectingneighborupdater_c);
            }
        } else if (this.count - 1 == this.maxChainedNeighborUpdates) {
            CollectingNeighborUpdater.LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + blockposition.toShortString());
        }

        if (!flag) {
            this.runUpdates();
        }

    }

    private void runUpdates() {
        try {
            while (!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
                for (int i = this.addedThisLayer.size() - 1; i >= 0; --i) {
                    this.stack.push((CollectingNeighborUpdater.c) this.addedThisLayer.get(i));
                }

                this.addedThisLayer.clear();
                CollectingNeighborUpdater.c collectingneighborupdater_c = (CollectingNeighborUpdater.c) this.stack.peek();

                while (this.addedThisLayer.isEmpty()) {
                    if (!collectingneighborupdater_c.runNext(this.level)) {
                        this.stack.pop();
                        break;
                    }
                }
            }
        } finally {
            this.stack.clear();
            this.addedThisLayer.clear();
            this.count = 0;
        }

    }

    private static record d(EnumDirection direction, IBlockData state, BlockPosition pos, BlockPosition neighborPos, int updateFlags) implements CollectingNeighborUpdater.c {

        @Override
        public boolean runNext(World world) {
            NeighborUpdater.executeShapeUpdate(world, this.direction, this.state, this.pos, this.neighborPos, this.updateFlags, 512);
            return false;
        }
    }

    private interface c {

        boolean runNext(World world);
    }

    static record e(BlockPosition pos, Block block, BlockPosition neighborPos) implements CollectingNeighborUpdater.c {

        @Override
        public boolean runNext(World world) {
            IBlockData iblockdata = world.getBlockState(this.pos);

            NeighborUpdater.executeUpdate(world, iblockdata, this.pos, this.block, this.neighborPos, false);
            return false;
        }
    }

    static record a(IBlockData state, BlockPosition pos, Block block, BlockPosition neighborPos, boolean movedByPiston) implements CollectingNeighborUpdater.c {

        @Override
        public boolean runNext(World world) {
            NeighborUpdater.executeUpdate(world, this.state, this.pos, this.block, this.neighborPos, this.movedByPiston);
            return false;
        }
    }

    static final class b implements CollectingNeighborUpdater.c {

        private final BlockPosition sourcePos;
        private final Block sourceBlock;
        @Nullable
        private final EnumDirection skipDirection;
        private int idx = 0;

        b(BlockPosition blockposition, Block block, @Nullable EnumDirection enumdirection) {
            this.sourcePos = blockposition;
            this.sourceBlock = block;
            this.skipDirection = enumdirection;
            if (NeighborUpdater.UPDATE_ORDER[this.idx] == enumdirection) {
                ++this.idx;
            }

        }

        @Override
        public boolean runNext(World world) {
            BlockPosition blockposition = this.sourcePos.relative(NeighborUpdater.UPDATE_ORDER[this.idx++]);
            IBlockData iblockdata = world.getBlockState(blockposition);

            iblockdata.neighborChanged(world, blockposition, this.sourceBlock, this.sourcePos, false);
            if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
                ++this.idx;
            }

            return this.idx < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}
