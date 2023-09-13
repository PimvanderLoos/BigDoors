package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.EnumDirection;

public class VoxelShapeSlice extends VoxelShape {

    private final VoxelShape delegate;
    private final EnumDirection.EnumAxis axis;
    private static final DoubleList SLICE_COORDS = new VoxelShapeCubePoint(1);

    public VoxelShapeSlice(VoxelShape voxelshape, EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        super(makeSlice(voxelshape.shape, enumdirection_enumaxis, i));
        this.delegate = voxelshape;
        this.axis = enumdirection_enumaxis;
    }

    private static VoxelShapeDiscrete makeSlice(VoxelShapeDiscrete voxelshapediscrete, EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        return new VoxelShapeDiscreteSlice(voxelshapediscrete, enumdirection_enumaxis.choose(i, 0, 0), enumdirection_enumaxis.choose(0, i, 0), enumdirection_enumaxis.choose(0, 0, i), enumdirection_enumaxis.choose(i + 1, voxelshapediscrete.xSize, voxelshapediscrete.xSize), enumdirection_enumaxis.choose(voxelshapediscrete.ySize, i + 1, voxelshapediscrete.ySize), enumdirection_enumaxis.choose(voxelshapediscrete.zSize, voxelshapediscrete.zSize, i + 1));
    }

    @Override
    protected DoubleList getCoords(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis == this.axis ? VoxelShapeSlice.SLICE_COORDS : this.delegate.getCoords(enumdirection_enumaxis);
    }
}
