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
import net.minecraft.world.level.material.Material;

public class WorldGenFeatureBlueIce extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureBlueIce(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (blockposition.getY() > generatoraccessseed.getSeaLevel() - 1) {
            return false;
        } else if (!generatoraccessseed.getType(blockposition).a(Blocks.WATER) && !generatoraccessseed.getType(blockposition.down()).a(Blocks.WATER)) {
            return false;
        } else {
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            int j;

            for (j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && generatoraccessseed.getType(blockposition.shift(enumdirection)).a(Blocks.PACKED_ICE)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                generatoraccessseed.setTypeAndData(blockposition, Blocks.BLUE_ICE.getBlockData(), 2);

                for (int k = 0; k < 200; ++k) {
                    i = random.nextInt(5) - random.nextInt(6);
                    j = 3;
                    if (i < 2) {
                        j += i / 2;
                    }

                    if (j >= 1) {
                        BlockPosition blockposition1 = blockposition.b(random.nextInt(j) - random.nextInt(j), i, random.nextInt(j) - random.nextInt(j));
                        IBlockData iblockdata = generatoraccessseed.getType(blockposition1);

                        if (iblockdata.getMaterial() == Material.AIR || iblockdata.a(Blocks.WATER) || iblockdata.a(Blocks.PACKED_ICE) || iblockdata.a(Blocks.ICE)) {
                            EnumDirection[] aenumdirection1 = EnumDirection.values();
                            int l = aenumdirection1.length;

                            for (int i1 = 0; i1 < l; ++i1) {
                                EnumDirection enumdirection1 = aenumdirection1[i1];
                                IBlockData iblockdata1 = generatoraccessseed.getType(blockposition1.shift(enumdirection1));

                                if (iblockdata1.a(Blocks.BLUE_ICE)) {
                                    generatoraccessseed.setTypeAndData(blockposition1, Blocks.BLUE_ICE.getBlockData(), 2);
                                    break;
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }
    }
}
