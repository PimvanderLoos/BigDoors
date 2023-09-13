package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerBlob extends WorldGenFoilagePlacer {

    public WorldGenFoilagePlacerBlob(int i, int j) {
        super(i, j, WorldGenFoilagePlacers.a);
    }

    public <T> WorldGenFoilagePlacerBlob(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
    }

    @Override
    public void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, int j, int k, BlockPosition blockposition, Set<BlockPosition> set) {
        for (int l = i; l >= j; --l) {
            int i1 = Math.max(k - 1 - (l - i) / 2, 0);

            this.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, blockposition, l, i1, set);
        }

    }

    @Override
    public int a(Random random, int i, int j, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        return this.a + random.nextInt(this.b + 1);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, int i1) {
        return Math.abs(j) == i1 && Math.abs(l) == i1 && (random.nextInt(2) == 0 || k == i);
    }

    @Override
    public int a(int i, int j, int k, int l) {
        return l == 0 ? 0 : 1;
    }
}
