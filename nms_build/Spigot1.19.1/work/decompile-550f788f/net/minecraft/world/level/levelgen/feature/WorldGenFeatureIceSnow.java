package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.BlockDirtSnow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureIceSnow extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureIceSnow(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = blockposition.getX() + i;
                int l = blockposition.getZ() + j;
                int i1 = generatoraccessseed.getHeight(HeightMap.Type.MOTION_BLOCKING, k, l);

                blockposition_mutableblockposition.set(k, i1, l);
                blockposition_mutableblockposition1.set(blockposition_mutableblockposition).move(EnumDirection.DOWN, 1);
                BiomeBase biomebase = (BiomeBase) generatoraccessseed.getBiome(blockposition_mutableblockposition).value();

                if (biomebase.shouldFreeze(generatoraccessseed, blockposition_mutableblockposition1, false)) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition1, Blocks.ICE.defaultBlockState(), 2);
                }

                if (biomebase.shouldSnow(generatoraccessseed, blockposition_mutableblockposition)) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.SNOW.defaultBlockState(), 2);
                    IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition1);

                    if (iblockdata.hasProperty(BlockDirtSnow.SNOWY)) {
                        generatoraccessseed.setBlock(blockposition_mutableblockposition1, (IBlockData) iblockdata.setValue(BlockDirtSnow.SNOWY, true), 2);
                    }
                }
            }
        }

        return true;
    }
}
