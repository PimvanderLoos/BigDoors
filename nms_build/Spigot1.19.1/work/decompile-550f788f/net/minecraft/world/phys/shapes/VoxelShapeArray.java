package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumDirection;

public class VoxelShapeArray extends VoxelShape {

    private final DoubleList xs;
    private final DoubleList ys;
    private final DoubleList zs;

    protected VoxelShapeArray(VoxelShapeDiscrete voxelshapediscrete, double[] adouble, double[] adouble1, double[] adouble2) {
        this(voxelshapediscrete, (DoubleList) DoubleArrayList.wrap(Arrays.copyOf(adouble, voxelshapediscrete.getXSize() + 1)), (DoubleList) DoubleArrayList.wrap(Arrays.copyOf(adouble1, voxelshapediscrete.getYSize() + 1)), (DoubleList) DoubleArrayList.wrap(Arrays.copyOf(adouble2, voxelshapediscrete.getZSize() + 1)));
    }

    VoxelShapeArray(VoxelShapeDiscrete voxelshapediscrete, DoubleList doublelist, DoubleList doublelist1, DoubleList doublelist2) {
        super(voxelshapediscrete);
        int i = voxelshapediscrete.getXSize() + 1;
        int j = voxelshapediscrete.getYSize() + 1;
        int k = voxelshapediscrete.getZSize() + 1;

        if (i == doublelist.size() && j == doublelist1.size() && k == doublelist2.size()) {
            this.xs = doublelist;
            this.ys = doublelist1;
            this.zs = doublelist2;
        } else {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
        }
    }

    @Override
    protected DoubleList getCoords(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case X:
                return this.xs;
            case Y:
                return this.ys;
            case Z:
                return this.zs;
            default:
                throw new IllegalArgumentException();
        }
    }
}
