package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenLightStone1 extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenLightStone1(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (!generatoraccessseed.isEmpty(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition.up());

            if (!iblockdata.a(Blocks.NETHERRACK) && !iblockdata.a(Blocks.BASALT) && !iblockdata.a(Blocks.BLACKSTONE)) {
                return false;
            } else {
                generatoraccessseed.setTypeAndData(blockposition, Blocks.GLOWSTONE.getBlockData(), 2);

                for (int i = 0; i < 1500; ++i) {
                    BlockPosition blockposition1 = blockposition.b(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));

                    if (generatoraccessseed.getType(blockposition1).isAir()) {
                        int j = 0;
                        EnumDirection[] aenumdirection = EnumDirection.values();
                        int k = aenumdirection.length;

                        for (int l = 0; l < k; ++l) {
                            EnumDirection enumdirection = aenumdirection[l];

                            if (generatoraccessseed.getType(blockposition1.shift(enumdirection)).a(Blocks.GLOWSTONE)) {
                                ++j;
                            }

                            if (j > 1) {
                                break;
                            }
                        }

                        if (j == 1) {
                            generatoraccessseed.setTypeAndData(blockposition1, Blocks.GLOWSTONE.getBlockData(), 2);
                        }
                    }
                }

                return true;
            }
        }
    }
}
