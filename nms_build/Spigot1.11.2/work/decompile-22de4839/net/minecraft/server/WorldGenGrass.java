package net.minecraft.server;

import java.util.Random;

public class WorldGenGrass extends WorldGenerator {

    private final IBlockData a;

    public WorldGenGrass(BlockLongGrass.EnumTallGrassType blocklonggrass_enumtallgrasstype) {
        this.a = Blocks.TALLGRASS.getBlockData().set(BlockLongGrass.TYPE, blocklonggrass_enumtallgrasstype);
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        for (IBlockData iblockdata = world.getType(blockposition); (iblockdata.getMaterial() == Material.AIR || iblockdata.getMaterial() == Material.LEAVES) && blockposition.getY() > 0; iblockdata = world.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        for (int i = 0; i < 128; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (world.isEmpty(blockposition1) && Blocks.TALLGRASS.f(world, blockposition1, this.a)) {
                world.setTypeAndData(blockposition1, this.a, 2);
            }
        }

        return true;
    }
}
