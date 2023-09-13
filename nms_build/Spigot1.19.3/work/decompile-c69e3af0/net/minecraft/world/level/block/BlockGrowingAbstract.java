package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockGrowingAbstract extends Block {

    protected final EnumDirection growthDirection;
    protected final boolean scheduleFluidTicks;
    protected final VoxelShape shape;

    protected BlockGrowingAbstract(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag) {
        super(blockbase_info);
        this.growthDirection = enumdirection;
        this.shape = voxelshape;
        this.scheduleFluidTicks = flag;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().relative(this.growthDirection));

        return !iblockdata.is((Block) this.getHeadBlock()) && !iblockdata.is(this.getBodyBlock()) ? this.getStateForPlacement((GeneratorAccess) blockactioncontext.getLevel()) : this.getBodyBlock().defaultBlockState();
    }

    public IBlockData getStateForPlacement(GeneratorAccess generatoraccess) {
        return this.defaultBlockState();
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.relative(this.growthDirection.getOpposite());
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return !this.canAttachTo(iblockdata1) ? false : iblockdata1.is((Block) this.getHeadBlock()) || iblockdata1.is(this.getBodyBlock()) || iblockdata1.isFaceSturdy(iworldreader, blockposition1, this.growthDirection);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!iblockdata.canSurvive(worldserver, blockposition)) {
            worldserver.destroyBlock(blockposition, true);
        }

    }

    protected boolean canAttachTo(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.shape;
    }

    protected abstract BlockGrowingTop getHeadBlock();

    protected abstract Block getBodyBlock();
}
