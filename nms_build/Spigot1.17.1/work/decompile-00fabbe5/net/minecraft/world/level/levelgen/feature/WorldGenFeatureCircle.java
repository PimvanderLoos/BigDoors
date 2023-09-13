package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class WorldGenFeatureCircle extends WorldGenFeatureDisk {

    public WorldGenFeatureCircle(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureCircleConfiguration> featureplacecontext) {
        return !featureplacecontext.a().getFluid(featureplacecontext.d()).a((Tag) TagsFluid.WATER) ? false : super.generate(featureplacecontext);
    }
}
