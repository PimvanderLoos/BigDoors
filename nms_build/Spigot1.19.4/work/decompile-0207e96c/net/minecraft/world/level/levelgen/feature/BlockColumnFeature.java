package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;

public class BlockColumnFeature extends WorldGenerator<BlockColumnConfiguration> {

    public BlockColumnFeature(Codec<BlockColumnConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockColumnConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockColumnConfiguration blockcolumnconfiguration = (BlockColumnConfiguration) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();
        int i = blockcolumnconfiguration.layers().size();
        int[] aint = new int[i];
        int j = 0;

        for (int k = 0; k < i; ++k) {
            aint[k] = ((BlockColumnConfiguration.a) blockcolumnconfiguration.layers().get(k)).height().sample(randomsource);
            j += aint[k];
        }

        if (j == 0) {
            return false;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = featureplacecontext.origin().mutable();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition_mutableblockposition.mutable().move(blockcolumnconfiguration.direction());

            int l;

            for (l = 0; l < j; ++l) {
                if (!blockcolumnconfiguration.allowedPlacement().test(generatoraccessseed, blockposition_mutableblockposition1)) {
                    truncate(aint, j, l, blockcolumnconfiguration.prioritizeTip());
                    break;
                }

                blockposition_mutableblockposition1.move(blockcolumnconfiguration.direction());
            }

            for (l = 0; l < i; ++l) {
                int i1 = aint[l];

                if (i1 != 0) {
                    BlockColumnConfiguration.a blockcolumnconfiguration_a = (BlockColumnConfiguration.a) blockcolumnconfiguration.layers().get(l);

                    for (int j1 = 0; j1 < i1; ++j1) {
                        generatoraccessseed.setBlock(blockposition_mutableblockposition, blockcolumnconfiguration_a.state().getState(randomsource, blockposition_mutableblockposition), 2);
                        blockposition_mutableblockposition.move(blockcolumnconfiguration.direction());
                    }
                }
            }

            return true;
        }
    }

    private static void truncate(int[] aint, int i, int j, boolean flag) {
        int k = i - j;
        int l = flag ? 1 : -1;
        int i1 = flag ? 0 : aint.length - 1;
        int j1 = flag ? aint.length : -1;

        for (int k1 = i1; k1 != j1 && k > 0; k1 += l) {
            int l1 = aint[k1];
            int i2 = Math.min(l1, k);

            k -= i2;
            aint[k1] -= i2;
        }

    }
}
