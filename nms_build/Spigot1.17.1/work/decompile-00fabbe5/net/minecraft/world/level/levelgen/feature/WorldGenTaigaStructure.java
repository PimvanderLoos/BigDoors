package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;

public class WorldGenTaigaStructure extends WorldGenerator<WorldGenFeatureLakeConfiguration> {

    public WorldGenTaigaStructure(Codec<WorldGenFeatureLakeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureLakeConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();

        WorldGenFeatureLakeConfiguration worldgenfeaturelakeconfiguration;

        for (worldgenfeaturelakeconfiguration = (WorldGenFeatureLakeConfiguration) featureplacecontext.e(); blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 3; blockposition = blockposition.down()) {
            if (!generatoraccessseed.isEmpty(blockposition.down())) {
                IBlockData iblockdata = generatoraccessseed.getType(blockposition.down());

                if (b(iblockdata) || a(iblockdata)) {
                    break;
                }
            }
        }

        if (blockposition.getY() <= generatoraccessseed.getMinBuildHeight() + 3) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                int j = random.nextInt(2);
                int k = random.nextInt(2);
                int l = random.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                Iterator iterator = BlockPosition.a(blockposition.c(-j, -k, -l), blockposition.c(j, k, l)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();

                    if (blockposition1.j(blockposition) <= (double) (f * f)) {
                        generatoraccessseed.setTypeAndData(blockposition1, worldgenfeaturelakeconfiguration.state, 4);
                    }
                }

                blockposition = blockposition.c(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
            }

            return true;
        }
    }
}
