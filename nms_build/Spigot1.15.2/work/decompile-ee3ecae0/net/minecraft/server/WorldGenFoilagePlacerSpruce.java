package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerSpruce extends WorldGenFoilagePlacer {

    public WorldGenFoilagePlacerSpruce(int i, int j) {
        super(i, j, WorldGenFoilagePlacers.b);
    }

    public <T> WorldGenFoilagePlacerSpruce(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
    }

    @Override
    public void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, int j, int k, BlockPosition blockposition, Set<BlockPosition> set) {
        int l = random.nextInt(2);
        int i1 = 1;
        byte b0 = 0;

        for (int j1 = i; j1 >= j; --j1) {
            this.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, blockposition, j1, l, set);
            if (l >= i1) {
                l = b0;
                b0 = 1;
                i1 = Math.min(i1 + 1, k);
            } else {
                ++l;
            }
        }

    }

    @Override
    public int a(Random random, int i, int j, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        return this.a + random.nextInt(this.b + 1);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, int i1) {
        return Math.abs(j) == i1 && Math.abs(l) == i1 && i1 > 0;
    }

    @Override
    public int a(int i, int j, int k, int l) {
        return l <= 1 ? 0 : 2;
    }
}
