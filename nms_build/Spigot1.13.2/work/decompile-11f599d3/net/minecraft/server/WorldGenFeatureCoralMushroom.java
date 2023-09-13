package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureCoralMushroom extends WorldGenFeatureCoral {

    public WorldGenFeatureCoralMushroom() {}

    protected boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = random.nextInt(3) + 3;
        int j = random.nextInt(3) + 3;
        int k = random.nextInt(3) + 3;
        int l = random.nextInt(3) + 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);

        for (int i1 = 0; i1 <= j; ++i1) {
            for (int j1 = 0; j1 <= i; ++j1) {
                for (int k1 = 0; k1 <= k; ++k1) {
                    blockposition_mutableblockposition.c(i1 + blockposition.getX(), j1 + blockposition.getY(), k1 + blockposition.getZ());
                    blockposition_mutableblockposition.c(EnumDirection.DOWN, l);
                    if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && random.nextFloat() >= 0.1F && !this.b(generatoraccess, random, blockposition_mutableblockposition, iblockdata)) {
                        ;
                    }
                }
            }
        }

        return true;
    }
}
