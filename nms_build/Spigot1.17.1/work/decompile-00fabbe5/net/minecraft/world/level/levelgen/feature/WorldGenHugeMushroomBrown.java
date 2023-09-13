package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.BlockHugeMushroom;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;

public class WorldGenHugeMushroomBrown extends WorldGenMushrooms {

    public WorldGenHugeMushroomBrown(Codec<WorldGenFeatureMushroomConfiguration> codec) {
        super(codec);
    }

    @Override
    protected void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        int j = worldgenfeaturemushroomconfiguration.foliageRadius;

        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean flag = k == -j;
                boolean flag1 = k == j;
                boolean flag2 = l == -j;
                boolean flag3 = l == j;
                boolean flag4 = flag || flag1;
                boolean flag5 = flag2 || flag3;

                if (!flag4 || !flag5) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, k, i, l);
                    if (!generatoraccess.getType(blockposition_mutableblockposition).i(generatoraccess, blockposition_mutableblockposition)) {
                        boolean flag6 = flag || flag5 && k == 1 - j;
                        boolean flag7 = flag1 || flag5 && k == j - 1;
                        boolean flag8 = flag2 || flag4 && l == 1 - j;
                        boolean flag9 = flag3 || flag4 && l == j - 1;
                        IBlockData iblockdata = worldgenfeaturemushroomconfiguration.capProvider.a(random, blockposition);

                        if (iblockdata.b(BlockHugeMushroom.WEST) && iblockdata.b(BlockHugeMushroom.EAST) && iblockdata.b(BlockHugeMushroom.NORTH) && iblockdata.b(BlockHugeMushroom.SOUTH)) {
                            iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockHugeMushroom.WEST, flag6)).set(BlockHugeMushroom.EAST, flag7)).set(BlockHugeMushroom.NORTH, flag8)).set(BlockHugeMushroom.SOUTH, flag9);
                        }

                        this.a((IWorldWriter) generatoraccess, blockposition_mutableblockposition, iblockdata);
                    }
                }
            }
        }

    }

    @Override
    protected int a(int i, int j, int k, int l) {
        return l <= 3 ? 0 : k;
    }
}
