package net.minecraft.server;

import java.util.Random;

public class BlockObserver extends BlockDirectional {

    public static final BlockStateBoolean b = BlockProperties.t;

    public BlockObserver(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockObserver.FACING, EnumDirection.SOUTH)).set(BlockObserver.b, Boolean.valueOf(false)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockObserver.FACING, BlockObserver.b});
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockObserver.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (((Boolean) iblockdata.get(BlockObserver.b)).booleanValue()) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockObserver.b, Boolean.valueOf(false)), 2);
        } else {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockObserver.b, Boolean.valueOf(true)), 2);
            world.I().a(blockposition, this, 2);
        }

        this.a(world, blockposition, iblockdata);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.get(BlockObserver.FACING) == enumdirection) {
            if (!((Boolean) iblockdata.get(BlockObserver.b)).booleanValue()) {
                this.a(generatoraccess, blockposition);
            } else if (!generatoraccess.e() && !generatoraccess.I().a(blockposition, this)) {
                return (IBlockData) iblockdata.set(BlockObserver.b, Boolean.valueOf(false));
            }
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!generatoraccess.e() && !generatoraccess.I().a(blockposition, this)) {
            generatoraccess.I().a(blockposition, this, 2);
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockObserver.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        world.a(blockposition1, (Block) this, blockposition);
        world.a(blockposition1, (Block) this, enumdirection);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockObserver.b)).booleanValue() && iblockdata.get(BlockObserver.FACING) == enumdirection ? 15 : 0;
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && iblockdata.getBlock() != iblockdata1.getBlock()) {
            if (!world.isClientSide && ((Boolean) iblockdata.get(BlockObserver.b)).booleanValue() && world.I().a(blockposition, this)) {
                this.a(world, blockposition, (IBlockData) iblockdata.set(BlockObserver.b, Boolean.valueOf(false)));
            }

        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockObserver.FACING, blockactioncontext.d().opposite().opposite());
    }
}
