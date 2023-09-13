package net.minecraft.server;

public class BlockHalfTransparent extends Block {

    private final boolean a;

    protected BlockHalfTransparent(Material material, boolean flag) {
        this(material, flag, material.r());
    }

    protected BlockHalfTransparent(Material material, boolean flag, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
        this.a = flag;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }
}
