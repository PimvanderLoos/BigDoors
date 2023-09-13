package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface VoxelShapeMerger {

    DoubleList getList();

    boolean forMergedIndexes(VoxelShapeMerger.a voxelshapemerger_a);

    int size();

    public interface a {

        boolean merge(int i, int j, int k);
    }
}
