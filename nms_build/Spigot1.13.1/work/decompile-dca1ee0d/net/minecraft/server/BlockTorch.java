package net.minecraft.server;

public class BlockTorch extends Block {

    protected static final VoxelShape o = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    protected BlockTorch(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockTorch.o;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !this.canPlace(iblockdata, generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());
        Block block = iblockdata1.getBlock();
        boolean flag = block instanceof BlockFence || block instanceof BlockStainedGlass || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.MOSSY_COBBLESTONE_WALL || iblockdata1.q();

        return flag && block != Blocks.END_GATEWAY;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
