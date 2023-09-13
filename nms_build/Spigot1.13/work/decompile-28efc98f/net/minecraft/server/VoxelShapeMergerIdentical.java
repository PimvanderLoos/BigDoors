package net.minecraft.server;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class VoxelShapeMergerIdentical extends AbstractDoubleList implements VoxelShapeMerger {

    private final DoubleList a;

    public VoxelShapeMergerIdentical(DoubleList doublelist) {
        this.a = doublelist;
    }

    public int size() {
        return this.a.size();
    }

    public boolean a(VoxelShapeMerger.a voxelshapemerger_a) {
        for (int i = 0; i <= this.size(); ++i) {
            if (!voxelshapemerger_a.merge(i, i, i)) {
                return false;
            }
        }

        return true;
    }

    public double getDouble(int i) {
        return this.a.getDouble(i);
    }
}
