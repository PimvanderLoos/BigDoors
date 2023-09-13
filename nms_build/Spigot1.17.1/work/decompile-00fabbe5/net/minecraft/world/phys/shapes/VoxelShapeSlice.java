package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.EnumDirection;

public class VoxelShapeSlice extends VoxelShape {

    private final VoxelShape delegate;
    private final EnumDirection.EnumAxis axis;
    private static final DoubleList SLICE_COORDS = new VoxelShapeCubePoint(1);

    public VoxelShapeSlice(VoxelShape voxelshape, EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        super(a(voxelshape.shape, enumdirection_enumaxis, i));
        this.delegate = voxelshape;
        this.axis = enumdirection_enumaxis;
    }

    private static VoxelShapeDiscrete a(VoxelShapeDiscrete voxelshapediscrete, EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        return new VoxelShapeDiscreteSlice(voxelshapediscrete, enumdirection_enumaxis.a(i, 0, 0), enumdirection_enumaxis.a(0, i, 0), enumdirection_enumaxis.a(0, 0, i), enumdirection_enumaxis.a(i + 1, voxelshapediscrete.xSize, voxelshapediscrete.xSize), enumdirection_enumaxis.a(voxelshapediscrete.ySize, i + 1, voxelshapediscrete.ySize), enumdirection_enumaxis.a(voxelshapediscrete.zSize, voxelshapediscrete.zSize, i + 1));
    }

    @Override
    protected DoubleList a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis == this.axis ? VoxelShapeSlice.SLICE_COORDS : this.delegate.a(enumdirection_enumaxis);
    }
}
