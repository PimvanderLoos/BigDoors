package net.minecraft.server;

public class BlockRepeater extends BlockDiodeAbstract {

    public static final BlockStateBoolean LOCKED = BlockProperties.s;
    public static final BlockStateInteger DELAY = BlockProperties.ag;

    protected BlockRepeater(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRepeater.FACING, EnumDirection.NORTH)).set(BlockRepeater.DELAY, 1)).set(BlockRepeater.LOCKED, false)).set(BlockRepeater.c, false));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.abilities.mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRepeater.DELAY), 3);
            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    protected int h(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockRepeater.DELAY) * 2;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        return (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) blockactioncontext.getWorld(), blockactioncontext.getClickPosition(), iblockdata));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !generatoraccess.p_() && enumdirection.m() != ((EnumDirection) iblockdata.get(BlockRepeater.FACING)).m() ? (IBlockData) iblockdata.set(BlockRepeater.LOCKED, this.a((IWorldReader) generatoraccess, blockposition, iblockdata)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        return this.b(iworldreader, blockposition, iblockdata) > 0;
    }

    @Override
    protected boolean i(IBlockData iblockdata) {
        return isDiode(iblockdata);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRepeater.FACING, BlockRepeater.DELAY, BlockRepeater.LOCKED, BlockRepeater.c);
    }
}
