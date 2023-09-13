package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockGrowingStem extends BlockGrowingAbstract implements IBlockFragilePlantElement {

    protected BlockGrowingStem(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag) {
        super(blockbase_info, enumdirection, voxelshape, flag);
    }

    protected IBlockData updateHeadAfterConvertedFromBody(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata1;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == this.growthDirection.getOpposite() && !iblockdata.canSurvive(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
        }

        BlockGrowingTop blockgrowingtop = this.getHeadBlock();

        if (enumdirection == this.growthDirection && !iblockdata1.is((Block) this) && !iblockdata1.is((Block) blockgrowingtop)) {
            return this.updateHeadAfterConvertedFromBody(iblockdata, blockgrowingtop.getStateForPlacement(generatoraccess));
        } else {
            if (this.scheduleFluidTicks) {
                generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
            }

            return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this.getHeadBlock());
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Optional<BlockPosition> optional = this.getHeadPos(iworldreader, blockposition, iblockdata.getBlock());

        return optional.isPresent() && this.getHeadBlock().canGrowInto(iworldreader.getBlockState(((BlockPosition) optional.get()).relative(this.growthDirection)));
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        Optional<BlockPosition> optional = this.getHeadPos(worldserver, blockposition, iblockdata.getBlock());

        if (optional.isPresent()) {
            IBlockData iblockdata1 = worldserver.getBlockState((BlockPosition) optional.get());

            ((BlockGrowingTop) iblockdata1.getBlock()).performBonemeal(worldserver, randomsource, (BlockPosition) optional.get(), iblockdata1);
        }

    }

    private Optional<BlockPosition> getHeadPos(IBlockAccess iblockaccess, BlockPosition blockposition, Block block) {
        return BlockUtil.getTopConnectedBlock(iblockaccess, blockposition, block, this.growthDirection, this.getHeadBlock());
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        boolean flag = super.canBeReplaced(iblockdata, blockactioncontext);

        return flag && blockactioncontext.getItemInHand().is(this.getHeadBlock().asItem()) ? false : flag;
    }

    @Override
    protected Block getBodyBlock() {
        return this;
    }
}
