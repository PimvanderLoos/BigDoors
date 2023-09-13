package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface VoxelShapeMerger {

    DoubleList a();

    boolean a(VoxelShapeMerger.a voxelshapemerger_a);

    public interface a {

        boolean merge(int i, int j, int k);
    }
}
