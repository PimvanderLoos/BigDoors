package net.minecraft.server;

public class BlockGravel extends BlockFalling {

    public BlockGravel(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        if (i > 3) {
            i = 3;
        }

        return (IMaterial) (world.random.nextInt(10 - i * 3) == 0 ? Items.FLINT : super.getDropType(iblockdata, world, blockposition, i));
    }
}
