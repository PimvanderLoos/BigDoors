package net.minecraft.server;

public class BlockDirtSnow extends Block {

    public static final BlockStateBoolean a = BlockProperties.v;

    protected BlockDirtSnow(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockDirtSnow.a, false));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection != EnumDirection.UP) {
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            Block block = iblockdata1.getBlock();

            return (IBlockData) iblockdata.set(BlockDirtSnow.a, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW);
        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Block block = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().up()).getBlock();

        return (IBlockData) this.getBlockData().set(BlockDirtSnow.a, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDirtSnow.a);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.DIRT;
    }
}
