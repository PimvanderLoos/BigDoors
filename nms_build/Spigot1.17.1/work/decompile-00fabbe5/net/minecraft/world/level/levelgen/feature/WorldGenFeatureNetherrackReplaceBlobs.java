package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRadiusConfiguration;

public class WorldGenFeatureNetherrackReplaceBlobs extends WorldGenerator<WorldGenFeatureRadiusConfiguration> {

    public WorldGenFeatureNetherrackReplaceBlobs(Codec<WorldGenFeatureRadiusConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureRadiusConfiguration> featureplacecontext) {
        WorldGenFeatureRadiusConfiguration worldgenfeatureradiusconfiguration = (WorldGenFeatureRadiusConfiguration) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        Block block = worldgenfeatureradiusconfiguration.targetState.getBlock();
        BlockPosition blockposition = a((GeneratorAccess) generatoraccessseed, featureplacecontext.d().i().a(EnumDirection.EnumAxis.Y, generatoraccessseed.getMinBuildHeight() + 1, generatoraccessseed.getMaxBuildHeight() - 1), block);

        if (blockposition == null) {
            return false;
        } else {
            int i = worldgenfeatureradiusconfiguration.b().a(random);
            int j = worldgenfeatureradiusconfiguration.b().a(random);
            int k = worldgenfeatureradiusconfiguration.b().a(random);
            int l = Math.max(i, Math.max(j, k));
            boolean flag = false;
            Iterator iterator = BlockPosition.a(blockposition, i, j, k).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (blockposition1.k(blockposition) > l) {
                    break;
                }

                IBlockData iblockdata = generatoraccessseed.getType(blockposition1);

                if (iblockdata.a(block)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition1, worldgenfeatureradiusconfiguration.replaceState);
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Nullable
    private static BlockPosition a(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Block block) {
        while (blockposition_mutableblockposition.getY() > generatoraccess.getMinBuildHeight() + 1) {
            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

            if (iblockdata.a(block)) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        return null;
    }
}
