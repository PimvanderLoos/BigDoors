package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerAcacia extends WorldGenFoilagePlacer {

    public WorldGenFoilagePlacerAcacia(int i, int j) {
        super(i, j, WorldGenFoilagePlacers.d);
    }

    public <T> WorldGenFoilagePlacerAcacia(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
    }

    @Override
    public void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, int j, int k, BlockPosition blockposition, Set<BlockPosition> set) {
        worldgenfeaturesmalltreeconfigurationconfiguration.a.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, blockposition, 0, k, set);
        worldgenfeaturesmalltreeconfigurationconfiguration.a.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, blockposition, 1, 1, set);
        BlockPosition blockposition1 = blockposition.up();

        int l;

        for (l = -1; l <= 1; ++l) {
            for (int i1 = -1; i1 <= 1; ++i1) {
                this.a(virtuallevelwritable, random, blockposition1.b(l, 0, i1), worldgenfeaturesmalltreeconfigurationconfiguration, set);
            }
        }

        for (l = 2; l <= k - 1; ++l) {
            this.a(virtuallevelwritable, random, blockposition1.east(l), worldgenfeaturesmalltreeconfigurationconfiguration, set);
            this.a(virtuallevelwritable, random, blockposition1.west(l), worldgenfeaturesmalltreeconfigurationconfiguration, set);
            this.a(virtuallevelwritable, random, blockposition1.south(l), worldgenfeaturesmalltreeconfigurationconfiguration, set);
            this.a(virtuallevelwritable, random, blockposition1.north(l), worldgenfeaturesmalltreeconfigurationconfiguration, set);
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
        return l == 0 ? 0 : 2;
    }
}
