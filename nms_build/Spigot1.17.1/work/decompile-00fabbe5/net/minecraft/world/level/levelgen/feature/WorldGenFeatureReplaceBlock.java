package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureReplaceBlockConfiguration;

public class WorldGenFeatureReplaceBlock extends WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> {

    public WorldGenFeatureReplaceBlock(Codec<WorldGenFeatureReplaceBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureReplaceBlockConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        WorldGenFeatureReplaceBlockConfiguration worldgenfeaturereplaceblockconfiguration = (WorldGenFeatureReplaceBlockConfiguration) featureplacecontext.e();
        Iterator iterator = worldgenfeaturereplaceblockconfiguration.targetStates.iterator();

        while (iterator.hasNext()) {
            WorldGenFeatureOreConfiguration.b worldgenfeatureoreconfiguration_b = (WorldGenFeatureOreConfiguration.b) iterator.next();

            if (worldgenfeatureoreconfiguration_b.target.a(generatoraccessseed.getType(blockposition), featureplacecontext.c())) {
                generatoraccessseed.setTypeAndData(blockposition, worldgenfeatureoreconfiguration_b.state, 2);
                break;
            }
        }

        return true;
    }
}
