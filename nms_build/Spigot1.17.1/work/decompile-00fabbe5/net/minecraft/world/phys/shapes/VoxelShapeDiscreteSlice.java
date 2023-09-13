package net.minecraft.world.phys.shapes;

import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;

public final class VoxelShapeDiscreteSlice extends VoxelShapeDiscrete {

    private final VoxelShapeDiscrete parent;
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;

    protected VoxelShapeDiscreteSlice(VoxelShapeDiscrete voxelshapediscrete, int i, int j, int k, int l, int i1, int j1) {
        super(l - i, i1 - j, j1 - k);
        this.parent = voxelshapediscrete;
        this.startX = i;
        this.startY = j;
        this.startZ = k;
        this.endX = l;
        this.endY = i1;
        this.endZ = j1;
    }

    @Override
    public boolean b(int i, int j, int k) {
        return this.parent.b(this.startX + i, this.startY + j, this.startZ + k);
    }

    @Override
    public void c(int i, int j, int k) {
        this.parent.c(this.startX + i, this.startY + j, this.startZ + k);
    }

    @Override
    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.a(enumdirection_enumaxis, this.parent.a(enumdirection_enumaxis));
    }

    @Override
    public int b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.a(enumdirection_enumaxis, this.parent.b(enumdirection_enumaxis));
    }

    private int a(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        int j = enumdirection_enumaxis.a(this.startX, this.startY, this.startZ);
        int k = enumdirection_enumaxis.a(this.endX, this.endY, this.endZ);

        return MathHelper.clamp(i, j, k) - j;
    }
}
