package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.GrowingPlantConfiguration;

public class GrowingPlantFeature extends WorldGenerator<GrowingPlantConfiguration> {

    public GrowingPlantFeature(Codec<GrowingPlantConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<GrowingPlantConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        GrowingPlantConfiguration growingplantconfiguration = (GrowingPlantConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        int i = ((IntProvider) growingplantconfiguration.heightDistribution.a(random).orElseThrow(IllegalStateException::new)).a(random);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = featureplacecontext.d().i();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition_mutableblockposition.i().c(growingplantconfiguration.direction);
        IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition);

        for (int j = 1; j <= i; ++j) {
            IBlockData iblockdata1 = iblockdata;

            iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition1);
            if (iblockdata1.isAir() || growingplantconfiguration.allowWater && iblockdata1.getFluid().a((Tag) TagsFluid.WATER)) {
                if (j == i || !iblockdata.isAir()) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, growingplantconfiguration.headProvider.a(random, blockposition_mutableblockposition), 2);
                    break;
                }

                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, growingplantconfiguration.bodyProvider.a(random, blockposition_mutableblockposition), 2);
            }

            blockposition_mutableblockposition1.c(growingplantconfiguration.direction);
            blockposition_mutableblockposition.c(growingplantconfiguration.direction);
        }

        return true;
    }
}
