package net.minecraft.server;

public class BlockConcretePowder extends BlockFalling {

    private final IBlockData a;

    public BlockConcretePowder(Block block, Block.Info block_info) {
        super(block_info);
        this.a = block.getBlockData();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (iblockdata1.getMaterial().isLiquid()) {
            world.setTypeAndData(blockposition, this.a, 3);
        }

    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return !blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition()).getMaterial().isLiquid() && !a((IBlockAccess) world, blockposition) ? super.getPlacedState(blockactioncontext) : this.a;
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection != EnumDirection.DOWN) {
                blockposition_mutableblockposition.g(blockposition).c(enumdirection);
                if (iblockaccess.getType(blockposition_mutableblockposition).getMaterial().isLiquid()) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return a((IBlockAccess) generatoraccess, blockposition) ? this.a : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }
}
