package net.minecraft.server;

import java.util.Random;

public class BlockStainedGlass extends BlockHalfTransparent {

    private final EnumColor color;

    public BlockStainedGlass(EnumColor enumcolor, Block.Info block_info) {
        super(block_info);
        this.color = enumcolor;
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public EnumColor d() {
        return this.color;
    }

    public TextureType c() {
        return TextureType.TRANSLUCENT;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    protected boolean X_() {
        return true;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (!world.isClientSide) {
                BlockBeacon.a(world, blockposition);
            }

        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            if (!world.isClientSide) {
                BlockBeacon.a(world, blockposition);
            }

        }
    }
}
