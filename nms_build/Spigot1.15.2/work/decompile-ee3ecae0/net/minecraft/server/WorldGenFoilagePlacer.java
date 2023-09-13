package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;

public abstract class WorldGenFoilagePlacer implements MinecraftSerializable {

    protected final int a;
    protected final int b;
    protected final WorldGenFoilagePlacers<?> c;

    public WorldGenFoilagePlacer(int i, int j, WorldGenFoilagePlacers<?> worldgenfoilageplacers) {
        this.a = i;
        this.b = j;
        this.c = worldgenfoilageplacers;
    }

    public abstract void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, int j, int k, BlockPosition blockposition, Set<BlockPosition> set);

    public abstract int a(Random random, int i, int j, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration);

    protected abstract boolean a(Random random, int i, int j, int k, int l, int i1);

    public abstract int a(int i, int j, int k, int l);

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, int i, BlockPosition blockposition, int j, int k, Set<BlockPosition> set) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = -k; l <= k; ++l) {
            for (int i1 = -k; i1 <= k; ++i1) {
                if (!this.a(random, i, l, j, i1, k)) {
                    blockposition_mutableblockposition.d(l + blockposition.getX(), j + blockposition.getY(), i1 + blockposition.getZ());
                    this.a(virtuallevelwritable, random, blockposition_mutableblockposition, worldgenfeaturesmalltreeconfigurationconfiguration, set);
                }
            }
        }

    }

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration, Set<BlockPosition> set) {
        if (WorldGenTreeAbstract.f(virtuallevelwritable, blockposition) || WorldGenTreeAbstract.i(virtuallevelwritable, blockposition) || WorldGenTreeAbstract.e(virtuallevelwritable, blockposition)) {
            virtuallevelwritable.setTypeAndData(blockposition, worldgenfeaturesmalltreeconfigurationconfiguration.n.a(random, blockposition), 19);
            set.add(blockposition.immutableCopy());
        }

    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("type"), dynamicops.createString(IRegistry.v.getKey(this.c).toString())).put(dynamicops.createString("radius"), dynamicops.createInt(this.a)).put(dynamicops.createString("radius_random"), dynamicops.createInt(this.b));
        return (new Dynamic(dynamicops, dynamicops.createMap(builder.build()))).getValue();
    }
}
