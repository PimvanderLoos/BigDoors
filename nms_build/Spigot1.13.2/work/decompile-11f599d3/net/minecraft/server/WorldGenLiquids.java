package net.minecraft.server;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator<WorldGenFeatureFlowingConfiguration> {

    public WorldGenLiquids() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureFlowingConfiguration worldgenfeatureflowingconfiguration) {
        if (!Block.c(generatoraccess.getType(blockposition.up()).getBlock())) {
            return false;
        } else if (!Block.c(generatoraccess.getType(blockposition.down()).getBlock())) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccess.getType(blockposition);

            if (!iblockdata.isAir() && !Block.c(iblockdata.getBlock())) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (Block.c(generatoraccess.getType(blockposition.west()).getBlock())) {
                    ++j;
                }

                if (Block.c(generatoraccess.getType(blockposition.east()).getBlock())) {
                    ++j;
                }

                if (Block.c(generatoraccess.getType(blockposition.north()).getBlock())) {
                    ++j;
                }

                if (Block.c(generatoraccess.getType(blockposition.south()).getBlock())) {
                    ++j;
                }

                int k = 0;

                if (generatoraccess.isEmpty(blockposition.west())) {
                    ++k;
                }

                if (generatoraccess.isEmpty(blockposition.east())) {
                    ++k;
                }

                if (generatoraccess.isEmpty(blockposition.north())) {
                    ++k;
                }

                if (generatoraccess.isEmpty(blockposition.south())) {
                    ++k;
                }

                if (j == 3 && k == 1) {
                    generatoraccess.setTypeAndData(blockposition, worldgenfeatureflowingconfiguration.a.i().i(), 2);
                    generatoraccess.getFluidTickList().a(blockposition, worldgenfeatureflowingconfiguration.a, 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}
