package net.minecraft.server;

import java.util.Random;

public class WorldGenGroundBush extends WorldGenTrees {

    private final IBlockData a;
    private final IBlockData b;

    public WorldGenGroundBush(IBlockData iblockdata, IBlockData iblockdata1) {
        super(false);
        this.b = iblockdata;
        this.a = iblockdata1;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        for (IBlockData iblockdata = world.getType(blockposition); (iblockdata.getMaterial() == Material.AIR || iblockdata.getMaterial() == Material.LEAVES) && blockposition.getY() > 0; iblockdata = world.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        Block block = world.getType(blockposition).getBlock();

        if (block == Blocks.DIRT || block == Blocks.GRASS) {
            blockposition = blockposition.up();
            this.a(world, blockposition, this.b);

            for (int i = blockposition.getY(); i <= blockposition.getY() + 2; ++i) {
                int j = i - blockposition.getY();
                int k = 2 - j;

                for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                    int i1 = l - blockposition.getX();

                    for (int j1 = blockposition.getZ() - k; j1 <= blockposition.getZ() + k; ++j1) {
                        int k1 = j1 - blockposition.getZ();

                        if (Math.abs(i1) != k || Math.abs(k1) != k || random.nextInt(2) != 0) {
                            BlockPosition blockposition1 = new BlockPosition(l, i, j1);
                            Material material = world.getType(blockposition1).getMaterial();

                            if (material == Material.AIR || material == Material.LEAVES) {
                                this.a(world, blockposition1, this.a);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
