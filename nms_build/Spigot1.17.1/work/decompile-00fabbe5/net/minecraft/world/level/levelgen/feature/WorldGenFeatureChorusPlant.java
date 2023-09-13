package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockChorusFlower;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureChorusPlant extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureChorusPlant(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        Random random = featureplacecontext.c();

        if (generatoraccessseed.isEmpty(blockposition) && generatoraccessseed.getType(blockposition.down()).a(Blocks.END_STONE)) {
            BlockChorusFlower.a(generatoraccessseed, blockposition, random, 8);
            return true;
        } else {
            return false;
        }
    }
}
