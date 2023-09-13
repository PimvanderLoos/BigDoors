package net.minecraft.server;

public class BlockSkull extends BlockSkullAbstract {

    public static final BlockStateInteger a = BlockProperties.an;
    protected static final VoxelShape b = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

    protected BlockSkull(BlockSkull.a blockskull_a, Block.Info block_info) {
        super(blockskull_a, block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSkull.a, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSkull.b;
    }

    public VoxelShape g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockSkull.a, MathHelper.floor((double) (blockactioncontext.h() * 16.0F / 360.0F) + 0.5D) & 15);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockSkull.a, enumblockrotation.a((Integer) iblockdata.get(BlockSkull.a), 16));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockSkull.a, enumblockmirror.a((Integer) iblockdata.get(BlockSkull.a), 16));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSkull.a);
    }

    public static enum Type implements BlockSkull.a {

        SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, DRAGON;

        private Type() {}
    }

    public interface a {}
}
