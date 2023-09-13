package net.minecraft.server;

public abstract class BlockGrowingAbstract extends Block {

    protected final EnumDirection a;
    protected final boolean b;
    protected final VoxelShape c;

    protected BlockGrowingAbstract(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag) {
        super(blockbase_info);
        this.a = enumdirection;
        this.c = voxelshape;
        this.b = flag;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.shift(this.a.opposite());
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        return !this.c(block) ? false : block == this.c() || block == this.d() || iblockdata1.d(iworldreader, blockposition1, this.a);
    }

    protected boolean c(Block block) {
        return true;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.c;
    }

    protected abstract BlockGrowingTop c();

    protected abstract Block d();
}
