package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoorHinge;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockDoor extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean OPEN = BlockProperties.OPEN;
    public static final BlockStateEnum<BlockPropertyDoorHinge> HINGE = BlockProperties.DOOR_HINGE;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> HALF = BlockProperties.DOUBLE_BLOCK_HALF;
    protected static final float AABB_DOOR_THICKNESS = 3.0F;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    protected BlockDoor(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockDoor.FACING, EnumDirection.NORTH)).setValue(BlockDoor.OPEN, false)).setValue(BlockDoor.HINGE, BlockPropertyDoorHinge.LEFT)).setValue(BlockDoor.POWERED, false)).setValue(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockDoor.FACING);
        boolean flag = !(Boolean) iblockdata.getValue(BlockDoor.OPEN);
        boolean flag1 = iblockdata.getValue(BlockDoor.HINGE) == BlockPropertyDoorHinge.RIGHT;

        switch (enumdirection) {
            case EAST:
            default:
                return flag ? BlockDoor.EAST_AABB : (flag1 ? BlockDoor.NORTH_AABB : BlockDoor.SOUTH_AABB);
            case SOUTH:
                return flag ? BlockDoor.SOUTH_AABB : (flag1 ? BlockDoor.EAST_AABB : BlockDoor.WEST_AABB);
            case WEST:
                return flag ? BlockDoor.WEST_AABB : (flag1 ? BlockDoor.SOUTH_AABB : BlockDoor.NORTH_AABB);
            case NORTH:
                return flag ? BlockDoor.NORTH_AABB : (flag1 ? BlockDoor.WEST_AABB : BlockDoor.EAST_AABB);
        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.getValue(BlockDoor.HALF);

        return enumdirection.getAxis() == EnumDirection.EnumAxis.Y && blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER == (enumdirection == EnumDirection.UP) ? (iblockdata1.is((Block) this) && iblockdata1.getValue(BlockDoor.HALF) != blockpropertydoubleblockhalf ? (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockDoor.FACING, (EnumDirection) iblockdata1.getValue(BlockDoor.FACING))).setValue(BlockDoor.OPEN, (Boolean) iblockdata1.getValue(BlockDoor.OPEN))).setValue(BlockDoor.HINGE, (BlockPropertyDoorHinge) iblockdata1.getValue(BlockDoor.HINGE))).setValue(BlockDoor.POWERED, (Boolean) iblockdata1.getValue(BlockDoor.POWERED)) : Blocks.AIR.defaultBlockState()) : (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER && enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1));
    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.isCreative()) {
            BlockTallPlant.preventCreativeDropFromBottomPart(world, blockposition, iblockdata, entityhuman);
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return (Boolean) iblockdata.getValue(BlockDoor.OPEN);
            case WATER:
                return false;
            case AIR:
                return (Boolean) iblockdata.getValue(BlockDoor.OPEN);
            default:
                return false;
        }
    }

    private int getCloseSound() {
        return this.material == Material.METAL ? 1011 : 1012;
    }

    private int getOpenSound() {
        return this.material == Material.METAL ? 1005 : 1006;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        World world = blockactioncontext.getLevel();

        if (blockposition.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockposition.above()).canBeReplaced(blockactioncontext)) {
            boolean flag = world.hasNeighborSignal(blockposition) || world.hasNeighborSignal(blockposition.above());

            return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockDoor.FACING, blockactioncontext.getHorizontalDirection())).setValue(BlockDoor.HINGE, this.getHinge(blockactioncontext))).setValue(BlockDoor.POWERED, flag)).setValue(BlockDoor.OPEN, flag)).setValue(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setBlock(blockposition.above(), (IBlockData) iblockdata.setValue(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER), 3);
    }

    private BlockPropertyDoorHinge getHinge(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        EnumDirection enumdirection = blockactioncontext.getHorizontalDirection();
        BlockPosition blockposition1 = blockposition.above();
        EnumDirection enumdirection1 = enumdirection.getCounterClockWise();
        BlockPosition blockposition2 = blockposition.relative(enumdirection1);
        IBlockData iblockdata = world.getBlockState(blockposition2);
        BlockPosition blockposition3 = blockposition1.relative(enumdirection1);
        IBlockData iblockdata1 = world.getBlockState(blockposition3);
        EnumDirection enumdirection2 = enumdirection.getClockWise();
        BlockPosition blockposition4 = blockposition.relative(enumdirection2);
        IBlockData iblockdata2 = world.getBlockState(blockposition4);
        BlockPosition blockposition5 = blockposition1.relative(enumdirection2);
        IBlockData iblockdata3 = world.getBlockState(blockposition5);
        int i = (iblockdata.isCollisionShapeFullBlock(world, blockposition2) ? -1 : 0) + (iblockdata1.isCollisionShapeFullBlock(world, blockposition3) ? -1 : 0) + (iblockdata2.isCollisionShapeFullBlock(world, blockposition4) ? 1 : 0) + (iblockdata3.isCollisionShapeFullBlock(world, blockposition5) ? 1 : 0);
        boolean flag = iblockdata.is((Block) this) && iblockdata.getValue(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
        boolean flag1 = iblockdata2.is((Block) this) && iblockdata2.getValue(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;

        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = enumdirection.getStepX();
                int k = enumdirection.getStepZ();
                Vec3D vec3d = blockactioncontext.getClickLocation();
                double d0 = vec3d.x - (double) blockposition.getX();
                double d1 = vec3d.z - (double) blockposition.getZ();

                return (j >= 0 || d1 >= 0.5D) && (j <= 0 || d1 <= 0.5D) && (k >= 0 || d0 <= 0.5D) && (k <= 0 || d0 >= 0.5D) ? BlockPropertyDoorHinge.LEFT : BlockPropertyDoorHinge.RIGHT;
            } else {
                return BlockPropertyDoorHinge.LEFT;
            }
        } else {
            return BlockPropertyDoorHinge.RIGHT;
        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (this.material == Material.METAL) {
            return EnumInteractionResult.PASS;
        } else {
            iblockdata = (IBlockData) iblockdata.cycle(BlockDoor.OPEN);
            world.setBlock(blockposition, iblockdata, 10);
            world.levelEvent(entityhuman, (Boolean) iblockdata.getValue(BlockDoor.OPEN) ? this.getOpenSound() : this.getCloseSound(), blockposition, 0);
            world.gameEvent(entityhuman, this.isOpen(iblockdata) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    public boolean isOpen(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockDoor.OPEN);
    }

    public void setOpen(@Nullable Entity entity, World world, IBlockData iblockdata, BlockPosition blockposition, boolean flag) {
        if (iblockdata.is((Block) this) && (Boolean) iblockdata.getValue(BlockDoor.OPEN) != flag) {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockDoor.OPEN, flag), 10);
            this.playSound(world, blockposition, flag);
            world.gameEvent(entity, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
        }
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.hasNeighborSignal(blockposition) || world.hasNeighborSignal(blockposition.relative(iblockdata.getValue(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? EnumDirection.UP : EnumDirection.DOWN));

        if (!this.defaultBlockState().is(block) && flag1 != (Boolean) iblockdata.getValue(BlockDoor.POWERED)) {
            if (flag1 != (Boolean) iblockdata.getValue(BlockDoor.OPEN)) {
                this.playSound(world, blockposition, flag1);
                world.gameEvent(flag1 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
            }

            world.setBlock(blockposition, (IBlockData) ((IBlockData) iblockdata.setValue(BlockDoor.POWERED, flag1)).setValue(BlockDoor.OPEN, flag1), 2);
        }

    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return iblockdata.getValue(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? iblockdata1.isFaceSturdy(iworldreader, blockposition1, EnumDirection.UP) : iblockdata1.is((Block) this);
    }

    private void playSound(World world, BlockPosition blockposition, boolean flag) {
        world.levelEvent((EntityHuman) null, flag ? this.getOpenSound() : this.getCloseSound(), blockposition, 0);
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockDoor.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockDoor.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return enumblockmirror == EnumBlockMirror.NONE ? iblockdata : (IBlockData) iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockDoor.FACING))).cycle(BlockDoor.HINGE);
    }

    @Override
    public long getSeed(IBlockData iblockdata, BlockPosition blockposition) {
        return MathHelper.getSeed(blockposition.getX(), blockposition.below(iblockdata.getValue(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? 0 : 1).getY(), blockposition.getZ());
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockDoor.HALF, BlockDoor.FACING, BlockDoor.OPEN, BlockDoor.HINGE, BlockDoor.POWERED);
    }

    public static boolean isWoodenDoor(World world, BlockPosition blockposition) {
        return isWoodenDoor(world.getBlockState(blockposition));
    }

    public static boolean isWoodenDoor(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockDoor && (iblockdata.getMaterial() == Material.WOOD || iblockdata.getMaterial() == Material.NETHER_WOOD);
    }
}
