package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public class BlockObserver extends BlockDirectional {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;

    public BlockObserver(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockObserver.FACING, EnumDirection.SOUTH)).setValue(BlockObserver.POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockObserver.FACING, BlockObserver.POWERED);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockObserver.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockObserver.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockObserver.FACING)));
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockObserver.POWERED)) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockObserver.POWERED, false), 2);
        } else {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockObserver.POWERED, true), 2);
            worldserver.scheduleTick(blockposition, (Block) this, 2);
        }

        this.updateNeighborsInFront(worldserver, blockposition, iblockdata);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.getValue(BlockObserver.FACING) == enumdirection && !(Boolean) iblockdata.getValue(BlockObserver.POWERED)) {
            this.startSignal(generatoraccess, blockposition);
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private void startSignal(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!generatoraccess.isClientSide() && !generatoraccess.getBlockTicks().hasScheduledTick(blockposition, this)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 2);
        }

    }

    protected void updateNeighborsInFront(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockObserver.FACING);
        BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());

        world.neighborChanged(blockposition1, this, blockposition);
        world.updateNeighborsAtExceptFromFacing(blockposition1, this, enumdirection);
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.getSignal(iblockaccess, blockposition, enumdirection);
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockObserver.POWERED) && iblockdata.getValue(BlockObserver.FACING) == enumdirection ? 15 : 0;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            if (!world.isClientSide() && (Boolean) iblockdata.getValue(BlockObserver.POWERED) && !world.getBlockTicks().hasScheduledTick(blockposition, this)) {
                IBlockData iblockdata2 = (IBlockData) iblockdata.setValue(BlockObserver.POWERED, false);

                world.setBlock(blockposition, iblockdata2, 18);
                this.updateNeighborsInFront(world, blockposition, iblockdata2);
            }

        }
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            if (!world.isClientSide && (Boolean) iblockdata.getValue(BlockObserver.POWERED) && world.getBlockTicks().hasScheduledTick(blockposition, this)) {
                this.updateNeighborsInFront(world, blockposition, (IBlockData) iblockdata.setValue(BlockObserver.POWERED, false));
            }

        }
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockObserver.FACING, blockactioncontext.getNearestLookingDirection().getOpposite().getOpposite());
    }
}
