package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenEndIsland extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenEndIsland(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        BlockPosition blockposition = featureplacecontext.d();
        float f = (float) (random.nextInt(3) + 4);

        for (int i = 0; f > 0.5F; --i) {
            for (int j = MathHelper.d(-f); j <= MathHelper.f(f); ++j) {
                for (int k = MathHelper.d(-f); k <= MathHelper.f(f); ++k) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        this.a((IWorldWriter) generatoraccessseed, blockposition.c(j, i, k), Blocks.END_STONE.getBlockData());
                    }
                }
            }

            f = (float) ((double) f - ((double) random.nextInt(2) + 0.5D));
        }

        return true;
    }
}
