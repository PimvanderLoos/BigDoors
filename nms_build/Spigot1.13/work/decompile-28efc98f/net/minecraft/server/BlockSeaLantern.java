package net.minecraft.server;

import java.util.Random;

public class BlockSeaLantern extends Block {

    public BlockSeaLantern(Block.Info block_info) {
        super(block_info);
    }

    public int getDropCount(IBlockData iblockdata, int i, World world, BlockPosition blockposition, Random random) {
        return MathHelper.clamp(this.a(iblockdata, random) + random.nextInt(i + 1), 1, 5);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.PRISMARINE_CRYSTALS;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 2 + random.nextInt(2);
    }

    protected boolean X_() {
        return true;
    }
}
