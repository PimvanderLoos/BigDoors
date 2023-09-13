package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockRedstoneTorchWall extends BlockRedstoneTorch {

    public static final BlockStateDirection b = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean c = BlockRedstoneTorch.LIT;

    protected BlockRedstoneTorchWall(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneTorchWall.b, EnumDirection.NORTH)).set(BlockRedstoneTorchWall.c, true));
    }

    public String m() {
        return this.getItem().getName();
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Blocks.WALL_TORCH.a(iblockdata, iblockaccess, blockposition);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Blocks.WALL_TORCH.canPlace(iblockdata, iworldreader, blockposition);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return Blocks.WALL_TORCH.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = Blocks.WALL_TORCH.getPlacedState(blockactioncontext);

        return iblockdata == null ? null : (IBlockData) this.getBlockData().set(BlockRedstoneTorchWall.b, iblockdata.get(BlockRedstoneTorchWall.b));
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockRedstoneTorchWall.b)).opposite();

        return world.isBlockFacePowered(blockposition.shift(enumdirection), enumdirection);
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockRedstoneTorchWall.c) && iblockdata.get(BlockRedstoneTorchWall.b) != enumdirection ? 15 : 0;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return Blocks.WALL_TORCH.a(iblockdata, enumblockrotation);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return Blocks.WALL_TORCH.a(iblockdata, enumblockmirror);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneTorchWall.b, BlockRedstoneTorchWall.c);
    }
}
