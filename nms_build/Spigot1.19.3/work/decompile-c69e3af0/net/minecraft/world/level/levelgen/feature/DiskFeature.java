package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class DiskFeature extends WorldGenerator<WorldGenFeatureCircleConfiguration> {

    public DiskFeature(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureCircleConfiguration> featureplacecontext) {
        WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration = (WorldGenFeatureCircleConfiguration) featureplacecontext.config();
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        boolean flag = false;
        int i = blockposition.getY();
        int j = i + worldgenfeaturecircleconfiguration.halfHeight();
        int k = i - worldgenfeaturecircleconfiguration.halfHeight() - 1;
        int l = worldgenfeaturecircleconfiguration.radius().sample(randomsource);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-l, 0, -l), blockposition.offset(l, 0, l)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            int i1 = blockposition1.getX() - blockposition.getX();
            int j1 = blockposition1.getZ() - blockposition.getZ();

            if (i1 * i1 + j1 * j1 <= l * l) {
                flag |= this.placeColumn(worldgenfeaturecircleconfiguration, generatoraccessseed, randomsource, j, k, blockposition_mutableblockposition.set(blockposition1));
            }
        }

        return flag;
    }

    protected boolean placeColumn(WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration, GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, int i, int j, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        boolean flag = false;
        Object object = null;

        for (int k = i; k > j; --k) {
            blockposition_mutableblockposition.setY(k);
            if (worldgenfeaturecircleconfiguration.target().test(generatoraccessseed, blockposition_mutableblockposition)) {
                IBlockData iblockdata = worldgenfeaturecircleconfiguration.stateProvider().getState(generatoraccessseed, randomsource, blockposition_mutableblockposition);

                generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                this.markAboveForPostProcessing(generatoraccessseed, blockposition_mutableblockposition);
                flag = true;
            }
        }

        return flag;
    }
}
