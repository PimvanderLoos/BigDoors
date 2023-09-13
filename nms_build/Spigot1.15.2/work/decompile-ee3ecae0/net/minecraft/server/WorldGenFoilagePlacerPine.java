package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerPine extends WorldGenFoilagePlacer {

    public WorldGenFoilagePlacerPine(int i, int j) {
        super(i, j, WorldGenFoilagePlacers.c);
    }

    public <T> WorldGenFoilagePlacerPine(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
    }

    @Override
    public void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, int j, int k, BlockPosition blockposition, Set<BlockPosition> set) {
        int l = 0;

        for (int i1 = i; i1 >= j; --i1) {
            this.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, blockposition, i1, l, set);
            if (l >= 1 && i1 == j + 1) {
                --l;
            } else if (l < k) {
                ++l;
            }
        }

    }

    @Override
    public int a(Random random, int i, int j, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        return this.a + random.nextInt(this.b + 1) + random.nextInt(j - i + 1);
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
