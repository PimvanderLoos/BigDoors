package net.minecraft.server;

import java.util.Random;

public class WorldGenBonusChest extends WorldGenerator {

    public WorldGenBonusChest() {}

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        for (IBlockData iblockdata = world.getType(blockposition); (iblockdata.getMaterial() == Material.AIR || iblockdata.getMaterial() == Material.LEAVES) && blockposition.getY() > 1; iblockdata = world.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        if (blockposition.getY() < 1) {
            return false;
        } else {
            blockposition = blockposition.up();

            for (int i = 0; i < 4; ++i) {
                BlockPosition blockposition1 = blockposition.a(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));

                if (world.isEmpty(blockposition1) && world.getType(blockposition1.down()).q()) {
                    world.setTypeAndData(blockposition1, Blocks.CHEST.getBlockData(), 2);
                    TileEntity tileentity = world.getTileEntity(blockposition1);

                    if (tileentity instanceof TileEntityChest) {
                        ((TileEntityChest) tileentity).a(LootTables.b, random.nextLong());
                    }

                    BlockPosition blockposition2 = blockposition1.east();
                    BlockPosition blockposition3 = blockposition1.west();
                    BlockPosition blockposition4 = blockposition1.north();
                    BlockPosition blockposition5 = blockposition1.south();

                    if (world.isEmpty(blockposition3) && world.getType(blockposition3.down()).q()) {
                        world.setTypeAndData(blockposition3, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (world.isEmpty(blockposition2) && world.getType(blockposition2.down()).q()) {
                        world.setTypeAndData(blockposition2, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (world.isEmpty(blockposition4) && world.getType(blockposition4.down()).q()) {
                        world.setTypeAndData(blockposition4, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (world.isEmpty(blockposition5) && world.getType(blockposition5.down()).q()) {
                        world.setTypeAndData(blockposition5, Blocks.TORCH.getBlockData(), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
