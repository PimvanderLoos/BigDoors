package net.minecraft.server;

public class BlockStainedHardenedClay extends BlockCloth {

    private static final MaterialMapColor[] b = new MaterialMapColor[] { MaterialMapColor.M, MaterialMapColor.N, MaterialMapColor.O, MaterialMapColor.P, MaterialMapColor.Q, MaterialMapColor.R, MaterialMapColor.S, MaterialMapColor.T, MaterialMapColor.U, MaterialMapColor.V, MaterialMapColor.W, MaterialMapColor.X, MaterialMapColor.Y, MaterialMapColor.Z, MaterialMapColor.aa, MaterialMapColor.ab};

    public BlockStainedHardenedClay() {
        super(Material.STONE);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockStainedHardenedClay.b[((EnumColor) iblockdata.get(BlockStainedHardenedClay.COLOR)).getColorIndex()];
    }
}
