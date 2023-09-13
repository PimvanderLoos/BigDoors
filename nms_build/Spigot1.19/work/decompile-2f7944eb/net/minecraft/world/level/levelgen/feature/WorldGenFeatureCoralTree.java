package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureCoralTree extends WorldGenFeatureCoral {

    public WorldGenFeatureCoralTree(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeFeature(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        int i = randomsource.nextInt(3) + 1;

        for (int j = 0; j < i; ++j) {
            if (!this.placeCoralBlock(generatoraccess, randomsource, blockposition_mutableblockposition, iblockdata)) {
                return true;
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        BlockPosition blockposition1 = blockposition_mutableblockposition.immutable();
        int k = randomsource.nextInt(3) + 2;
        List<EnumDirection> list = EnumDirection.EnumDirectionLimit.HORIZONTAL.shuffledCopy(randomsource);
        List<EnumDirection> list1 = list.subList(0, k);
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            blockposition_mutableblockposition.set(blockposition1);
            blockposition_mutableblockposition.move(enumdirection);
            int l = randomsource.nextInt(5) + 2;
            int i1 = 0;

            for (int j1 = 0; j1 < l && this.placeCoralBlock(generatoraccess, randomsource, blockposition_mutableblockposition, iblockdata); ++j1) {
                ++i1;
                blockposition_mutableblockposition.move(EnumDirection.UP);
                if (j1 == 0 || i1 >= 2 && randomsource.nextFloat() < 0.25F) {
                    blockposition_mutableblockposition.move(enumdirection);
                    i1 = 0;
                }
            }
        }

        return true;
    }
}
