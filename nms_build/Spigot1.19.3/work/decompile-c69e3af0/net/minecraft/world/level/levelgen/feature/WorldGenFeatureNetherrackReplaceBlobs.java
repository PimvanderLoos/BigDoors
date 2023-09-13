package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRadiusConfiguration;

public class WorldGenFeatureNetherrackReplaceBlobs extends WorldGenerator<WorldGenFeatureRadiusConfiguration> {

    public WorldGenFeatureNetherrackReplaceBlobs(Codec<WorldGenFeatureRadiusConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureRadiusConfiguration> featureplacecontext) {
        WorldGenFeatureRadiusConfiguration worldgenfeatureradiusconfiguration = (WorldGenFeatureRadiusConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        Block block = worldgenfeatureradiusconfiguration.targetState.getBlock();
        BlockPosition blockposition = findTarget(generatoraccessseed, featureplacecontext.origin().mutable().clamp(EnumDirection.EnumAxis.Y, generatoraccessseed.getMinBuildHeight() + 1, generatoraccessseed.getMaxBuildHeight() - 1), block);

        if (blockposition == null) {
            return false;
        } else {
            int i = worldgenfeatureradiusconfiguration.radius().sample(randomsource);
            int j = worldgenfeatureradiusconfiguration.radius().sample(randomsource);
            int k = worldgenfeatureradiusconfiguration.radius().sample(randomsource);
            int l = Math.max(i, Math.max(j, k));
            boolean flag = false;
            Iterator iterator = BlockPosition.withinManhattan(blockposition, i, j, k).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (blockposition1.distManhattan(blockposition) > l) {
                    break;
                }

                IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition1);

                if (iblockdata.is(block)) {
                    this.setBlock(generatoraccessseed, blockposition1, worldgenfeatureradiusconfiguration.replaceState);
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Nullable
    private static BlockPosition findTarget(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Block block) {
        while (blockposition_mutableblockposition.getY() > generatoraccess.getMinBuildHeight() + 1) {
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition);

            if (iblockdata.is(block)) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.move(EnumDirection.DOWN);
        }

        return null;
    }
}
