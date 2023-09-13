package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class VoxelShapeMergerDisjoint extends AbstractDoubleList implements VoxelShapeMerger {

    private final DoubleList lower;
    private final DoubleList upper;
    private final boolean swap;

    protected VoxelShapeMergerDisjoint(DoubleList doublelist, DoubleList doublelist1, boolean flag) {
        this.lower = doublelist;
        this.upper = doublelist1;
        this.swap = flag;
    }

    @Override
    public int size() {
        return this.lower.size() + this.upper.size();
    }

    @Override
    public boolean forMergedIndexes(VoxelShapeMerger.a voxelshapemerger_a) {
        return this.swap ? this.forNonSwappedIndexes((i, j, k) -> {
            return voxelshapemerger_a.merge(j, i, k);
        }) : this.forNonSwappedIndexes(voxelshapemerger_a);
    }

    private boolean forNonSwappedIndexes(VoxelShapeMerger.a voxelshapemerger_a) {
        int i = this.lower.size();

        int j;

        for (j = 0; j < i; ++j) {
            if (!voxelshapemerger_a.merge(j, -1, j)) {
                return false;
            }
        }

        j = this.upper.size() - 1;

        for (int k = 0; k < j; ++k) {
            if (!voxelshapemerger_a.merge(i - 1, k, i + k)) {
                return false;
            }
        }

        return true;
    }

    public double getDouble(int i) {
        return i < this.lower.size() ? this.lower.getDouble(i) : this.upper.getDouble(i - this.lower.size());
    }

    @Override
    public DoubleList getList() {
        return this;
    }
}
