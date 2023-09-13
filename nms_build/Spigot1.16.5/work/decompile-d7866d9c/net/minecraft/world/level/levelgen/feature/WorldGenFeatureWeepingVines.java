package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockGrowingTop;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureWeepingVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final EnumDirection[] a = EnumDirection.values();

    public WorldGenFeatureWeepingVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (!generatoraccessseed.isEmpty(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition.up());

            if (!iblockdata.a(Blocks.NETHERRACK) && !iblockdata.a(Blocks.NETHER_WART_BLOCK)) {
                return false;
            } else {
                this.a((GeneratorAccess) generatoraccessseed, random, blockposition);
                this.b(generatoraccessseed, random, blockposition);
                return true;
            }
        }
    }

    private void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        generatoraccess.setTypeAndData(blockposition, Blocks.NETHER_WART_BLOCK.getBlockData(), 2);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 200; ++i) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
                int j = 0;
                EnumDirection[] aenumdirection = WorldGenFeatureWeepingVines.a;
                int k = aenumdirection.length;

                for (int l = 0; l < k; ++l) {
                    EnumDirection enumdirection = aenumdirection[l];
                    IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, enumdirection));

                    if (iblockdata.a(Blocks.NETHERRACK) || iblockdata.a(Blocks.NETHER_WART_BLOCK)) {
                        ++j;
                    }

                    if (j > 1) {
                        break;
                    }
                }

                if (j == 1) {
                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.NETHER_WART_BLOCK.getBlockData(), 2);
                }
            }
        }

    }

    private void b(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 100; ++i) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
                IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.up());

                if (iblockdata.a(Blocks.NETHERRACK) || iblockdata.a(Blocks.NETHER_WART_BLOCK)) {
                    int j = MathHelper.nextInt(random, 1, 8);

                    if (random.nextInt(6) == 0) {
                        j *= 2;
                    }

                    if (random.nextInt(5) == 0) {
                        j = 1;
                    }

                    boolean flag = true;
                    boolean flag1 = true;

                    a(generatoraccess, random, blockposition_mutableblockposition, j, 17, 25);
                }
            }
        }

    }

    public static void a(GeneratorAccess generatoraccess, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j, int k) {
        for (int l = 0; l <= i; ++l) {
            if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
                if (l == i || !generatoraccess.isEmpty(blockposition_mutableblockposition.down())) {
                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, (IBlockData) Blocks.WEEPING_VINES.getBlockData().set(BlockGrowingTop.d, MathHelper.nextInt(random, j, k)), 2);
                    break;
                }

                generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.WEEPING_VINES_PLANT.getBlockData(), 2);
            }

            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

    }
}
