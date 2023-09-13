package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;

public class WorldGenFeatureBlockPile extends WorldGenerator<WorldGenFeatureBlockPileConfiguration> {

    public WorldGenFeatureBlockPile(Codec<WorldGenFeatureBlockPileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureBlockPileConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration = (WorldGenFeatureBlockPileConfiguration) featureplacecontext.e();

        if (blockposition.getY() < generatoraccessseed.getMinBuildHeight() + 5) {
            return false;
        } else {
            int i = 2 + random.nextInt(2);
            int j = 2 + random.nextInt(2);
            Iterator iterator = BlockPosition.a(blockposition.c(-i, 0, -j), blockposition.c(i, 1, j)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int k = blockposition.getX() - blockposition1.getX();
                int l = blockposition.getZ() - blockposition1.getZ();

                if ((float) (k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
                    this.a((GeneratorAccess) generatoraccessseed, blockposition1, random, worldgenfeatureblockpileconfiguration);
                } else if ((double) random.nextFloat() < 0.031D) {
                    this.a((GeneratorAccess) generatoraccessseed, blockposition1, random, worldgenfeatureblockpileconfiguration);
                }
            }

            return true;
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = generatoraccess.getType(blockposition1);

        return iblockdata.a(Blocks.DIRT_PATH) ? random.nextBoolean() : iblockdata.d(generatoraccess, blockposition1, EnumDirection.UP);
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration) {
        if (generatoraccess.isEmpty(blockposition) && this.a(generatoraccess, blockposition, random)) {
            generatoraccess.setTypeAndData(blockposition, worldgenfeatureblockpileconfiguration.stateProvider.a(random, blockposition), 4);
        }

    }
}
