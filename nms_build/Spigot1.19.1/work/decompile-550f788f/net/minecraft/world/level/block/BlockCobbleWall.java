package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyWallHeight;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockCobbleWall extends Block implements IBlockWaterlogged {

    public static final BlockStateBoolean UP = BlockProperties.UP;
    public static final BlockStateEnum<BlockPropertyWallHeight> EAST_WALL = BlockProperties.EAST_WALL;
    public static final BlockStateEnum<BlockPropertyWallHeight> NORTH_WALL = BlockProperties.NORTH_WALL;
    public static final BlockStateEnum<BlockPropertyWallHeight> SOUTH_WALL = BlockProperties.SOUTH_WALL;
    public static final BlockStateEnum<BlockPropertyWallHeight> WEST_WALL = BlockProperties.WEST_WALL;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private final Map<IBlockData, VoxelShape> shapeByIndex;
    private final Map<IBlockData, VoxelShape> collisionShapeByIndex;
    private static final int WALL_WIDTH = 3;
    private static final int WALL_HEIGHT = 14;
    private static final int POST_WIDTH = 4;
    private static final int POST_COVER_WIDTH = 1;
    private static final int WALL_COVER_START = 7;
    private static final int WALL_COVER_END = 9;
    private static final VoxelShape POST_TEST = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape NORTH_TEST = Block.box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape SOUTH_TEST = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_TEST = Block.box(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape EAST_TEST = Block.box(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public BlockCobbleWall(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockCobbleWall.UP, true)).setValue(BlockCobbleWall.NORTH_WALL, BlockPropertyWallHeight.NONE)).setValue(BlockCobbleWall.EAST_WALL, BlockPropertyWallHeight.NONE)).setValue(BlockCobbleWall.SOUTH_WALL, BlockPropertyWallHeight.NONE)).setValue(BlockCobbleWall.WEST_WALL, BlockPropertyWallHeight.NONE)).setValue(BlockCobbleWall.WATERLOGGED, false));
        this.shapeByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape applyWallShape(VoxelShape voxelshape, BlockPropertyWallHeight blockpropertywallheight, VoxelShape voxelshape1, VoxelShape voxelshape2) {
        return blockpropertywallheight == BlockPropertyWallHeight.TALL ? VoxelShapes.or(voxelshape, voxelshape2) : (blockpropertywallheight == BlockPropertyWallHeight.LOW ? VoxelShapes.or(voxelshape, voxelshape1) : voxelshape);
    }

    private Map<IBlockData, VoxelShape> makeShapes(float f, float f1, float f2, float f3, float f4, float f5) {
        float f6 = 8.0F - f;
        float f7 = 8.0F + f;
        float f8 = 8.0F - f1;
        float f9 = 8.0F + f1;
        VoxelShape voxelshape = Block.box((double) f6, 0.0D, (double) f6, (double) f7, (double) f2, (double) f7);
        VoxelShape voxelshape1 = Block.box((double) f8, (double) f3, 0.0D, (double) f9, (double) f4, (double) f9);
        VoxelShape voxelshape2 = Block.box((double) f8, (double) f3, (double) f8, (double) f9, (double) f4, 16.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, (double) f3, (double) f8, (double) f9, (double) f4, (double) f9);
        VoxelShape voxelshape4 = Block.box((double) f8, (double) f3, (double) f8, 16.0D, (double) f4, (double) f9);
        VoxelShape voxelshape5 = Block.box((double) f8, (double) f3, 0.0D, (double) f9, (double) f5, (double) f9);
        VoxelShape voxelshape6 = Block.box((double) f8, (double) f3, (double) f8, (double) f9, (double) f5, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, (double) f3, (double) f8, (double) f9, (double) f5, (double) f9);
        VoxelShape voxelshape8 = Block.box((double) f8, (double) f3, (double) f8, 16.0D, (double) f5, (double) f9);
        Builder<IBlockData, VoxelShape> builder = ImmutableMap.builder();
        Iterator iterator = BlockCobbleWall.UP.getPossibleValues().iterator();

        while (iterator.hasNext()) {
            Boolean obool = (Boolean) iterator.next();
            Iterator iterator1 = BlockCobbleWall.EAST_WALL.getPossibleValues().iterator();

            while (iterator1.hasNext()) {
                BlockPropertyWallHeight blockpropertywallheight = (BlockPropertyWallHeight) iterator1.next();
                Iterator iterator2 = BlockCobbleWall.NORTH_WALL.getPossibleValues().iterator();

                while (iterator2.hasNext()) {
                    BlockPropertyWallHeight blockpropertywallheight1 = (BlockPropertyWallHeight) iterator2.next();
                    Iterator iterator3 = BlockCobbleWall.WEST_WALL.getPossibleValues().iterator();

                    while (iterator3.hasNext()) {
                        BlockPropertyWallHeight blockpropertywallheight2 = (BlockPropertyWallHeight) iterator3.next();
                        Iterator iterator4 = BlockCobbleWall.SOUTH_WALL.getPossibleValues().iterator();

                        while (iterator4.hasNext()) {
                            BlockPropertyWallHeight blockpropertywallheight3 = (BlockPropertyWallHeight) iterator4.next();
                            VoxelShape voxelshape9 = VoxelShapes.empty();

                            voxelshape9 = applyWallShape(voxelshape9, blockpropertywallheight, voxelshape4, voxelshape8);
                            voxelshape9 = applyWallShape(voxelshape9, blockpropertywallheight2, voxelshape3, voxelshape7);
                            voxelshape9 = applyWallShape(voxelshape9, blockpropertywallheight1, voxelshape1, voxelshape5);
                            voxelshape9 = applyWallShape(voxelshape9, blockpropertywallheight3, voxelshape2, voxelshape6);
                            if (obool) {
                                voxelshape9 = VoxelShapes.or(voxelshape9, voxelshape);
                            }

                            IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockCobbleWall.UP, obool)).setValue(BlockCobbleWall.EAST_WALL, blockpropertywallheight)).setValue(BlockCobbleWall.WEST_WALL, blockpropertywallheight2)).setValue(BlockCobbleWall.NORTH_WALL, blockpropertywallheight1)).setValue(BlockCobbleWall.SOUTH_WALL, blockpropertywallheight3);

                            builder.put((IBlockData) iblockdata.setValue(BlockCobbleWall.WATERLOGGED, false), voxelshape9);
                            builder.put((IBlockData) iblockdata.setValue(BlockCobbleWall.WATERLOGGED, true), voxelshape9);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapeByIndex.get(iblockdata);
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.collisionShapeByIndex.get(iblockdata);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    private boolean connectsTo(IBlockData iblockdata, boolean flag, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();
        boolean flag1 = block instanceof BlockFenceGate && BlockFenceGate.connectsToDirection(iblockdata, enumdirection);

        return iblockdata.is(TagsBlock.WALLS) || !isExceptionForConnection(iblockdata) && flag || block instanceof BlockIronBars || flag1;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        BlockPosition blockposition5 = blockposition.above();
        IBlockData iblockdata = world.getBlockState(blockposition1);
        IBlockData iblockdata1 = world.getBlockState(blockposition2);
        IBlockData iblockdata2 = world.getBlockState(blockposition3);
        IBlockData iblockdata3 = world.getBlockState(blockposition4);
        IBlockData iblockdata4 = world.getBlockState(blockposition5);
        boolean flag = this.connectsTo(iblockdata, iblockdata.isFaceSturdy(world, blockposition1, EnumDirection.SOUTH), EnumDirection.SOUTH);
        boolean flag1 = this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(world, blockposition2, EnumDirection.WEST), EnumDirection.WEST);
        boolean flag2 = this.connectsTo(iblockdata2, iblockdata2.isFaceSturdy(world, blockposition3, EnumDirection.NORTH), EnumDirection.NORTH);
        boolean flag3 = this.connectsTo(iblockdata3, iblockdata3.isFaceSturdy(world, blockposition4, EnumDirection.EAST), EnumDirection.EAST);
        IBlockData iblockdata5 = (IBlockData) this.defaultBlockState().setValue(BlockCobbleWall.WATERLOGGED, fluid.getType() == FluidTypes.WATER);

        return this.updateShape(world, iblockdata5, blockposition5, iblockdata4, flag, flag1, flag2, flag3);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockCobbleWall.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return enumdirection == EnumDirection.DOWN ? super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1) : (enumdirection == EnumDirection.UP ? this.topUpdate(generatoraccess, iblockdata, blockposition1, iblockdata1) : this.sideUpdate(generatoraccess, blockposition, iblockdata, blockposition1, iblockdata1, enumdirection));
    }

    private static boolean isConnected(IBlockData iblockdata, IBlockState<BlockPropertyWallHeight> iblockstate) {
        return iblockdata.getValue(iblockstate) != BlockPropertyWallHeight.NONE;
    }

    private static boolean isCovered(VoxelShape voxelshape, VoxelShape voxelshape1) {
        return !VoxelShapes.joinIsNotEmpty(voxelshape1, voxelshape, OperatorBoolean.ONLY_FIRST);
    }

    private IBlockData topUpdate(IWorldReader iworldreader, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1) {
        boolean flag = isConnected(iblockdata, BlockCobbleWall.NORTH_WALL);
        boolean flag1 = isConnected(iblockdata, BlockCobbleWall.EAST_WALL);
        boolean flag2 = isConnected(iblockdata, BlockCobbleWall.SOUTH_WALL);
        boolean flag3 = isConnected(iblockdata, BlockCobbleWall.WEST_WALL);

        return this.updateShape(iworldreader, iblockdata, blockposition, iblockdata1, flag, flag1, flag2, flag3);
    }

    private IBlockData sideUpdate(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1, EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.getOpposite();
        boolean flag = enumdirection == EnumDirection.NORTH ? this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(iworldreader, blockposition1, enumdirection1), enumdirection1) : isConnected(iblockdata, BlockCobbleWall.NORTH_WALL);
        boolean flag1 = enumdirection == EnumDirection.EAST ? this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(iworldreader, blockposition1, enumdirection1), enumdirection1) : isConnected(iblockdata, BlockCobbleWall.EAST_WALL);
        boolean flag2 = enumdirection == EnumDirection.SOUTH ? this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(iworldreader, blockposition1, enumdirection1), enumdirection1) : isConnected(iblockdata, BlockCobbleWall.SOUTH_WALL);
        boolean flag3 = enumdirection == EnumDirection.WEST ? this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(iworldreader, blockposition1, enumdirection1), enumdirection1) : isConnected(iblockdata, BlockCobbleWall.WEST_WALL);
        BlockPosition blockposition2 = blockposition.above();
        IBlockData iblockdata2 = iworldreader.getBlockState(blockposition2);

        return this.updateShape(iworldreader, iblockdata, blockposition2, iblockdata2, flag, flag1, flag2, flag3);
    }

    private IBlockData updateShape(IWorldReader iworldreader, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        VoxelShape voxelshape = iblockdata1.getCollisionShape(iworldreader, blockposition).getFaceShape(EnumDirection.DOWN);
        IBlockData iblockdata2 = this.updateSides(iblockdata, flag, flag1, flag2, flag3, voxelshape);

        return (IBlockData) iblockdata2.setValue(BlockCobbleWall.UP, this.shouldRaisePost(iblockdata2, iblockdata1, voxelshape));
    }

    private boolean shouldRaisePost(IBlockData iblockdata, IBlockData iblockdata1, VoxelShape voxelshape) {
        boolean flag = iblockdata1.getBlock() instanceof BlockCobbleWall && (Boolean) iblockdata1.getValue(BlockCobbleWall.UP);

        if (flag) {
            return true;
        } else {
            BlockPropertyWallHeight blockpropertywallheight = (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.NORTH_WALL);
            BlockPropertyWallHeight blockpropertywallheight1 = (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.SOUTH_WALL);
            BlockPropertyWallHeight blockpropertywallheight2 = (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.EAST_WALL);
            BlockPropertyWallHeight blockpropertywallheight3 = (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.WEST_WALL);
            boolean flag1 = blockpropertywallheight1 == BlockPropertyWallHeight.NONE;
            boolean flag2 = blockpropertywallheight3 == BlockPropertyWallHeight.NONE;
            boolean flag3 = blockpropertywallheight2 == BlockPropertyWallHeight.NONE;
            boolean flag4 = blockpropertywallheight == BlockPropertyWallHeight.NONE;
            boolean flag5 = flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;

            if (flag5) {
                return true;
            } else {
                boolean flag6 = blockpropertywallheight == BlockPropertyWallHeight.TALL && blockpropertywallheight1 == BlockPropertyWallHeight.TALL || blockpropertywallheight2 == BlockPropertyWallHeight.TALL && blockpropertywallheight3 == BlockPropertyWallHeight.TALL;

                return flag6 ? false : iblockdata1.is(TagsBlock.WALL_POST_OVERRIDE) || isCovered(voxelshape, BlockCobbleWall.POST_TEST);
            }
        }
    }

    private IBlockData updateSides(IBlockData iblockdata, boolean flag, boolean flag1, boolean flag2, boolean flag3, VoxelShape voxelshape) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.NORTH_WALL, this.makeWallState(flag, voxelshape, BlockCobbleWall.NORTH_TEST))).setValue(BlockCobbleWall.EAST_WALL, this.makeWallState(flag1, voxelshape, BlockCobbleWall.EAST_TEST))).setValue(BlockCobbleWall.SOUTH_WALL, this.makeWallState(flag2, voxelshape, BlockCobbleWall.SOUTH_TEST))).setValue(BlockCobbleWall.WEST_WALL, this.makeWallState(flag3, voxelshape, BlockCobbleWall.WEST_TEST));
    }

    private BlockPropertyWallHeight makeWallState(boolean flag, VoxelShape voxelshape, VoxelShape voxelshape1) {
        return flag ? (isCovered(voxelshape, voxelshape1) ? BlockPropertyWallHeight.TALL : BlockPropertyWallHeight.LOW) : BlockPropertyWallHeight.NONE;
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockCobbleWall.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !(Boolean) iblockdata.getValue(BlockCobbleWall.WATERLOGGED);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockCobbleWall.UP, BlockCobbleWall.NORTH_WALL, BlockCobbleWall.EAST_WALL, BlockCobbleWall.WEST_WALL, BlockCobbleWall.SOUTH_WALL, BlockCobbleWall.WATERLOGGED);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.NORTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.SOUTH_WALL))).setValue(BlockCobbleWall.EAST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.WEST_WALL))).setValue(BlockCobbleWall.SOUTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.NORTH_WALL))).setValue(BlockCobbleWall.WEST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.EAST_WALL));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.NORTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.EAST_WALL))).setValue(BlockCobbleWall.EAST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.SOUTH_WALL))).setValue(BlockCobbleWall.SOUTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.WEST_WALL))).setValue(BlockCobbleWall.WEST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.NORTH_WALL));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.NORTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.WEST_WALL))).setValue(BlockCobbleWall.EAST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.NORTH_WALL))).setValue(BlockCobbleWall.SOUTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.EAST_WALL))).setValue(BlockCobbleWall.WEST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.SOUTH_WALL));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.NORTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.SOUTH_WALL))).setValue(BlockCobbleWall.SOUTH_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.NORTH_WALL));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockCobbleWall.EAST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.WEST_WALL))).setValue(BlockCobbleWall.WEST_WALL, (BlockPropertyWallHeight) iblockdata.getValue(BlockCobbleWall.EAST_WALL));
            default:
                return super.mirror(iblockdata, enumblockmirror);
        }
    }
}
