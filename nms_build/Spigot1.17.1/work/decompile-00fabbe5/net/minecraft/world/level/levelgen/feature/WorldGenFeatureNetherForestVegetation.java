package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;

public class WorldGenFeatureNetherForestVegetation extends WorldGenerator<WorldGenFeatureBlockPileConfiguration> {

    public WorldGenFeatureNetherForestVegetation(Codec<WorldGenFeatureBlockPileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureBlockPileConfiguration> featureplacecontext) {
        return a(featureplacecontext.a(), featureplacecontext.c(), featureplacecontext.d(), (WorldGenFeatureBlockPileConfiguration) featureplacecontext.e(), 8, 4);
    }

    public static boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration, int i, int j) {
        IBlockData iblockdata = generatoraccess.getType(blockposition.down());

        if (!iblockdata.a((Tag) TagsBlock.NYLIUM)) {
            return false;
        } else {
            int k = blockposition.getY();

            if (k >= generatoraccess.getMinBuildHeight() + 1 && k + 1 < generatoraccess.getMaxBuildHeight()) {
                int l = 0;

                for (int i1 = 0; i1 < i * i; ++i1) {
                    BlockPosition blockposition1 = blockposition.c(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
                    IBlockData iblockdata1 = worldgenfeatureblockpileconfiguration.stateProvider.a(random, blockposition1);

                    if (generatoraccess.isEmpty(blockposition1) && blockposition1.getY() > generatoraccess.getMinBuildHeight() && iblockdata1.canPlace(generatoraccess, blockposition1)) {
                        generatoraccess.setTypeAndData(blockposition1, iblockdata1, 2);
                        ++l;
                    }
                }

                return l > 0;
            } else {
                return false;
            }
        }
    }
}
