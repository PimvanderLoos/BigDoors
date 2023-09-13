package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.BlockDirtSnow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureIceSnow extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureIceSnow(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = blockposition.getX() + i;
                int l = blockposition.getZ() + j;
                int i1 = generatoraccessseed.a(HeightMap.Type.MOTION_BLOCKING, k, l);

                blockposition_mutableblockposition.d(k, i1, l);
                blockposition_mutableblockposition1.g(blockposition_mutableblockposition).c(EnumDirection.DOWN, 1);
                BiomeBase biomebase = generatoraccessseed.getBiome(blockposition_mutableblockposition);

                if (biomebase.a(generatoraccessseed, blockposition_mutableblockposition1, false)) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition1, Blocks.ICE.getBlockData(), 2);
                }

                if (biomebase.b(generatoraccessseed, blockposition_mutableblockposition)) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.SNOW.getBlockData(), 2);
                    IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition1);

                    if (iblockdata.b(BlockDirtSnow.a)) {
                        generatoraccessseed.setTypeAndData(blockposition_mutableblockposition1, (IBlockData) iblockdata.set(BlockDirtSnow.a, true), 2);
                    }
                }
            }
        }

        return true;
    }
}
