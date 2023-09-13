package net.minecraft.server;

public class BlockGrassPath extends Block {

    protected static final VoxelShape a = BlockSoil.b;

    protected BlockGrassPath(Block.Info block_info) {
        super(block_info);
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.J();
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return !this.getBlockData().canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition()) ? Block.a(this.getBlockData(), Blocks.DIRT.getBlockData(), (GeneratorAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition()) : super.getPlacedState(blockactioncontext);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.UP && !iblockdata.canPlace(generatoraccess, blockposition) ? Block.a(iblockdata, Blocks.DIRT.getBlockData(), generatoraccess, blockposition) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.up());

        return !iblockdata1.getMaterial().isBuildable() || iblockdata1.getBlock() instanceof BlockFenceGate;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockGrassPath.a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.DIRT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
