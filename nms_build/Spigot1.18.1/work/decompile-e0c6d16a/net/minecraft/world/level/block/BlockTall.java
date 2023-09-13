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

    public static final BlockStateBoolean NORTH = BlockSprawling.NORTH;
    public static final BlockStateBoolean EAST = BlockSprawling.EAST;
    public static final BlockStateBoolean SOUTH = BlockSprawling.SOUTH;
    public static final BlockStateBoolean WEST = BlockSprawling.WEST;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = (Map) BlockSprawling.PROPERTY_BY_DIRECTION.entrySet().stream().filter((entry) -> {
        return ((EnumDirection) entry.getKey()).getAxis().isHorizontal();
    }).collect(SystemUtils.toMap());
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<IBlockData> stateToIndex = new Object2IntOpenHashMap();

    protected BlockTall(float f, float f1, float f2, float f3, float f4, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.collisionShapeByIndex = this.makeShapes(f, f1, f4, 0.0F, f4);
        this.shapeByIndex = this.makeShapes(f, f1, f2, 0.0F, f3);
        UnmodifiableIterator unmodifiableiterator = this.stateDefinition.getPossibleStates().iterator();

        while (unmodifiableiterator.hasNext()) {
            IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

            this.getAABBIndex(iblockdata);
        }

    }

    protected VoxelShape[] makeShapes(float f, float f1, float f2, float f3, float f4) {
        float f5 = 8.0F - f;
        float f6 = 8.0F + f;
        float f7 = 8.0F - f1;
        float f8 = 8.0F + f1;
        VoxelShape voxelshape = Block.box((double) f5, 0.0D, (double) f5, (double) f6, (double) f2, (double) f6);
        VoxelShape voxelshape1 = Block.box((double) f7, (double) f3, 0.0D, (double) f8, (double) f4, (double) f8);
        VoxelShape voxelshape2 = Block.box((double) f7, (double) f3, (double) f7, (double) f8, (double) f4, 16.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, (double) f3, (double) f7, (double) f8, (double) f4, (double) f8);
        VoxelShape voxelshape4 = Block.box((double) f7, (double) f3, (double) f7, 16.0D, (double) f4, (double) f8);
        VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
        VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
        VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

        for (int i = 0; i < 16; ++i) {
            avoxelshape[i] = VoxelShapes.or(voxelshape, avoxelshape[i]);
        }

        return avoxelshape;
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !(Boolean) iblockdata.getValue(BlockTall.WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.shapeByIndex[this.getAABBIndex(iblockdata)];
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.collisionShapeByIndex[this.getAABBIndex(iblockdata)];
    }

    private static int indexFor(EnumDirection enumdirection) {
        return 1 << enumdirection.get2DDataValue();
    }

    protected int getAABBIndex(IBlockData iblockdata) {
        return this.stateToIndex.computeIntIfAbsent(iblockdata, (iblockdata1) -> {
            int i = 0;

            if ((Boolean) iblockdata1.getValue(BlockTall.NORTH)) {
                i |= indexFor(EnumDirection.NORTH);
            }

            if ((Boolean) iblockdata1.getValue(BlockTall.EAST)) {
                i |= indexFor(EnumDirection.EAST);
            }

            if ((Boolean) iblockdata1.getValue(BlockTall.SOUTH)) {
                i |= indexFor(EnumDirection.SOUTH);
            }

            if ((Boolean) iblockdata1.getValue(BlockTall.WEST)) {
                i |= indexFor(EnumDirection.WEST);
            }

            return i;
        });
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockTall.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTall.NORTH, (Boolean) iblockdata.getValue(BlockTall.SOUTH))).setValue(BlockTall.EAST, (Boolean) iblockdata.getValue(BlockTall.WEST))).setValue(BlockTall.SOUTH, (Boolean) iblockdata.getValue(BlockTall.NORTH))).setValue(BlockTall.WEST, (Boolean) iblockdata.getValue(BlockTall.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTall.NORTH, (Boolean) iblockdata.getValue(BlockTall.EAST))).setValue(BlockTall.EAST, (Boolean) iblockdata.getValue(BlockTall.SOUTH))).setValue(BlockTall.SOUTH, (Boolean) iblockdata.getValue(BlockTall.WEST))).setValue(BlockTall.WEST, (Boolean) iblockdata.getValue(BlockTall.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTall.NORTH, (Boolean) iblockdata.getValue(BlockTall.WEST))).setValue(BlockTall.EAST, (Boolean) iblockdata.getValue(BlockTall.NORTH))).setValue(BlockTall.SOUTH, (Boolean) iblockdata.getValue(BlockTall.EAST))).setValue(BlockTall.WEST, (Boolean) iblockdata.getValue(BlockTall.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockTall.NORTH, (Boolean) iblockdata.getValue(BlockTall.SOUTH))).setValue(BlockTall.SOUTH, (Boolean) iblockdata.getValue(BlockTall.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockTall.EAST, (Boolean) iblockdata.getValue(BlockTall.WEST))).setValue(BlockTall.WEST, (Boolean) iblockdata.getValue(BlockTall.EAST));
            default:
                return super.mirror(iblockdata, enumblockmirror);
        }
    }
}
