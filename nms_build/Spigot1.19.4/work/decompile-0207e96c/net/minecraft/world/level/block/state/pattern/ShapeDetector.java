package net.minecraft.world.level.block.state.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IWorldReader;

public class ShapeDetector {

    private final Predicate<ShapeDetectorBlock>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public ShapeDetector(Predicate<ShapeDetectorBlock>[][][] apredicate) {
        this.pattern = apredicate;
        this.depth = apredicate.length;
        if (this.depth > 0) {
            this.height = apredicate[0].length;
            if (this.height > 0) {
                this.width = apredicate[0][0].length;
            } else {
                this.width = 0;
            }
        } else {
            this.height = 0;
            this.width = 0;
        }

    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @VisibleForTesting
    public Predicate<ShapeDetectorBlock>[][][] getPattern() {
        return this.pattern;
    }

    @Nullable
    @VisibleForTesting
    public ShapeDetector.ShapeDetectorCollection matches(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
        LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache = createLevelCache(iworldreader, false);

        return this.matches(blockposition, enumdirection, enumdirection1, loadingcache);
    }

    @Nullable
    private ShapeDetector.ShapeDetectorCollection matches(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (!this.pattern[k][j][i].test((ShapeDetectorBlock) loadingcache.getUnchecked(translateAndRotate(blockposition, enumdirection, enumdirection1, i, j, k)))) {
                        return null;
                    }
                }
            }
        }

        return new ShapeDetector.ShapeDetectorCollection(blockposition, enumdirection, enumdirection1, loadingcache, this.width, this.height, this.depth);
    }

    @Nullable
    public ShapeDetector.ShapeDetectorCollection find(IWorldReader iworldreader, BlockPosition blockposition) {
        LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache = createLevelCache(iworldreader, false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);
        Iterator iterator = BlockPosition.betweenClosed(blockposition, blockposition.offset(i - 1, i - 1, i - 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];
                EnumDirection[] aenumdirection1 = EnumDirection.values();
                int l = aenumdirection1.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    EnumDirection enumdirection1 = aenumdirection1[i1];

                    if (enumdirection1 != enumdirection && enumdirection1 != enumdirection.getOpposite()) {
                        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.matches(blockposition1, enumdirection, enumdirection1, loadingcache);

                        if (shapedetector_shapedetectorcollection != null) {
                            return shapedetector_shapedetectorcollection;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static LoadingCache<BlockPosition, ShapeDetectorBlock> createLevelCache(IWorldReader iworldreader, boolean flag) {
        return CacheBuilder.newBuilder().build(new ShapeDetector.BlockLoader(iworldreader, flag));
    }

    protected static BlockPosition translateAndRotate(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, int i, int j, int k) {
        if (enumdirection != enumdirection1 && enumdirection != enumdirection1.getOpposite()) {
            BaseBlockPosition baseblockposition = new BaseBlockPosition(enumdirection.getStepX(), enumdirection.getStepY(), enumdirection.getStepZ());
            BaseBlockPosition baseblockposition1 = new BaseBlockPosition(enumdirection1.getStepX(), enumdirection1.getStepY(), enumdirection1.getStepZ());
            BaseBlockPosition baseblockposition2 = baseblockposition.cross(baseblockposition1);

            return blockposition.offset(baseblockposition1.getX() * -j + baseblockposition2.getX() * i + baseblockposition.getX() * k, baseblockposition1.getY() * -j + baseblockposition2.getY() * i + baseblockposition.getY() * k, baseblockposition1.getZ() * -j + baseblockposition2.getZ() * i + baseblockposition.getZ() * k);
        } else {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
    }

    public static class ShapeDetectorCollection {

        private final BlockPosition frontTopLeft;
        private final EnumDirection forwards;
        private final EnumDirection up;
        private final LoadingCache<BlockPosition, ShapeDetectorBlock> cache;
        private final int width;
        private final int height;
        private final int depth;

        public ShapeDetectorCollection(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache, int i, int j, int k) {
            this.frontTopLeft = blockposition;
            this.forwards = enumdirection;
            this.up = enumdirection1;
            this.cache = loadingcache;
            this.width = i;
            this.height = j;
            this.depth = k;
        }

        public BlockPosition getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public EnumDirection getForwards() {
            return this.forwards;
        }

        public EnumDirection getUp() {
            return this.up;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getDepth() {
            return this.depth;
        }

        public ShapeDetectorBlock getBlock(int i, int j, int k) {
            return (ShapeDetectorBlock) this.cache.getUnchecked(ShapeDetector.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), i, j, k));
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
        }
    }

    private static class BlockLoader extends CacheLoader<BlockPosition, ShapeDetectorBlock> {

        private final IWorldReader level;
        private final boolean loadChunks;

        public BlockLoader(IWorldReader iworldreader, boolean flag) {
            this.level = iworldreader;
            this.loadChunks = flag;
        }

        public ShapeDetectorBlock load(BlockPosition blockposition) {
            return new ShapeDetectorBlock(this.level, blockposition, this.loadChunks);
        }
    }
}
