package net.minecraft.server;

import java.util.Random;

public class BlockNoDrop extends Block {

    public BlockNoDrop(Material material) {
        super(material);
    }

    public int a(Random random) {
        return 0;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }
}
