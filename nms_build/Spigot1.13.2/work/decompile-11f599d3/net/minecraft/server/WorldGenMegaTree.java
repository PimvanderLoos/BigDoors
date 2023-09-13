package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenMegaTree extends WorldGenMegaTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private static final IBlockData aH = Blocks.SPRUCE_LOG.getBlockData();
    private static final IBlockData aI = Blocks.SPRUCE_LEAVES.getBlockData();
    private static final IBlockData aJ = Blocks.PODZOL.getBlockData();
    private final boolean aK;

    public WorldGenMegaTree(boolean flag, boolean flag1) {
        super(flag, 13, 15, WorldGenMegaTree.aH, WorldGenMegaTree.aI);
        this.aK = flag1;
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i = this.a(random);

        if (!this.a(generatoraccess, blockposition, i)) {
            return false;
        } else {
            this.a(generatoraccess, blockposition.getX(), blockposition.getZ(), blockposition.getY() + i, 0, random);

            for (int j = 0; j < i; ++j) {
                IBlockData iblockdata = generatoraccess.getType(blockposition.up(j));

                if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                    this.a(set, generatoraccess, blockposition.up(j), this.b);
                }

                if (j < i - 1) {
                    iblockdata = generatoraccess.getType(blockposition.a(1, j, 0));
                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                        this.a(set, generatoraccess, blockposition.a(1, j, 0), this.b);
                    }

                    iblockdata = generatoraccess.getType(blockposition.a(1, j, 1));
                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                        this.a(set, generatoraccess, blockposition.a(1, j, 1), this.b);
                    }

                    iblockdata = generatoraccess.getType(blockposition.a(0, j, 1));
                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                        this.a(set, generatoraccess, blockposition.a(0, j, 1), this.b);
                    }
                }
            }

            this.a(generatoraccess, random, blockposition);
            return true;
        }
    }

    private void a(GeneratorAccess generatoraccess, int i, int j, int k, int l, Random random) {
        int i1 = random.nextInt(5) + (this.aK ? this.a : 3);
        int j1 = 0;

        for (int k1 = k - i1; k1 <= k; ++k1) {
            int l1 = k - k1;
            int i2 = l + MathHelper.d((float) l1 / (float) i1 * 3.5F);

            this.b(generatoraccess, new BlockPosition(i, k1, j), i2 + (l1 > 0 && i2 == j1 && (k1 & 1) == 0 ? 1 : 0));
            j1 = i2;
        }

    }

    public void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        this.b(generatoraccess, blockposition.west().north());
        this.b(generatoraccess, blockposition.east(2).north());
        this.b(generatoraccess, blockposition.west().south(2));
        this.b(generatoraccess, blockposition.east(2).south(2));

        for (int i = 0; i < 5; ++i) {
            int j = random.nextInt(64);
            int k = j % 8;
            int l = j / 8;

            if (k == 0 || k == 7 || l == 0 || l == 7) {
                this.b(generatoraccess, blockposition.a(-3 + k, 0, -3 + l));
            }
        }

    }

    private void b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.c(generatoraccess, blockposition.a(i, 0, j));
                }
            }
        }

    }

    private void c(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        for (int i = 2; i >= -3; --i) {
            BlockPosition blockposition1 = blockposition.up(i);
            IBlockData iblockdata = generatoraccess.getType(blockposition1);
            Block block = iblockdata.getBlock();

            if (block == Blocks.GRASS_BLOCK || Block.d(block)) {
                this.a(generatoraccess, blockposition1, WorldGenMegaTree.aJ);
                break;
            }

            if (!iblockdata.isAir() && i < 0) {
                break;
            }
        }

    }
}
