package net.minecraft.server;

import java.util.Random;

public class BlockGlass extends BlockHalfTransparent {

    public BlockGlass(Material material, boolean flag) {
        super(material, flag);
        this.a(CreativeModeTab.b);
    }

    public int a(Random random) {
        return 0;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    protected boolean n() {
        return true;
    }
}
