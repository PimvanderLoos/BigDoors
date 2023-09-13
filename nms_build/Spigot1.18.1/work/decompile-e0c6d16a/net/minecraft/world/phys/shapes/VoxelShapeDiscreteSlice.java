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
    public boolean isFull(int i, int j, int k) {
        return this.parent.isFull(this.startX + i, this.startY + j, this.startZ + k);
    }

    @Override
    public void fill(int i, int j, int k) {
        this.parent.fill(this.startX + i, this.startY + j, this.startZ + k);
    }

    @Override
    public int firstFull(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.clampToShape(enumdirection_enumaxis, this.parent.firstFull(enumdirection_enumaxis));
    }

    @Override
    public int lastFull(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.clampToShape(enumdirection_enumaxis, this.parent.lastFull(enumdirection_enumaxis));
    }

    private int clampToShape(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        int j = enumdirection_enumaxis.choose(this.startX, this.startY, this.startZ);
        int k = enumdirection_enumaxis.choose(this.endX, this.endY, this.endZ);

        return MathHelper.clamp(i, j, k) - j;
    }
}
