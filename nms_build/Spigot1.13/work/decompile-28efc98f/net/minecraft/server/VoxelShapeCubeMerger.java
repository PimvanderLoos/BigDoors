package net.minecraft.server;

import com.google.common.math.IntMath;

public final class VoxelShapeCubeMerger extends VoxelShapeCubePoint implements VoxelShapeMerger {

    private final int a;
    private final int b;
    private final int c;

    VoxelShapeCubeMerger(int i, int j) {
        super((int) VoxelShapes.a(i, j));
        this.a = i;
        this.b = j;
        this.c = IntMath.gcd(i, j);
    }

    public boolean a(VoxelShapeMerger.a voxelshapemerger_a) {
        int i = this.a / this.c;
        int j = this.b / this.c;

        for (int k = 0; k <= this.size(); ++k) {
            if (!voxelshapemerger_a.merge(k / j, k / i, k)) {
                return false;
            }
        }

        return true;
    }
}
