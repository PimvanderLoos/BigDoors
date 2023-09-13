package net.minecraft.server;

public class BlockSoulFire extends BlockFireAbstract {

    public BlockSoulFire(BlockBase.Info blockbase_info) {
        super(blockbase_info, 2.0F);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.canPlace(iblockdata, generatoraccess, blockposition) ? this.getBlockData() : Blocks.AIR.getBlockData();
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return c(iworldreader.getType(blockposition.down()).getBlock());
    }

    public static boolean c(Block block) {
        return block.a((Tag) TagsBlock.SOUL_FIRE_BASE_BLOCKS);
    }

    @Override
    protected boolean e(IBlockData iblockdata) {
        return true;
    }
}
