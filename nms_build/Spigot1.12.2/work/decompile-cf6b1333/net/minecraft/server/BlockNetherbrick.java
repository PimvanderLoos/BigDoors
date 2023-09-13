package net.minecraft.server;

public class BlockNetherbrick extends Block {

    public BlockNetherbrick() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.L;
    }
}
