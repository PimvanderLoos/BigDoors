package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockAir extends Block {

    protected BlockAir() {
        super(Material.AIR);
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockAir.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return false;
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {}

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
