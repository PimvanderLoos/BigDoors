package net.minecraft.server;

public class BlockDoubleStep extends BlockDoubleStepAbstract {

    public BlockDoubleStep() {}

    public boolean e() {
        return true;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.SOLID;
    }
}
