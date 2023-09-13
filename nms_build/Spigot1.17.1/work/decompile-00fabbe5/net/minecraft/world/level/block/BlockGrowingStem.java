package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockGrowingStem extends BlockGrowingAbstract implements IBlockFragilePlantElement {

    protected BlockGrowingStem(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag) {
        super(blockbase_info, enumdirection, voxelshape, flag);
    }

    protected IBlockData a(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata1;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == this.growthDirection.opposite() && !iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        BlockGrowingTop blockgrowingtop = this.d();

        if (enumdirection == this.growthDirection && !iblockdata1.a((Block) this) && !iblockdata1.a((Block) blockgrowingtop)) {
            return this.a(iblockdata, blockgrowingtop.a(generatoraccess));
        } else {
            if (this.scheduleFluidTicks) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this.d());
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Optional<BlockPosition> optional = this.a(iblockaccess, blockposition, iblockdata.getBlock());

        return optional.isPresent() && this.d().g(iblockaccess.getType(((BlockPosition) optional.get()).shift(this.growthDirection)));
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        Optional<BlockPosition> optional = this.a((IBlockAccess) worldserver, blockposition, iblockdata.getBlock());

        if (optional.isPresent()) {
            IBlockData iblockdata1 = worldserver.getType((BlockPosition) optional.get());

            ((BlockGrowingTop) iblockdata1.getBlock()).a(worldserver, random, (BlockPosition) optional.get(), iblockdata1);
        }

    }

    private Optional<BlockPosition> a(IBlockAccess iblockaccess, BlockPosition blockposition, Block block) {
        return BlockUtil.a(iblockaccess, blockposition, block, this.growthDirection, this.d());
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        boolean flag = super.a(iblockdata, blockactioncontext);

        return flag && blockactioncontext.getItemStack().a(this.d().getItem()) ? false : flag;
    }

    @Override
    protected Block c() {
        return this;
    }
}
