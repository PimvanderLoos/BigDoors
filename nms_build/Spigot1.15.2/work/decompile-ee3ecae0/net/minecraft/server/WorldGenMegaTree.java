package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenMegaTree extends WorldGenMegaTreeAbstract<WorldGenMegaTreeConfiguration> {

    public WorldGenMegaTree(Function<Dynamic<?>, ? extends WorldGenMegaTreeConfiguration> function) {
        super(function);
    }

    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        int i = this.a(random, worldgenmegatreeconfiguration);

        if (!this.a(virtuallevelwritable, blockposition, i)) {
            return false;
        } else {
            this.a(virtuallevelwritable, random, blockposition.getX(), blockposition.getZ(), blockposition.getY() + i, 0, set1, structureboundingbox, worldgenmegatreeconfiguration);
            this.a(virtuallevelwritable, random, blockposition, i, set, structureboundingbox, worldgenmegatreeconfiguration);
            return true;
        }
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, int i, int j, int k, int l, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        int i1 = random.nextInt(5) + worldgenmegatreeconfiguration.b;
        int j1 = 0;

        for (int k1 = k - i1; k1 <= k; ++k1) {
            int l1 = k - k1;
            int i2 = l + MathHelper.d((float) l1 / (float) i1 * 3.5F);

            this.a(virtuallevelwritable, random, new BlockPosition(i, k1, j), i2 + (l1 > 0 && i2 == j1 && (k1 & 1) == 0 ? 1 : 0), set, structureboundingbox, (WorldGenFeatureTreeConfiguration) worldgenmegatreeconfiguration);
            j1 = i2;
        }

    }
}
