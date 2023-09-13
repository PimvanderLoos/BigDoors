package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenJungleTree extends WorldGenMegaTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    public WorldGenJungleTree(boolean flag, int i, int j, IBlockData iblockdata, IBlockData iblockdata1) {
        super(flag, i, j, iblockdata, iblockdata1);
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i = this.a(random);

        if (!this.a(generatoraccess, blockposition, i)) {
            return false;
        } else {
            this.d(generatoraccess, blockposition.up(i), 2);

            for (int j = blockposition.getY() + i - 2 - random.nextInt(4); j > blockposition.getY() + i / 2; j -= 2 + random.nextInt(4)) {
                float f = random.nextFloat() * 6.2831855F;
                int k = blockposition.getX() + (int) (0.5F + MathHelper.cos(f) * 4.0F);
                int l = blockposition.getZ() + (int) (0.5F + MathHelper.sin(f) * 4.0F);

                int i1;

                for (i1 = 0; i1 < 5; ++i1) {
                    k = blockposition.getX() + (int) (1.5F + MathHelper.cos(f) * (float) i1);
                    l = blockposition.getZ() + (int) (1.5F + MathHelper.sin(f) * (float) i1);
                    this.a(set, generatoraccess, new BlockPosition(k, j - 3 + i1 / 2, l), this.b);
                }

                i1 = 1 + random.nextInt(2);
                int j1 = j;

                for (int k1 = j - i1; k1 <= j1; ++k1) {
                    int l1 = k1 - j1;

                    this.c(generatoraccess, new BlockPosition(k, k1, l), 1 - l1);
                }
            }

            for (int i2 = 0; i2 < i; ++i2) {
                BlockPosition blockposition1 = blockposition.up(i2);

                if (this.a(generatoraccess.getType(blockposition1).getBlock())) {
                    this.a(set, generatoraccess, blockposition1, this.b);
                    if (i2 > 0) {
                        this.a(generatoraccess, random, blockposition1.west(), BlockVine.EAST);
                        this.a(generatoraccess, random, blockposition1.north(), BlockVine.SOUTH);
                    }
                }

                if (i2 < i - 1) {
                    BlockPosition blockposition2 = blockposition1.east();

                    if (this.a(generatoraccess.getType(blockposition2).getBlock())) {
                        this.a(set, generatoraccess, blockposition2, this.b);
                        if (i2 > 0) {
                            this.a(generatoraccess, random, blockposition2.east(), BlockVine.WEST);
                            this.a(generatoraccess, random, blockposition2.north(), BlockVine.SOUTH);
                        }
                    }

                    BlockPosition blockposition3 = blockposition1.south().east();

                    if (this.a(generatoraccess.getType(blockposition3).getBlock())) {
                        this.a(set, generatoraccess, blockposition3, this.b);
                        if (i2 > 0) {
                            this.a(generatoraccess, random, blockposition3.east(), BlockVine.WEST);
                            this.a(generatoraccess, random, blockposition3.south(), BlockVine.NORTH);
                        }
                    }

                    BlockPosition blockposition4 = blockposition1.south();

                    if (this.a(generatoraccess.getType(blockposition4).getBlock())) {
                        this.a(set, generatoraccess, blockposition4, this.b);
                        if (i2 > 0) {
                            this.a(generatoraccess, random, blockposition4.west(), BlockVine.EAST);
                            this.a(generatoraccess, random, blockposition4.south(), BlockVine.NORTH);
                        }
                    }
                }
            }

            return true;
        }
    }

    private void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
        if (random.nextInt(3) > 0 && generatoraccess.isEmpty(blockposition)) {
            this.a(generatoraccess, blockposition, (IBlockData) Blocks.VINE.getBlockData().set(blockstateboolean, true));
        }

    }

    private void d(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        boolean flag = true;

        for (int j = -2; j <= 0; ++j) {
            this.b(generatoraccess, blockposition.up(j), i + 1 - j);
        }

    }
}
