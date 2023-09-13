package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureFillConfiguration;

public class WorldGenFeatureFill extends WorldGenerator<WorldGenFeatureFillConfiguration> {

    public WorldGenFeatureFill(Codec<WorldGenFeatureFillConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureFillConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.d();
        WorldGenFeatureFillConfiguration worldgenfeaturefillconfiguration = (WorldGenFeatureFillConfiguration) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = blockposition.getX() + i;
                int l = blockposition.getZ() + j;
                int i1 = generatoraccessseed.getMinBuildHeight() + worldgenfeaturefillconfiguration.height;

                blockposition_mutableblockposition.d(k, i1, l);
                if (generatoraccessseed.getType(blockposition_mutableblockposition).isAir()) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, worldgenfeaturefillconfiguration.state, 2);
                }
            }
        }

        return true;
    }
}
