package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;

public final class VoxelShapeCube extends VoxelShape {

    protected VoxelShapeCube(VoxelShapeDiscrete voxelshapediscrete) {
        super(voxelshapediscrete);
    }

    @Override
    protected DoubleList a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return new VoxelShapeCubePoint(this.shape.c(enumdirection_enumaxis));
    }

    @Override
    protected int a(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        int i = this.shape.c(enumdirection_enumaxis);

        return MathHelper.floor(MathHelper.a(d0 * (double) i, -1.0D, (double) i));
    }
}
