package net.minecraft.world.level.block.piston;

import java.util.Arrays;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDirectional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockPistonExtension extends BlockDirectional {

    public static final BlockStateEnum<BlockPropertyPistonType> TYPE = BlockProperties.PISTON_TYPE;
    public static final BlockStateBoolean SHORT = BlockProperties.SHORT;
    public static final float PLATFORM = 4.0F;
    protected static final VoxelShape EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final float AABB_OFFSET = 2.0F;
    protected static final float EDGE_MIN = 6.0F;
    protected static final float EDGE_MAX = 10.0F;
    protected static final VoxelShape UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
    protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
    protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
    protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    private static final VoxelShape[] SHAPES_SHORT = makeShapes(true);
    private static final VoxelShape[] SHAPES_LONG = makeShapes(false);

    private static VoxelShape[] makeShapes(boolean flag) {
        return (VoxelShape[]) Arrays.stream(EnumDirection.values()).map((enumdirection) -> {
            return calculateShape(enumdirection, flag);
        }).toArray((i) -> {
            return new VoxelShape[i];
        });
    }

    private static VoxelShape calculateShape(EnumDirection enumdirection, boolean flag) {
        switch (enumdirection) {
            case DOWN:
            default:
                return VoxelShapes.or(BlockPistonExtension.DOWN_AABB, flag ? BlockPistonExtension.SHORT_DOWN_ARM_AABB : BlockPistonExtension.DOWN_ARM_AABB);
            case UP:
                return VoxelShapes.or(BlockPistonExtension.UP_AABB, flag ? BlockPistonExtension.SHORT_UP_ARM_AABB : BlockPistonExtension.UP_ARM_AABB);
            case NORTH:
                return VoxelShapes.or(BlockPistonExtension.NORTH_AABB, flag ? BlockPistonExtension.SHORT_NORTH_ARM_AABB : BlockPistonExtension.NORTH_ARM_AABB);
            case SOUTH:
                return VoxelShapes.or(BlockPistonExtension.SOUTH_AABB, flag ? BlockPistonExtension.SHORT_SOUTH_ARM_AABB : BlockPistonExtension.SOUTH_ARM_AABB);
            case WEST:
                return VoxelShapes.or(BlockPistonExtension.WEST_AABB, flag ? BlockPistonExtension.SHORT_WEST_ARM_AABB : BlockPistonExtension.WEST_ARM_AABB);
            case EAST:
                return VoxelShapes.or(BlockPistonExtension.EAST_AABB, flag ? BlockPistonExtension.SHORT_EAST_ARM_AABB : BlockPistonExtension.EAST_ARM_AABB);
        }
    }

    public BlockPistonExtension(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPistonExtension.FACING, EnumDirection.NORTH)).setValue(BlockPistonExtension.TYPE, BlockPropertyPistonType.DEFAULT)).setValue(BlockPistonExtension.SHORT, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return ((Boolean) iblockdata.getValue(BlockPistonExtension.SHORT) ? BlockPistonExtension.SHAPES_SHORT : BlockPistonExtension.SHAPES_LONG)[((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)).ordinal()];
    }

    private boolean isFittingBase(IBlockData iblockdata, IBlockData iblockdata1) {
        Block block = iblockdata.getValue(BlockPistonExtension.TYPE) == BlockPropertyPistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;

        return iblockdata1.is(block) && (Boolean) iblockdata1.getValue(BlockPiston.EXTENDED) && iblockdata1.getValue(BlockPistonExtension.FACING) == iblockdata.getValue(BlockPistonExtension.FACING);
    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.getAbilities().instabuild) {
            BlockPosition blockposition1 = blockposition.relative(((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)).getOpposite());

            if (this.isFittingBase(iblockdata, world.getBlockState(blockposition1))) {
                world.destroyBlock(blockposition1, false);
            }
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
            BlockPosition blockposition1 = blockposition.relative(((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)).getOpposite());

            if (this.isFittingBase(iblockdata, world.getBlockState(blockposition1))) {
                world.destroyBlock(blockposition1, true);
            }

        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.getOpposite() == iblockdata.getValue(BlockPistonExtension.FACING) && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.relative(((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)).getOpposite()));

        return this.isFittingBase(iblockdata, iblockdata1) || iblockdata1.is(Blocks.MOVING_PISTON) && iblockdata1.getValue(BlockPistonExtension.FACING) == iblockdata.getValue(BlockPistonExtension.FACING);
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (iblockdata.canSurvive(world, blockposition)) {
            world.neighborChanged(blockposition.relative(((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)).getOpposite()), block, blockposition1);
        }

    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(iblockdata.getValue(BlockPistonExtension.TYPE) == BlockPropertyPistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockPistonExtension.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockPistonExtension.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPistonExtension.FACING, BlockPistonExtension.TYPE, BlockPistonExtension.SHORT);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
