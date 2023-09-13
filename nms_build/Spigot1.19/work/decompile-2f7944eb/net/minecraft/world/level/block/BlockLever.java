package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockLever extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    protected static final int DEPTH = 6;
    protected static final int WIDTH = 6;
    protected static final int HEIGHT = 8;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.box(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
    protected static final VoxelShape UP_AABB_Z = Block.box(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
    protected static final VoxelShape UP_AABB_X = Block.box(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
    protected static final VoxelShape DOWN_AABB_Z = Block.box(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
    protected static final VoxelShape DOWN_AABB_X = Block.box(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

    protected BlockLever(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockLever.FACING, EnumDirection.NORTH)).setValue(BlockLever.POWERED, false)).setValue(BlockLever.FACE, BlockPropertyAttachPosition.WALL));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((BlockPropertyAttachPosition) iblockdata.getValue(BlockLever.FACE)) {
            case FLOOR:
                switch (((EnumDirection) iblockdata.getValue(BlockLever.FACING)).getAxis()) {
                    case X:
                        return BlockLever.UP_AABB_X;
                    case Z:
                    default:
                        return BlockLever.UP_AABB_Z;
                }
            case WALL:
                switch ((EnumDirection) iblockdata.getValue(BlockLever.FACING)) {
                    case EAST:
                        return BlockLever.EAST_AABB;
                    case WEST:
                        return BlockLever.WEST_AABB;
                    case SOUTH:
                        return BlockLever.SOUTH_AABB;
                    case NORTH:
                    default:
                        return BlockLever.NORTH_AABB;
                }
            case CEILING:
            default:
                switch (((EnumDirection) iblockdata.getValue(BlockLever.FACING)).getAxis()) {
                    case X:
                        return BlockLever.DOWN_AABB_X;
                    case Z:
                    default:
                        return BlockLever.DOWN_AABB_Z;
                }
        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        IBlockData iblockdata1;

        if (world.isClientSide) {
            iblockdata1 = (IBlockData) iblockdata.cycle(BlockLever.POWERED);
            if ((Boolean) iblockdata1.getValue(BlockLever.POWERED)) {
                makeParticle(iblockdata1, world, blockposition, 1.0F);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            iblockdata1 = this.pull(iblockdata, world, blockposition);
            float f = (Boolean) iblockdata1.getValue(BlockLever.POWERED) ? 0.6F : 0.5F;

            world.playSound((EntityHuman) null, blockposition, SoundEffects.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            world.gameEvent((Entity) entityhuman, (Boolean) iblockdata1.getValue(BlockLever.POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, blockposition);
            return EnumInteractionResult.CONSUME;
        }
    }

    public IBlockData pull(IBlockData iblockdata, World world, BlockPosition blockposition) {
        iblockdata = (IBlockData) iblockdata.cycle(BlockLever.POWERED);
        world.setBlock(blockposition, iblockdata, 3);
        this.updateNeighbours(iblockdata, world, blockposition);
        return iblockdata;
    }

    private static void makeParticle(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, float f) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.getValue(BlockLever.FACING)).getOpposite();
        EnumDirection enumdirection1 = getConnectedDirection(iblockdata).getOpposite();
        double d0 = (double) blockposition.getX() + 0.5D + 0.1D * (double) enumdirection.getStepX() + 0.2D * (double) enumdirection1.getStepX();
        double d1 = (double) blockposition.getY() + 0.5D + 0.1D * (double) enumdirection.getStepY() + 0.2D * (double) enumdirection1.getStepY();
        double d2 = (double) blockposition.getZ() + 0.5D + 0.1D * (double) enumdirection.getStepZ() + 0.2D * (double) enumdirection1.getStepZ();

        generatoraccess.addParticle(new ParticleParamRedstone(ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, f), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockLever.POWERED) && randomsource.nextFloat() < 0.25F) {
            makeParticle(iblockdata, world, blockposition, 0.5F);
        }

    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.getValue(BlockLever.POWERED)) {
                this.updateNeighbours(iblockdata, world, blockposition);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockLever.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockLever.POWERED) && getConnectedDirection(iblockdata) == enumdirection ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    private void updateNeighbours(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.updateNeighborsAt(blockposition, this);
        world.updateNeighborsAt(blockposition.relative(getConnectedDirection(iblockdata).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockLever.FACE, BlockLever.FACING, BlockLever.POWERED);
    }
}
