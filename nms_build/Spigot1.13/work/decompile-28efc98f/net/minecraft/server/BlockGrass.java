package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockGrass extends BlockDirtSnowSpreadable implements IBlockFragilePlantElement {

    public BlockGrass(Block.Info block_info) {
        super(block_info);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = Blocks.GRASS.getBlockData();
        int i = 0;

        while (i < 128) {
            BlockPosition blockposition2 = blockposition1;
            int j = 0;

            while (true) {
                if (j < i / 16) {
                    blockposition2 = blockposition2.a(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (world.getType(blockposition2.down()).getBlock() == this && !world.getType(blockposition2).k()) {
                        ++j;
                        continue;
                    }
                } else if (world.getType(blockposition2).isAir()) {
                    label33:
                    {
                        IBlockData iblockdata2;

                        if (random.nextInt(8) == 0) {
                            List list = world.getBiome(blockposition2).f();

                            if (list.isEmpty()) {
                                break label33;
                            }

                            iblockdata2 = ((WorldGenFeatureCompositeFlower) list.get(0)).a(random, blockposition2);
                        } else {
                            iblockdata2 = iblockdata1;
                        }

                        if (iblockdata2.canPlace(world, blockposition2)) {
                            world.setTypeAndData(blockposition2, iblockdata2, 3);
                        }
                    }
                }

                ++i;
                break;
            }
        }

    }

    public boolean f(IBlockData iblockdata) {
        return true;
    }

    public TextureType c() {
        return TextureType.CUTOUT_MIPPED;
    }
}
