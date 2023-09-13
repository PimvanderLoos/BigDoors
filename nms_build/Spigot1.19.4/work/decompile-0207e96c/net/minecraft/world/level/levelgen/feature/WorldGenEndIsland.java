package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenEndIsland extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenEndIsland(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        float f = (float) randomsource.nextInt(3) + 4.0F;

        for (int i = 0; f > 0.5F; --i) {
            for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
                for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        this.setBlock(generatoraccessseed, blockposition.offset(j, i, k), Blocks.END_STONE.defaultBlockState());
                    }
                }
            }

            f -= (float) randomsource.nextInt(2) + 0.5F;
        }

        return true;
    }
}
