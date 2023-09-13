package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockTall extends Block implements IBlockWaterlogged {

    public static final BlockStateBoolean NORTH = BlockSprawling.a;
    public static final BlockStateBoolean EAST = BlockSprawling.b;
    public static final BlockStateBoolean SOUTH = BlockSprawling.c;
    public static final BlockStateBoolean WEST = BlockSprawling.d;
    public static final BlockStateBoolean e = BlockProperties.C;
    protected static final Map<EnumDirection, BlockStateBoolean> f = (Map) BlockSprawling.g.entrySet().stream().filter((entry) -> {
        return ((EnumDirection) entry.getKey()).n().d();
    }).collect(SystemUtils.a());
    protected final VoxelShape[] g;
    protected final VoxelShape[] h;
    private final Object2IntMap<IBlockData> i = new Object2IntOpenHashMap();

    protected BlockTall(float f, float f1, float f2, float f3, float f4, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.g = this.a(f, f1, f4, 0.0F, f4);
        this.h = this.a(f, f1, f2, 0.0F, f3);
        UnmodifiableIterator unmodifiableiterator = this.blockStateList.a().iterator();

        while (unmodifiableiterator.hasNext()) {
            IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

            this.g(iblockdata);
        }

    }

    protected VoxelShape[] a(float f, float f1, float f2, float f3, float f4) {
        float f5 = 8.0F - f;
        float f6 = 8.0F + f;
        float f7 = 8.0F - f1;
        float f8 = 8.0F + f1;
        VoxelShape voxelshape = Block.a((double) f5, 0.0D, (double) f5, (double) f6, (double) f2, (double) f6);
        VoxelShape voxelshape1 = Block.a((double) f7, (double) f3, 0.0D, (double) f8, (double) f4, (double) f8);
        VoxelShape voxelshape2 = Block.a((double) f7, (double) f3, (double) f7, (double) f8, (double) f4, 16.0D);
        VoxelShape voxelshape3 = Block.a(0.0D, (double) f3, (double) f7, (double) f8, (double) f4, (double) f8);
        VoxelShape voxelshape4 = Block.a((double) f7, (double) f3, (double) f7, 16.0D, (double) f4, (double) f8);
        VoxelShape voxelshape5 = VoxelShapes.a(voxelshape1, voxelshape4);
        VoxelShape voxelshape6 = VoxelShapes.a(voxelshape2, voxelshape3);
        VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.a(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.a(voxelshape2, voxelshape1), VoxelShapes.a(voxelshape3, voxelshape1), VoxelShapes.a(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.a(voxelshape2, voxelshape4), VoxelShapes.a(voxelshape3, voxelshape4), VoxelShapes.a(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.a(voxelshape2, voxelshape5), VoxelShapes.a(voxelshape3, voxelshape5), VoxelShapes.a(voxelshape6, voxelshape5)};

        for (int i = 0; i < 16; ++i) {
            avoxelshape[i] = VoxelShapes.a(voxelshape, avoxelshape[i]);
        }

        return avoxelshape;
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !(Boolean) iblockdata.get(BlockTall.e);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.h[this.g(iblockdata)];
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.g[this.g(iblockdata)];
    }

    private static int a(EnumDirection enumdirection) {
        return 1 << enumdirection.get2DRotationValue();
    }

    protected int g(IBlockData iblockdata) {
        return this.i.computeIntIfAbsent(iblockdata, (iblockdata1) -> {
            int i = 0;

            if ((Boolean) iblockdata1.get(BlockTall.NORTH)) {
                i |= a(EnumDirection.NORTH);
            }

            if ((Boolean) iblockdata1.get(BlockTall.EAST)) {
                i |= a(EnumDirection.EAST);
            }

            if ((Boolean) iblockdata1.get(BlockTall.SOUTH)) {
                i |= a(EnumDirection.SOUTH);
            }

            if ((Boolean) iblockdata1.get(BlockTall.WEST)) {
                i |= a(EnumDirection.WEST);
            }

            return i;
        });
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockTall.e) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockTall.NORTH, iblockdata.get(BlockTall.SOUTH))).set(BlockTall.EAST, iblockdata.get(BlockTall.WEST))).set(BlockTall.SOUTH, iblockdata.get(BlockTall.NORTH))).set(BlockTall.WEST, iblockdata.get(BlockTall.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockTall.NORTH, iblockdata.get(BlockTall.EAST))).set(BlockTall.EAST, iblockdata.get(BlockTall.SOUTH))).set(BlockTall.SOUTH, iblockdata.get(BlockTall.WEST))).set(BlockTall.WEST, iblockdata.get(BlockTall.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockTall.NORTH, iblockdata.get(BlockTall.WEST))).set(BlockTall.EAST, iblockdata.get(BlockTall.NORTH))).set(BlockTall.SOUTH, iblockdata.get(BlockTall.EAST))).set(BlockTall.WEST, iblockdata.get(BlockTall.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockTall.NORTH, iblockdata.get(BlockTall.SOUTH))).set(BlockTall.SOUTH, iblockdata.get(BlockTall.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockTall.EAST, iblockdata.get(BlockTall.WEST))).set(BlockTall.WEST, iblockdata.get(BlockTall.EAST));
            default:
                return super.a(iblockdata, enumblockmirror);
        }
    }
}
