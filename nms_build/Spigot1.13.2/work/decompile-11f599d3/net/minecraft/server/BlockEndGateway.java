package net.minecraft.server;

import java.util.Random;

public class BlockEndGateway extends BlockTileEntity {

    protected BlockEndGateway(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityEndGateway();
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
