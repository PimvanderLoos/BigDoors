package net.minecraft.server;

import java.util.Random;

public class BlockGlass extends BlockHalfTransparent {

    public BlockGlass(Block.Info block_info) {
        super(block_info);
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    protected boolean X_() {
        return true;
    }
}
