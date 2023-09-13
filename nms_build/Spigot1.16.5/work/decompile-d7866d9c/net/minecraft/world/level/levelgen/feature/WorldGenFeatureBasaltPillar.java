package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureBasaltPillar extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureBasaltPillar(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (generatoraccessseed.isEmpty(blockposition) && !generatoraccessseed.isEmpty(blockposition.up())) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.i();
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            boolean flag3 = true;

            while (generatoraccessseed.isEmpty(blockposition_mutableblockposition)) {
                if (World.isOutsideWorld(blockposition_mutableblockposition)) {
                    return true;
                }

                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.BASALT.getBlockData(), 2);
                flag = flag && this.b(generatoraccessseed, random, blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.NORTH));
                flag1 = flag1 && this.b(generatoraccessseed, random, blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.SOUTH));
                flag2 = flag2 && this.b(generatoraccessseed, random, blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.WEST));
                flag3 = flag3 && this.b(generatoraccessseed, random, blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.EAST));
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }

            blockposition_mutableblockposition.c(EnumDirection.UP);
            this.a((GeneratorAccess) generatoraccessseed, random, (BlockPosition) blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.NORTH));
            this.a((GeneratorAccess) generatoraccessseed, random, (BlockPosition) blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.SOUTH));
            this.a((GeneratorAccess) generatoraccessseed, random, (BlockPosition) blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.WEST));
            this.a((GeneratorAccess) generatoraccessseed, random, (BlockPosition) blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.EAST));
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition2 = new BlockPosition.MutableBlockPosition();

            for (int i = -3; i < 4; ++i) {
                for (int j = -3; j < 4; ++j) {
                    int k = MathHelper.a(i) * MathHelper.a(j);

                    if (random.nextInt(10) < 10 - k) {
                        blockposition_mutableblockposition2.g(blockposition_mutableblockposition.b(i, 0, j));
                        int l = 3;

                        while (generatoraccessseed.isEmpty(blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition2, EnumDirection.DOWN))) {
                            blockposition_mutableblockposition2.c(EnumDirection.DOWN);
                            --l;
                            if (l <= 0) {
                                break;
                            }
                        }

                        if (!generatoraccessseed.isEmpty(blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition2, EnumDirection.DOWN))) {
                            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition2, Blocks.BASALT.getBlockData(), 2);
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        if (random.nextBoolean()) {
            generatoraccess.setTypeAndData(blockposition, Blocks.BASALT.getBlockData(), 2);
        }

    }

    private boolean b(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        if (random.nextInt(10) != 0) {
            generatoraccess.setTypeAndData(blockposition, Blocks.BASALT.getBlockData(), 2);
            return true;
        } else {
            return false;
        }
    }
}
