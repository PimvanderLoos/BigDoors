package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        featureplacecontext.config();
        if (!generatoraccessseed.isEmptyBlock(blockposition)) {
            return false;
        } else {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && BlockVine.isAcceptableNeighbour(generatoraccessseed, blockposition.relative(enumdirection), enumdirection)) {
                    generatoraccessseed.setBlock(blockposition, (IBlockData) Blocks.VINE.defaultBlockState().setValue(BlockVine.getPropertyForFace(enumdirection), true), 2);
                    return true;
                }
            }

            return false;
        }
    }
}
