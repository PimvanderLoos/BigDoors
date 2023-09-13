package net.minecraft.server;

public class BlockBarrier extends Block {

    protected BlockBarrier() {
        super(Material.BANNER);
        this.j();
        this.b(6000001.0F);
        this.p();
        this.n = true;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {}
}
