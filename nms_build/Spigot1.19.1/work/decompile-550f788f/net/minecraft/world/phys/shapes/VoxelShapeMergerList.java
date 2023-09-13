package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;

public class VoxelShapeMergerList implements VoxelShapeMerger {

    private static final DoubleList EMPTY = DoubleLists.unmodifiable(DoubleArrayList.wrap(new double[]{0.0D}));
    private final double[] result;
    private final int[] firstIndices;
    private final int[] secondIndices;
    private final int resultLength;

    public VoxelShapeMergerList(DoubleList doublelist, DoubleList doublelist1, boolean flag, boolean flag1) {
        double d0 = Double.NaN;
        int i = doublelist.size();
        int j = doublelist1.size();
        int k = i + j;

        this.result = new double[k];
        this.firstIndices = new int[k];
        this.secondIndices = new int[k];
        boolean flag2 = !flag;
        boolean flag3 = !flag1;
        int l = 0;
        int i1 = 0;
        int j1 = 0;

        while (true) {
            boolean flag4 = i1 >= i;
            boolean flag5 = j1 >= j;

            if (flag4 && flag5) {
                this.resultLength = Math.max(1, l);
                return;
            }

            boolean flag6 = !flag4 && (flag5 || doublelist.getDouble(i1) < doublelist1.getDouble(j1) + 1.0E-7D);

            if (flag6) {
                ++i1;
                if (flag2 && (j1 == 0 || flag5)) {
                    continue;
                }
            } else {
                ++j1;
                if (flag3 && (i1 == 0 || flag4)) {
                    continue;
                }
            }

            int k1 = i1 - 1;
            int l1 = j1 - 1;
            double d1 = flag6 ? doublelist.getDouble(k1) : doublelist1.getDouble(l1);

            if (d0 < d1 - 1.0E-7D) {
                this.firstIndices[l] = k1;
                this.secondIndices[l] = l1;
                this.result[l] = d1;
                ++l;
                d0 = d1;
            } else {
                this.firstIndices[l - 1] = k1;
                this.secondIndices[l - 1] = l1;
            }
        }
    }

    @Override
    public boolean forMergedIndexes(VoxelShapeMerger.a voxelshapemerger_a) {
        int i = this.resultLength - 1;

        for (int j = 0; j < i; ++j) {
            if (!voxelshapemerger_a.merge(this.firstIndices[j], this.secondIndices[j], j)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int size() {
        return this.resultLength;
    }

    @Override
    public DoubleList getList() {
        return (DoubleList) (this.resultLength <= 1 ? VoxelShapeMergerList.EMPTY : DoubleArrayList.wrap(this.result, this.resultLength));
    }
}
