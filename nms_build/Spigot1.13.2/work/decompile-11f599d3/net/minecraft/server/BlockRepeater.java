package net.minecraft.server;

public class BlockRepeater extends BlockDiodeAbstract {

    public static final BlockStateBoolean LOCKED = BlockProperties.p;
    public static final BlockStateInteger DELAY = BlockProperties.aa;

    protected BlockRepeater(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRepeater.FACING, EnumDirection.NORTH)).set(BlockRepeater.DELAY, 1)).set(BlockRepeater.LOCKED, false)).set(BlockRepeater.c, false));
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.abilities.mayBuild) {
            return false;
        } else {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRepeater.DELAY), 3);
            return true;
        }
    }

    protected int k(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockRepeater.DELAY) * 2;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        return (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) blockactioncontext.getWorld(), blockactioncontext.getClickPosition(), iblockdata));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !generatoraccess.e() && enumdirection.k() != ((EnumDirection) iblockdata.get(BlockRepeater.FACING)).k() ? (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) generatoraccess, blockposition, iblockdata)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        return this.b(iworldreader, blockposition, iblockdata) > 0;
    }

    protected boolean w(IBlockData iblockdata) {
        return isDiode(iblockdata);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRepeater.FACING, BlockRepeater.DELAY, BlockRepeater.LOCKED, BlockRepeater.c);
    }
}
