package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockUtil {

    public BlockUtil() {}

    public static BlockUtil.Rectangle getLargestRectangleAround(BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis, int i, EnumDirection.EnumAxis enumdirection_enumaxis1, int j, Predicate<BlockPosition> predicate) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        EnumDirection enumdirection = EnumDirection.get(EnumDirection.EnumAxisDirection.NEGATIVE, enumdirection_enumaxis);
        EnumDirection enumdirection1 = enumdirection.getOpposite();
        EnumDirection enumdirection2 = EnumDirection.get(EnumDirection.EnumAxisDirection.NEGATIVE, enumdirection_enumaxis1);
        EnumDirection enumdirection3 = enumdirection2.getOpposite();
        int k = getLimit(predicate, blockposition_mutableblockposition.set(blockposition), enumdirection, i);
        int l = getLimit(predicate, blockposition_mutableblockposition.set(blockposition), enumdirection1, i);
        int i1 = k;
        BlockUtil.IntBounds[] ablockutil_intbounds = new BlockUtil.IntBounds[k + 1 + l];

        ablockutil_intbounds[k] = new BlockUtil.IntBounds(getLimit(predicate, blockposition_mutableblockposition.set(blockposition), enumdirection2, j), getLimit(predicate, blockposition_mutableblockposition.set(blockposition), enumdirection3, j));
        int j1 = ablockutil_intbounds[k].min;

        BlockUtil.IntBounds blockutil_intbounds;
        int k1;

        for (k1 = 1; k1 <= k; ++k1) {
            blockutil_intbounds = ablockutil_intbounds[i1 - (k1 - 1)];
            ablockutil_intbounds[i1 - k1] = new BlockUtil.IntBounds(getLimit(predicate, blockposition_mutableblockposition.set(blockposition).move(enumdirection, k1), enumdirection2, blockutil_intbounds.min), getLimit(predicate, blockposition_mutableblockposition.set(blockposition).move(enumdirection, k1), enumdirection3, blockutil_intbounds.max));
        }

        for (k1 = 1; k1 <= l; ++k1) {
            blockutil_intbounds = ablockutil_intbounds[i1 + k1 - 1];
            ablockutil_intbounds[i1 + k1] = new BlockUtil.IntBounds(getLimit(predicate, blockposition_mutableblockposition.set(blockposition).move(enumdirection1, k1), enumdirection2, blockutil_intbounds.min), getLimit(predicate, blockposition_mutableblockposition.set(blockposition).move(enumdirection1, k1), enumdirection3, blockutil_intbounds.max));
        }

        k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int[] aint = new int[ablockutil_intbounds.length];

        for (int k2 = j1; k2 >= 0; --k2) {
            BlockUtil.IntBounds blockutil_intbounds1;
            int l2;
            int i3;

            for (int j3 = 0; j3 < ablockutil_intbounds.length; ++j3) {
                blockutil_intbounds1 = ablockutil_intbounds[j3];
                l2 = j1 - blockutil_intbounds1.min;
                i3 = j1 + blockutil_intbounds1.max;
                aint[j3] = k2 >= l2 && k2 <= i3 ? i3 + 1 - k2 : 0;
            }

            Pair<BlockUtil.IntBounds, Integer> pair = getMaxRectangleLocation(aint);

            blockutil_intbounds1 = (BlockUtil.IntBounds) pair.getFirst();
            l2 = 1 + blockutil_intbounds1.max - blockutil_intbounds1.min;
            i3 = (Integer) pair.getSecond();
            if (l2 * i3 > i2 * j2) {
                k1 = blockutil_intbounds1.min;
                l1 = k2;
                i2 = l2;
                j2 = i3;
            }
        }

        return new BlockUtil.Rectangle(blockposition.relative(enumdirection_enumaxis, k1 - i1).relative(enumdirection_enumaxis1, l1 - j1), i2, j2);
    }

    private static int getLimit(Predicate<BlockPosition> predicate, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection, int i) {
        int j;

        for (j = 0; j < i && predicate.test(blockposition_mutableblockposition.move(enumdirection)); ++j) {
            ;
        }

        return j;
    }

    @VisibleForTesting
    static Pair<BlockUtil.IntBounds, Integer> getMaxRectangleLocation(int[] aint) {
        int i = 0;
        int j = 0;
        int k = 0;
        IntArrayList intarraylist = new IntArrayList();

        intarraylist.push(0);
        int l = 1;

        while (l <= aint.length) {
            int i1 = l == aint.length ? 0 : aint[l];

            while (true) {
                if (!intarraylist.isEmpty()) {
                    int j1 = aint[intarraylist.topInt()];

                    if (i1 < j1) {
                        intarraylist.popInt();
                        int k1 = intarraylist.isEmpty() ? 0 : intarraylist.topInt() + 1;

                        if (j1 * (l - k1) > k * (j - i)) {
                            j = l;
                            i = k1;
                            k = j1;
                        }
                        continue;
                    }

                    intarraylist.push(l);
                }

                if (intarraylist.isEmpty()) {
                    intarraylist.push(l);
                }

                ++l;
                break;
            }
        }

        return new Pair(new BlockUtil.IntBounds(i, j - 1), k);
    }

    public static Optional<BlockPosition> getTopConnectedBlock(IBlockAccess iblockaccess, BlockPosition blockposition, Block block, EnumDirection enumdirection, Block block1) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        IBlockData iblockdata;

        do {
            blockposition_mutableblockposition.move(enumdirection);
            iblockdata = iblockaccess.getBlockState(blockposition_mutableblockposition);
        } while (iblockdata.is(block));

        return iblockdata.is(block1) ? Optional.of(blockposition_mutableblockposition) : Optional.empty();
    }

    public static class IntBounds {

        public final int min;
        public final int max;

        public IntBounds(int i, int j) {
            this.min = i;
            this.max = j;
        }

        public String toString() {
            return "IntBounds{min=" + this.min + ", max=" + this.max + "}";
        }
    }

    public static class Rectangle {

        public final BlockPosition minCorner;
        public final int axis1Size;
        public final int axis2Size;

        public Rectangle(BlockPosition blockposition, int i, int j) {
            this.minCorner = blockposition;
            this.axis1Size = i;
            this.axis2Size = j;
        }
    }
}
