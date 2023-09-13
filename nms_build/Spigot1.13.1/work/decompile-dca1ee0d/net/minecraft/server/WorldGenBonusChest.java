package net.minecraft.server;

import java.util.Random;

public class WorldGenBonusChest extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenBonusChest() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (IBlockData iblockdata = generatoraccess.getType(blockposition); (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) && blockposition.getY() > 1; iblockdata = generatoraccess.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        if (blockposition.getY() < 1) {
            return false;
        } else {
            blockposition = blockposition.up();

            for (int i = 0; i < 4; ++i) {
                BlockPosition blockposition1 = blockposition.a(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));

                if (generatoraccess.isEmpty(blockposition1) && generatoraccess.getType(blockposition1.down()).q()) {
                    generatoraccess.setTypeAndData(blockposition1, Blocks.CHEST.getBlockData(), 2);
                    TileEntityLootable.a(generatoraccess, random, blockposition1, LootTables.b);
                    BlockPosition blockposition2 = blockposition1.east();
                    BlockPosition blockposition3 = blockposition1.west();
                    BlockPosition blockposition4 = blockposition1.north();
                    BlockPosition blockposition5 = blockposition1.south();

                    if (generatoraccess.isEmpty(blockposition3) && generatoraccess.getType(blockposition3.down()).q()) {
                        generatoraccess.setTypeAndData(blockposition3, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (generatoraccess.isEmpty(blockposition2) && generatoraccess.getType(blockposition2.down()).q()) {
                        generatoraccess.setTypeAndData(blockposition2, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (generatoraccess.isEmpty(blockposition4) && generatoraccess.getType(blockposition4.down()).q()) {
                        generatoraccess.setTypeAndData(blockposition4, Blocks.TORCH.getBlockData(), 2);
                    }

                    if (generatoraccess.isEmpty(blockposition5) && generatoraccess.getType(blockposition5.down()).q()) {
                        generatoraccess.setTypeAndData(blockposition5, Blocks.TORCH.getBlockData(), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
