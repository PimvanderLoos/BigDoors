package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();

        featureplacecontext.e();
        if (!generatoraccessseed.isEmpty(blockposition)) {
            return false;
        } else {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && BlockVine.a((IBlockAccess) generatoraccessseed, blockposition.shift(enumdirection), enumdirection)) {
                    generatoraccessseed.setTypeAndData(blockposition, (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.getDirection(enumdirection), true), 2);
                    return true;
                }
            }

            return false;
        }
    }
}
