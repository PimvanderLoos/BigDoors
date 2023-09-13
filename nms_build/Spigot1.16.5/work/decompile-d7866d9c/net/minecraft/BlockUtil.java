package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;

public class BlockUtil {

    public static BlockUtil.Rectangle a(BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis, int i, EnumDirection.EnumAxis enumdirection_enumaxis1, int j, Predicate<BlockPosition> predicate) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        EnumDirection enumdirection = EnumDirection.a(EnumDirection.EnumAxisDirection.NEGATIVE, enumdirection_enumaxis);
        EnumDirection enumdirection1 = enumdirection.opposite();
        EnumDirection enumdirection2 = EnumDirection.a(EnumDirection.EnumAxisDirection.NEGATIVE, enumdirection_enumaxis1);
        EnumDirection enumdirection3 = enumdirection2.opposite();
        int k = a(predicate, blockposition_mutableblockposition.g(blockposition), enumdirection, i);
        int l = a(predicate, blockposition_mutableblockposition.g(blockposition), enumdirection1, i);
        int i1 = k;
        BlockUtil.IntBounds[] ablockutil_intbounds = new BlockUtil.IntBounds[k + 1 + l];

        ablockutil_intbounds[k] = new BlockUtil.IntBounds(a(predicate, blockposition_mutableblockposition.g(blockposition), enumdirection2, j), a(predicate, blockposition_mutableblockposition.g(blockposition), enumdirection3, j));
        int j1 = ablockutil_intbounds[k].a;

        BlockUtil.IntBounds blockutil_intbounds;
        int k1;

        for (k1 = 1; k1 <= k; ++k1) {
            blockutil_intbounds = ablockutil_intbounds[i1 - (k1 - 1)];
            ablockutil_intbounds[i1 - k1] = new BlockUtil.IntBounds(a(predicate, blockposition_mutableblockposition.g(blockposition).c(enumdirection, k1), enumdirection2, blockutil_intbounds.a), a(predicate, blockposition_mutableblockposition.g(blockposition).c(enumdirection, k1), enumdirection3, blockutil_intbounds.b));
        }

        for (k1 = 1; k1 <= l; ++k1) {
            blockutil_intbounds = ablockutil_intbounds[i1 + k1 - 1];
            ablockutil_intbounds[i1 + k1] = new BlockUtil.IntBounds(a(predicate, blockposition_mutableblockposition.g(blockposition).c(enumdirection1, k1), enumdirection2, blockutil_intbounds.a), a(predicate, blockposition_mutableblockposition.g(blockposition).c(enumdirection1, k1), enumdirection3, blockutil_intbounds.b));
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
                l2 = j1 - blockutil_intbounds1.a;
                i3 = j1 + blockutil_intbounds1.b;
                aint[j3] = k2 >= l2 && k2 <= i3 ? i3 + 1 - k2 : 0;
            }

            Pair<BlockUtil.IntBounds, Integer> pair = a(aint);

            blockutil_intbounds1 = (BlockUtil.IntBounds) pair.getFirst();
            l2 = 1 + blockutil_intbounds1.b - blockutil_intbounds1.a;
            i3 = (Integer) pair.getSecond();
            if (l2 * i3 > i2 * j2) {
                k1 = blockutil_intbounds1.a;
                l1 = k2;
                i2 = l2;
                j2 = i3;
            }
        }

        return new BlockUtil.Rectangle(blockposition.a(enumdirection_enumaxis, k1 - i1).a(enumdirection_enumaxis1, l1 - j1), i2, j2);
    }

    private static int a(Predicate<BlockPosition> predicate, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection, int i) {
        int j;

        for (j = 0; j < i && predicate.test(blockposition_mutableblockposition.c(enumdirection)); ++j) {
            ;
        }

        return j;
    }

    @VisibleForTesting
    static Pair<BlockUtil.IntBounds, Integer> a(int[] aint) {
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

    public static class Rectangle {

        public final BlockPosition origin;
        public final int side1;
        public final int side2;

        public Rectangle(BlockPosition blockposition, int i, int j) {
            this.origin = blockposition;
            this.side1 = i;
            this.side2 = j;
        }
    }

    public static class IntBounds {

        public final int a;
        public final int b;

        public IntBounds(int i, int j) {
            this.a = i;
            this.b = j;
        }

        public String toString() {
            return "IntBounds{min=" + this.a + ", max=" + this.b + '}';
        }
    }
}
