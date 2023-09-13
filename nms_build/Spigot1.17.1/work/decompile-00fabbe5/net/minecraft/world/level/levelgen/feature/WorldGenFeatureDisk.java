package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class WorldGenFeatureDisk extends WorldGenerator<WorldGenFeatureCircleConfiguration> {

    public WorldGenFeatureDisk(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureCircleConfiguration> featureplacecontext) {
        WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration = (WorldGenFeatureCircleConfiguration) featureplacecontext.e();
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        boolean flag = false;
        int i = blockposition.getY();
        int j = i + worldgenfeaturecircleconfiguration.halfHeight;
        int k = i - worldgenfeaturecircleconfiguration.halfHeight - 1;
        boolean flag1 = worldgenfeaturecircleconfiguration.state.getBlock() instanceof BlockFalling;
        int l = worldgenfeaturecircleconfiguration.radius.a(featureplacecontext.c());

        for (int i1 = blockposition.getX() - l; i1 <= blockposition.getX() + l; ++i1) {
            for (int j1 = blockposition.getZ() - l; j1 <= blockposition.getZ() + l; ++j1) {
                int k1 = i1 - blockposition.getX();
                int l1 = j1 - blockposition.getZ();

                if (k1 * k1 + l1 * l1 <= l * l) {
                    boolean flag2 = false;

                    for (int i2 = j; i2 >= k; --i2) {
                        BlockPosition blockposition1 = new BlockPosition(i1, i2, j1);
                        IBlockData iblockdata = generatoraccessseed.getType(blockposition1);
                        Block block = iblockdata.getBlock();
                        boolean flag3 = false;

                        if (i2 > k) {
                            Iterator iterator = worldgenfeaturecircleconfiguration.targets.iterator();

                            while (iterator.hasNext()) {
                                IBlockData iblockdata1 = (IBlockData) iterator.next();

                                if (iblockdata1.a(block)) {
                                    generatoraccessseed.setTypeAndData(blockposition1, worldgenfeaturecircleconfiguration.state, 2);
                                    this.a(generatoraccessseed, blockposition1);
                                    flag = true;
                                    flag3 = true;
                                    break;
                                }
                            }
                        }

                        if (flag1 && flag2 && iblockdata.isAir()) {
                            IBlockData iblockdata2 = worldgenfeaturecircleconfiguration.state.a(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();

                            generatoraccessseed.setTypeAndData(new BlockPosition(i1, i2 + 1, j1), iblockdata2, 2);
                        }

                        flag2 = flag3;
                    }
                }
            }
        }

        return flag;
    }
}
