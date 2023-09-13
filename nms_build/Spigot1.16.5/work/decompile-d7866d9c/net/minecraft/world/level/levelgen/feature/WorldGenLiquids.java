package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;

public class WorldGenLiquids extends WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> {

    public WorldGenLiquids(Codec<WorldGenFeatureHellFlowingLavaConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureHellFlowingLavaConfiguration worldgenfeaturehellflowinglavaconfiguration) {
        if (!worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.up()).getBlock())) {
            return false;
        } else if (worldgenfeaturehellflowinglavaconfiguration.c && !worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.down()).getBlock())) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition);

            if (!iblockdata.isAir() && !worldgenfeaturehellflowinglavaconfiguration.f.contains(iblockdata.getBlock())) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.west()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.east()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.north()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.south()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.f.contains(generatoraccessseed.getType(blockposition.down()).getBlock())) {
                    ++j;
                }

                int k = 0;

                if (generatoraccessseed.isEmpty(blockposition.west())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.east())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.north())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.south())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.down())) {
                    ++k;
                }

                if (j == worldgenfeaturehellflowinglavaconfiguration.d && k == worldgenfeaturehellflowinglavaconfiguration.e) {
                    generatoraccessseed.setTypeAndData(blockposition, worldgenfeaturehellflowinglavaconfiguration.b.getBlockData(), 2);
                    generatoraccessseed.getFluidTickList().a(blockposition, worldgenfeaturehellflowinglavaconfiguration.b.getType(), 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}
