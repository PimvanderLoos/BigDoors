package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureCoralMushroom extends WorldGenFeatureCoral {

    public WorldGenFeatureCoralMushroom(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeFeature(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        int i = randomsource.nextInt(3) + 3;
        int j = randomsource.nextInt(3) + 3;
        int k = randomsource.nextInt(3) + 3;
        int l = randomsource.nextInt(3) + 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i1 = 0; i1 <= j; ++i1) {
            for (int j1 = 0; j1 <= i; ++j1) {
                for (int k1 = 0; k1 <= k; ++k1) {
                    blockposition_mutableblockposition.set(i1 + blockposition.getX(), j1 + blockposition.getY(), k1 + blockposition.getZ());
                    blockposition_mutableblockposition.move(EnumDirection.DOWN, l);
                    if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && randomsource.nextFloat() >= 0.1F && !this.placeCoralBlock(generatoraccess, randomsource, blockposition_mutableblockposition, iblockdata)) {
                        ;
                    }
                }
            }
        }

        return true;
    }
}
