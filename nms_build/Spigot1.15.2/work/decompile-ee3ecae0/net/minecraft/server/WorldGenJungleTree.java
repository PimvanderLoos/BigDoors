package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenJungleTree extends WorldGenMegaTreeAbstract<WorldGenMegaTreeConfiguration> {

    public WorldGenJungleTree(Function<Dynamic<?>, ? extends WorldGenMegaTreeConfiguration> function) {
        super(function);
    }

    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        int i = this.a(random, worldgenmegatreeconfiguration);

        if (!this.a(virtuallevelwritable, blockposition, i)) {
            return false;
        } else {
            this.c(virtuallevelwritable, random, blockposition.up(i), 2, set1, structureboundingbox, worldgenmegatreeconfiguration);

            for (int j = blockposition.getY() + i - 2 - random.nextInt(4); j > blockposition.getY() + i / 2; j -= 2 + random.nextInt(4)) {
                float f = random.nextFloat() * 6.2831855F;
                int k = blockposition.getX() + (int) (0.5F + MathHelper.cos(f) * 4.0F);
                int l = blockposition.getZ() + (int) (0.5F + MathHelper.sin(f) * 4.0F);

                int i1;

                for (i1 = 0; i1 < 5; ++i1) {
                    k = blockposition.getX() + (int) (1.5F + MathHelper.cos(f) * (float) i1);
                    l = blockposition.getZ() + (int) (1.5F + MathHelper.sin(f) * (float) i1);
                    BlockPosition blockposition1 = new BlockPosition(k, j - 3 + i1 / 2, l);

                    this.a(virtuallevelwritable, random, blockposition1, set, structureboundingbox, worldgenmegatreeconfiguration);
                }

                i1 = 1 + random.nextInt(2);
                int j1 = j;

                for (int k1 = j - i1; k1 <= j1; ++k1) {
                    int l1 = k1 - j1;

                    this.b(virtuallevelwritable, random, new BlockPosition(k, k1, l), 1 - l1, set1, structureboundingbox, worldgenmegatreeconfiguration);
                }
            }

            this.a(virtuallevelwritable, random, blockposition, i, set, structureboundingbox, worldgenmegatreeconfiguration);
            return true;
        }
    }

    private void c(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        boolean flag = true;

        for (int j = -2; j <= 0; ++j) {
            this.a(virtuallevelwritable, random, blockposition.up(j), i + 1 - j, set, structureboundingbox, worldgenfeaturetreeconfiguration);
        }

    }
}
