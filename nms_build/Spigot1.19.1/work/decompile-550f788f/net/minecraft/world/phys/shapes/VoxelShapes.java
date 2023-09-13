package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumAxisCycle;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.phys.AxisAlignedBB;

public final class VoxelShapes {

    public static final double EPSILON = 1.0E-7D;
    public static final double BIG_EPSILON = 1.0E-6D;
    private static final VoxelShape BLOCK = (VoxelShape) SystemUtils.make(() -> {
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(1, 1, 1);

        voxelshapebitset.fill(0, 0, 0);
        return new VoxelShapeCube(voxelshapebitset);
    });
    public static final VoxelShape INFINITY = box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new VoxelShapeArray(new VoxelShapeBitSet(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}));

    public VoxelShapes() {}

    public static VoxelShape empty() {
        return VoxelShapes.EMPTY;
    }

    public static VoxelShape block() {
        return VoxelShapes.BLOCK;
    }

    public static VoxelShape box(double d0, double d1, double d2, double d3, double d4, double d5) {
        if (d0 <= d3 && d1 <= d4 && d2 <= d5) {
            return create(d0, d1, d2, d3, d4, d5);
        } else {
            throw new IllegalArgumentException("The min values need to be smaller or equals to the max values");
        }
    }

    public static VoxelShape create(double d0, double d1, double d2, double d3, double d4, double d5) {
        if (d3 - d0 >= 1.0E-7D && d4 - d1 >= 1.0E-7D && d5 - d2 >= 1.0E-7D) {
            int i = findBits(d0, d3);
            int j = findBits(d1, d4);
            int k = findBits(d2, d5);

            if (i >= 0 && j >= 0 && k >= 0) {
                if (i == 0 && j == 0 && k == 0) {
                    return block();
                } else {
                    int l = 1 << i;
                    int i1 = 1 << j;
                    int j1 = 1 << k;
                    VoxelShapeBitSet voxelshapebitset = VoxelShapeBitSet.withFilledBounds(l, i1, j1, (int) Math.round(d0 * (double) l), (int) Math.round(d1 * (double) i1), (int) Math.round(d2 * (double) j1), (int) Math.round(d3 * (double) l), (int) Math.round(d4 * (double) i1), (int) Math.round(d5 * (double) j1));

                    return new VoxelShapeCube(voxelshapebitset);
                }
            } else {
                return new VoxelShapeArray(VoxelShapes.BLOCK.shape, DoubleArrayList.wrap(new double[]{d0, d3}), DoubleArrayList.wrap(new double[]{d1, d4}), DoubleArrayList.wrap(new double[]{d2, d5}));
            }
        } else {
            return empty();
        }
    }

    public static VoxelShape create(AxisAlignedBB axisalignedbb) {
        return create(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
    }

    @VisibleForTesting
    protected static int findBits(double d0, double d1) {
        if (d0 >= -1.0E-7D && d1 <= 1.0000001D) {
            for (int i = 0; i <= 3; ++i) {
                int j = 1 << i;
                double d2 = d0 * (double) j;
                double d3 = d1 * (double) j;
                boolean flag = Math.abs(d2 - (double) Math.round(d2)) < 1.0E-7D * (double) j;
                boolean flag1 = Math.abs(d3 - (double) Math.round(d3)) < 1.0E-7D * (double) j;

                if (flag && flag1) {
                    return i;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    protected static long lcm(int i, int j) {
        return (long) i * (long) (j / IntMath.gcd(i, j));
    }

    public static VoxelShape or(VoxelShape voxelshape, VoxelShape voxelshape1) {
        return join(voxelshape, voxelshape1, OperatorBoolean.OR);
    }

    public static VoxelShape or(VoxelShape voxelshape, VoxelShape... avoxelshape) {
        return (VoxelShape) Arrays.stream(avoxelshape).reduce(voxelshape, VoxelShapes::or);
    }

    public static VoxelShape join(VoxelShape voxelshape, VoxelShape voxelshape1, OperatorBoolean operatorboolean) {
        return joinUnoptimized(voxelshape, voxelshape1, operatorboolean).optimize();
    }

    public static VoxelShape joinUnoptimized(VoxelShape voxelshape, VoxelShape voxelshape1, OperatorBoolean operatorboolean) {
        if (operatorboolean.apply(false, false)) {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException());
        } else if (voxelshape == voxelshape1) {
            return operatorboolean.apply(true, true) ? voxelshape : empty();
        } else {
            boolean flag = operatorboolean.apply(true, false);
            boolean flag1 = operatorboolean.apply(false, true);

            if (voxelshape.isEmpty()) {
                return flag1 ? voxelshape1 : empty();
            } else if (voxelshape1.isEmpty()) {
                return flag ? voxelshape : empty();
            } else {
                VoxelShapeMerger voxelshapemerger = createIndexMerger(1, voxelshape.getCoords(EnumDirection.EnumAxis.X), voxelshape1.getCoords(EnumDirection.EnumAxis.X), flag, flag1);
                VoxelShapeMerger voxelshapemerger1 = createIndexMerger(voxelshapemerger.size() - 1, voxelshape.getCoords(EnumDirection.EnumAxis.Y), voxelshape1.getCoords(EnumDirection.EnumAxis.Y), flag, flag1);
                VoxelShapeMerger voxelshapemerger2 = createIndexMerger((voxelshapemerger.size() - 1) * (voxelshapemerger1.size() - 1), voxelshape.getCoords(EnumDirection.EnumAxis.Z), voxelshape1.getCoords(EnumDirection.EnumAxis.Z), flag, flag1);
                VoxelShapeBitSet voxelshapebitset = VoxelShapeBitSet.join(voxelshape.shape, voxelshape1.shape, voxelshapemerger, voxelshapemerger1, voxelshapemerger2, operatorboolean);

                return (VoxelShape) (voxelshapemerger instanceof VoxelShapeCubeMerger && voxelshapemerger1 instanceof VoxelShapeCubeMerger && voxelshapemerger2 instanceof VoxelShapeCubeMerger ? new VoxelShapeCube(voxelshapebitset) : new VoxelShapeArray(voxelshapebitset, voxelshapemerger.getList(), voxelshapemerger1.getList(), voxelshapemerger2.getList()));
            }
        }
    }

    public static boolean joinIsNotEmpty(VoxelShape voxelshape, VoxelShape voxelshape1, OperatorBoolean operatorboolean) {
        if (operatorboolean.apply(false, false)) {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException());
        } else {
            boolean flag = voxelshape.isEmpty();
            boolean flag1 = voxelshape1.isEmpty();

            if (!flag && !flag1) {
                if (voxelshape == voxelshape1) {
                    return operatorboolean.apply(true, true);
                } else {
                    boolean flag2 = operatorboolean.apply(true, false);
                    boolean flag3 = operatorboolean.apply(false, true);
                    EnumDirection.EnumAxis[] aenumdirection_enumaxis = EnumAxisCycle.AXIS_VALUES;
                    int i = aenumdirection_enumaxis.length;

                    for (int j = 0; j < i; ++j) {
                        EnumDirection.EnumAxis enumdirection_enumaxis = aenumdirection_enumaxis[j];

                        if (voxelshape.max(enumdirection_enumaxis) < voxelshape1.min(enumdirection_enumaxis) - 1.0E-7D) {
                            return flag2 || flag3;
                        }

                        if (voxelshape1.max(enumdirection_enumaxis) < voxelshape.min(enumdirection_enumaxis) - 1.0E-7D) {
                            return flag2 || flag3;
                        }
                    }

                    VoxelShapeMerger voxelshapemerger = createIndexMerger(1, voxelshape.getCoords(EnumDirection.EnumAxis.X), voxelshape1.getCoords(EnumDirection.EnumAxis.X), flag2, flag3);
                    VoxelShapeMerger voxelshapemerger1 = createIndexMerger(voxelshapemerger.size() - 1, voxelshape.getCoords(EnumDirection.EnumAxis.Y), voxelshape1.getCoords(EnumDirection.EnumAxis.Y), flag2, flag3);
                    VoxelShapeMerger voxelshapemerger2 = createIndexMerger((voxelshapemerger.size() - 1) * (voxelshapemerger1.size() - 1), voxelshape.getCoords(EnumDirection.EnumAxis.Z), voxelshape1.getCoords(EnumDirection.EnumAxis.Z), flag2, flag3);

                    return joinIsNotEmpty(voxelshapemerger, voxelshapemerger1, voxelshapemerger2, voxelshape.shape, voxelshape1.shape, operatorboolean);
                }
            } else {
                return operatorboolean.apply(!flag, !flag1);
            }
        }
    }

    private static boolean joinIsNotEmpty(VoxelShapeMerger voxelshapemerger, VoxelShapeMerger voxelshapemerger1, VoxelShapeMerger voxelshapemerger2, VoxelShapeDiscrete voxelshapediscrete, VoxelShapeDiscrete voxelshapediscrete1, OperatorBoolean operatorboolean) {
        return !voxelshapemerger.forMergedIndexes((i, j, k) -> {
            return voxelshapemerger1.forMergedIndexes((l, i1, j1) -> {
                return voxelshapemerger2.forMergedIndexes((k1, l1, i2) -> {
                    return !operatorboolean.apply(voxelshapediscrete.isFullWide(i, l, k1), voxelshapediscrete1.isFullWide(j, i1, l1));
                });
            });
        });
    }

    public static double collide(EnumDirection.EnumAxis enumdirection_enumaxis, AxisAlignedBB axisalignedbb, Iterable<VoxelShape> iterable, double d0) {
        VoxelShape voxelshape;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); d0 = voxelshape.collide(enumdirection_enumaxis, axisalignedbb, d0)) {
            voxelshape = (VoxelShape) iterator.next();
            if (Math.abs(d0) < 1.0E-7D) {
                return 0.0D;
            }
        }

        return d0;
    }

    public static boolean blockOccudes(VoxelShape voxelshape, VoxelShape voxelshape1, EnumDirection enumdirection) {
        if (voxelshape == block() && voxelshape1 == block()) {
            return true;
        } else if (voxelshape1.isEmpty()) {
            return false;
        } else {
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
            EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection = enumdirection.getAxisDirection();
            VoxelShape voxelshape2 = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? voxelshape : voxelshape1;
            VoxelShape voxelshape3 = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? voxelshape1 : voxelshape;
            OperatorBoolean operatorboolean = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? OperatorBoolean.ONLY_FIRST : OperatorBoolean.ONLY_SECOND;

            return DoubleMath.fuzzyEquals(voxelshape2.max(enumdirection_enumaxis), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(voxelshape3.min(enumdirection_enumaxis), 0.0D, 1.0E-7D) && !joinIsNotEmpty(new VoxelShapeSlice(voxelshape2, enumdirection_enumaxis, voxelshape2.shape.getSize(enumdirection_enumaxis) - 1), new VoxelShapeSlice(voxelshape3, enumdirection_enumaxis, 0), operatorboolean);
        }
    }

    public static VoxelShape getFaceShape(VoxelShape voxelshape, EnumDirection enumdirection) {
        if (voxelshape == block()) {
            return block();
        } else {
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
            boolean flag;
            int i;

            if (enumdirection.getAxisDirection() == EnumDirection.EnumAxisDirection.POSITIVE) {
                flag = DoubleMath.fuzzyEquals(voxelshape.max(enumdirection_enumaxis), 1.0D, 1.0E-7D);
                i = voxelshape.shape.getSize(enumdirection_enumaxis) - 1;
            } else {
                flag = DoubleMath.fuzzyEquals(voxelshape.min(enumdirection_enumaxis), 0.0D, 1.0E-7D);
                i = 0;
            }

            return (VoxelShape) (!flag ? empty() : new VoxelShapeSlice(voxelshape, enumdirection_enumaxis, i));
        }
    }

    public static boolean mergedFaceOccludes(VoxelShape voxelshape, VoxelShape voxelshape1, EnumDirection enumdirection) {
        if (voxelshape != block() && voxelshape1 != block()) {
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
            EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection = enumdirection.getAxisDirection();
            VoxelShape voxelshape2 = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? voxelshape : voxelshape1;
            VoxelShape voxelshape3 = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? voxelshape1 : voxelshape;

            if (!DoubleMath.fuzzyEquals(voxelshape2.max(enumdirection_enumaxis), 1.0D, 1.0E-7D)) {
                voxelshape2 = empty();
            }

            if (!DoubleMath.fuzzyEquals(voxelshape3.min(enumdirection_enumaxis), 0.0D, 1.0E-7D)) {
                voxelshape3 = empty();
            }

            return !joinIsNotEmpty(block(), joinUnoptimized(new VoxelShapeSlice(voxelshape2, enumdirection_enumaxis, voxelshape2.shape.getSize(enumdirection_enumaxis) - 1), new VoxelShapeSlice(voxelshape3, enumdirection_enumaxis, 0), OperatorBoolean.OR), OperatorBoolean.ONLY_FIRST);
        } else {
            return true;
        }
    }

    public static boolean faceShapeOccludes(VoxelShape voxelshape, VoxelShape voxelshape1) {
        return voxelshape != block() && voxelshape1 != block() ? (voxelshape.isEmpty() && voxelshape1.isEmpty() ? false : !joinIsNotEmpty(block(), joinUnoptimized(voxelshape, voxelshape1, OperatorBoolean.OR), OperatorBoolean.ONLY_FIRST)) : true;
    }

    @VisibleForTesting
    protected static VoxelShapeMerger createIndexMerger(int i, DoubleList doublelist, DoubleList doublelist1, boolean flag, boolean flag1) {
        int j = doublelist.size() - 1;
        int k = doublelist1.size() - 1;

        if (doublelist instanceof VoxelShapeCubePoint && doublelist1 instanceof VoxelShapeCubePoint) {
            long l = lcm(j, k);

            if ((long) i * l <= 256L) {
                return new VoxelShapeCubeMerger(j, k);
            }
        }

        return (VoxelShapeMerger) (doublelist.getDouble(j) < doublelist1.getDouble(0) - 1.0E-7D ? new VoxelShapeMergerDisjoint(doublelist, doublelist1, false) : (doublelist1.getDouble(k) < doublelist.getDouble(0) - 1.0E-7D ? new VoxelShapeMergerDisjoint(doublelist1, doublelist, true) : (j == k && Objects.equals(doublelist, doublelist1) ? new VoxelShapeMergerIdentical(doublelist) : new VoxelShapeMergerList(doublelist, doublelist1, flag, flag1))));
    }

    public interface a {

        void consume(double d0, double d1, double d2, double d3, double d4, double d5);
    }
}
