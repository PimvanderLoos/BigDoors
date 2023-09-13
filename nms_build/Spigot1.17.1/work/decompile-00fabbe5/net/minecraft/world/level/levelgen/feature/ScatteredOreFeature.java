package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;

public class ScatteredOreFeature extends WorldGenerator<WorldGenFeatureOreConfiguration> {

    private static final int MAX_DIST_FROM_ORIGIN = 7;

    ScatteredOreFeature(Codec<WorldGenFeatureOreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureOreConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        WorldGenFeatureOreConfiguration worldgenfeatureoreconfiguration = (WorldGenFeatureOreConfiguration) featureplacecontext.e();
        BlockPosition blockposition = featureplacecontext.d();
        int i = random.nextInt(worldgenfeatureoreconfiguration.size + 1);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int j = 0;

        while (j < i) {
            this.a(blockposition_mutableblockposition, random, blockposition, Math.min(j, 7));
            IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition);
            Iterator iterator = worldgenfeatureoreconfiguration.targetStates.iterator();

            while (true) {
                if (iterator.hasNext()) {
                    WorldGenFeatureOreConfiguration.b worldgenfeatureoreconfiguration_b = (WorldGenFeatureOreConfiguration.b) iterator.next();

                    Objects.requireNonNull(generatoraccessseed);
                    if (!WorldGenMinable.a(iblockdata, generatoraccessseed::getType, random, worldgenfeatureoreconfiguration, worldgenfeatureoreconfiguration_b, blockposition_mutableblockposition)) {
                        continue;
                    }

                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, worldgenfeatureoreconfiguration_b.state, 2);
                }

                ++j;
                break;
            }
        }

        return true;
    }

    private void a(BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Random random, BlockPosition blockposition, int i) {
        int j = this.a(random, i);
        int k = this.a(random, i);
        int l = this.a(random, i);

        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, j, k, l);
    }

    private int a(Random random, int i) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) i);
    }
}
