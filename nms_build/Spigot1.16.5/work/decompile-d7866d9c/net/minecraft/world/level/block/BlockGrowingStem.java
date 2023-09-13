package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
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

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == this.a.opposite() && !iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        BlockGrowingTop blockgrowingtop = this.c();

        if (enumdirection == this.a) {
            Block block = iblockdata1.getBlock();

            if (block != this && block != blockgrowingtop) {
                return blockgrowingtop.a(generatoraccess);
            }
        }

        if (this.b) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Optional<BlockPosition> optional = this.b(iblockaccess, blockposition, iblockdata);

        return optional.isPresent() && this.c().h(iblockaccess.getType(((BlockPosition) optional.get()).shift(this.a)));
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        Optional<BlockPosition> optional = this.b((IBlockAccess) worldserver, blockposition, iblockdata);

        if (optional.isPresent()) {
            IBlockData iblockdata1 = worldserver.getType((BlockPosition) optional.get());

            ((BlockGrowingTop) iblockdata1.getBlock()).a(worldserver, random, (BlockPosition) optional.get(), iblockdata1);
        }

    }

    private Optional<BlockPosition> b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition;

        Block block;

        do {
            blockposition1 = blockposition1.shift(this.a);
            block = iblockaccess.getType(blockposition1).getBlock();
        } while (block == iblockdata.getBlock());

        return block == this.c() ? Optional.of(blockposition1) : Optional.empty();
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        boolean flag = super.a(iblockdata, blockactioncontext);

        return flag && blockactioncontext.getItemStack().getItem() == this.c().getItem() ? false : flag;
    }

    @Override
    protected Block d() {
        return this;
    }
}
