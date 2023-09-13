package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;

public class WorldGenFeatureBlockPile extends WorldGenerator<WorldGenFeatureBlockPileConfiguration> {

    public WorldGenFeatureBlockPile(Codec<WorldGenFeatureBlockPileConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration) {
        if (blockposition.getY() < 5) {
            return false;
        } else {
            int i = 2 + random.nextInt(2);
            int j = 2 + random.nextInt(2);
            Iterator iterator = BlockPosition.a(blockposition.b(-i, 0, -j), blockposition.b(i, 1, j)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int k = blockposition.getX() - blockposition1.getX();
                int l = blockposition.getZ() - blockposition1.getZ();

                if ((float) (k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
                    this.a(generatoraccessseed, blockposition1, random, worldgenfeatureblockpileconfiguration);
                } else if ((double) random.nextFloat() < 0.031D) {
                    this.a(generatoraccessseed, blockposition1, random, worldgenfeatureblockpileconfiguration);
                }
            }

            return true;
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = generatoraccess.getType(blockposition1);

        return iblockdata.a(Blocks.GRASS_PATH) ? random.nextBoolean() : iblockdata.d(generatoraccess, blockposition1, EnumDirection.UP);
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration) {
        if (generatoraccess.isEmpty(blockposition) && this.a(generatoraccess, blockposition, random)) {
            generatoraccess.setTypeAndData(blockposition, worldgenfeatureblockpileconfiguration.b.a(random, blockposition), 4);
        }

    }
}
