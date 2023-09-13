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
    public boolean place(FeaturePlaceContext<WorldGenFeatureReplaceBlockConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        WorldGenFeatureReplaceBlockConfiguration worldgenfeaturereplaceblockconfiguration = (WorldGenFeatureReplaceBlockConfiguration) featureplacecontext.config();
        Iterator iterator = worldgenfeaturereplaceblockconfiguration.targetStates.iterator();

        while (iterator.hasNext()) {
            WorldGenFeatureOreConfiguration.a worldgenfeatureoreconfiguration_a = (WorldGenFeatureOreConfiguration.a) iterator.next();

            if (worldgenfeatureoreconfiguration_a.target.test(generatoraccessseed.getBlockState(blockposition), featureplacecontext.random())) {
                generatoraccessseed.setBlock(blockposition, worldgenfeatureoreconfiguration_a.state, 2);
                break;
            }
        }

        return true;
    }
}
