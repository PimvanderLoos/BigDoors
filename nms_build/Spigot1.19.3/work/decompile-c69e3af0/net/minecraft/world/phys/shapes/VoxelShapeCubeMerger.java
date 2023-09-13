package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class VoxelShapeCubeMerger implements VoxelShapeMerger {

    private final VoxelShapeCubePoint result;
    private final int firstDiv;
    private final int secondDiv;

    VoxelShapeCubeMerger(int i, int j) {
        this.result = new VoxelShapeCubePoint((int) VoxelShapes.lcm(i, j));
        int k = IntMath.gcd(i, j);

        this.firstDiv = i / k;
        this.secondDiv = j / k;
    }

    @Override
    public boolean forMergedIndexes(VoxelShapeMerger.a voxelshapemerger_a) {
        int i = this.result.size() - 1;

        for (int j = 0; j < i; ++j) {
            if (!voxelshapemerger_a.merge(j / this.secondDiv, j / this.firstDiv, j)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int size() {
        return this.result.size();
    }

    @Override
    public DoubleList getList() {
        return this.result;
    }
}
