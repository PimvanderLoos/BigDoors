package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenFeatureSeaGrass extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    public WorldGenFeatureSeaGrass(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureConfigurationChance> featureplacecontext) {
        boolean flag = false;
        Random random = featureplacecontext.c();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance = (WorldGenFeatureConfigurationChance) featureplacecontext.e();
        int i = random.nextInt(8) - random.nextInt(8);
        int j = random.nextInt(8) - random.nextInt(8);
        int k = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + i, blockposition.getZ() + j);
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + i, k, blockposition.getZ() + j);

        if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER)) {
            boolean flag1 = random.nextDouble() < (double) worldgenfeatureconfigurationchance.probability;
            IBlockData iblockdata = flag1 ? Blocks.TALL_SEAGRASS.getBlockData() : Blocks.SEAGRASS.getBlockData();

            if (iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                if (flag1) {
                    IBlockData iblockdata1 = (IBlockData) iblockdata.set(TallSeagrassBlock.HALF, BlockPropertyDoubleBlockHalf.UPPER);
                    BlockPosition blockposition2 = blockposition1.up();

                    if (generatoraccessseed.getType(blockposition2).a(Blocks.WATER)) {
                        generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                        generatoraccessseed.setTypeAndData(blockposition2, iblockdata1, 2);
                    }
                } else {
                    generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                }

                flag = true;
            }
        }

        return flag;
    }
}
