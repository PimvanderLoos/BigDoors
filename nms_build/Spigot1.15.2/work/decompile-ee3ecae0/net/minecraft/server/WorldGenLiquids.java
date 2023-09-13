package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class WorldGenLiquids extends WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> {

    public WorldGenLiquids(Function<Dynamic<?>, ? extends WorldGenFeatureHellFlowingLavaConfiguration> function) {
        super(function);
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureHellFlowingLavaConfiguration worldgenfeaturehellflowinglavaconfiguration) {
        if (!worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.up()).getBlock())) {
            return false;
        } else if (worldgenfeaturehellflowinglavaconfiguration.b && !worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.down()).getBlock())) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccess.getType(blockposition);

            if (!iblockdata.isAir() && !worldgenfeaturehellflowinglavaconfiguration.f.contains(iblockdata.getBlock())) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.west()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.east()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.north()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.south()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccess.getType(blockposition.down()).getBlock())) {
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

                if (generatoraccess.isEmpty(blockposition.down())) {
                    ++k;
                }

                if (j == worldgenfeaturehellflowinglavaconfiguration.c && k == worldgenfeaturehellflowinglavaconfiguration.d) {
                    generatoraccess.setTypeAndData(blockposition, worldgenfeaturehellflowinglavaconfiguration.a.getBlockData(), 2);
                    generatoraccess.getFluidTickList().a(blockposition, worldgenfeaturehellflowinglavaconfiguration.a.getType(), 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}
