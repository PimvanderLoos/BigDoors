package net.minecraft.server;

public class BlockGlazedTerracotta extends BlockFacingHorizontal {

    public BlockGlazedTerracotta(Block.Info block_info) {
        super(block_info);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockGlazedTerracotta.FACING);
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockGlazedTerracotta.FACING, blockactioncontext.f().opposite());
    }

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.PUSH_ONLY;
    }
}
