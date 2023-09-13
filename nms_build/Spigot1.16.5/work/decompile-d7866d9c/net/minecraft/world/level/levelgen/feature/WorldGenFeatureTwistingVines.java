package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockGrowingTop;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureTwistingVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureTwistingVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return a(generatoraccessseed, random, blockposition, 8, 4, 8);
    }

    public static boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, int i, int j, int k) {
        if (a(generatoraccess, blockposition)) {
            return false;
        } else {
            b(generatoraccess, random, blockposition, i, j, k);
            return true;
        }
    }

    private static void b(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = 0; l < i * i; ++l) {
            blockposition_mutableblockposition.g(blockposition).e(MathHelper.nextInt(random, -i, i), MathHelper.nextInt(random, -j, j), MathHelper.nextInt(random, -i, i));
            if (a(generatoraccess, blockposition_mutableblockposition) && !a(generatoraccess, (BlockPosition) blockposition_mutableblockposition)) {
                int i1 = MathHelper.nextInt(random, 1, k);

                if (random.nextInt(6) == 0) {
                    i1 *= 2;
                }

                if (random.nextInt(5) == 0) {
                    i1 = 1;
                }

                boolean flag = true;
                boolean flag1 = true;

                a(generatoraccess, random, blockposition_mutableblockposition, i1, 17, 25);
            }
        }

    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        do {
            blockposition_mutableblockposition.e(0, -1, 0);
            if (World.isOutsideWorld(blockposition_mutableblockposition)) {
                return false;
            }
        } while (generatoraccess.getType(blockposition_mutableblockposition).isAir());

        blockposition_mutableblockposition.e(0, 1, 0);
        return true;
    }

    public static void a(GeneratorAccess generatoraccess, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j, int k) {
        for (int l = 1; l <= i; ++l) {
            if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
                if (l == i || !generatoraccess.isEmpty(blockposition_mutableblockposition.up())) {
                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, (IBlockData) Blocks.TWISTING_VINES.getBlockData().set(BlockGrowingTop.d, MathHelper.nextInt(random, j, k)), 2);
                    break;
                }

                generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.TWISTING_VINES_PLANT.getBlockData(), 2);
            }

            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!generatoraccess.isEmpty(blockposition)) {
            return true;
        } else {
            IBlockData iblockdata = generatoraccess.getType(blockposition.down());

            return !iblockdata.a(Blocks.NETHERRACK) && !iblockdata.a(Blocks.WARPED_NYLIUM) && !iblockdata.a(Blocks.WARPED_WART_BLOCK);
        }
    }
}
