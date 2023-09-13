package net.minecraft.server;

public class BlockBloodStone extends Block {

    public BlockBloodStone() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.L;
    }
}
