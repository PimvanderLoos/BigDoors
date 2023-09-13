package net.minecraft.server;

public class BlockPowered extends Block {

    public BlockPowered(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 15;
    }
}
