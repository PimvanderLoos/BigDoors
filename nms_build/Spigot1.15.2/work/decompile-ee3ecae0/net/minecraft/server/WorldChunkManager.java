package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class WorldChunkManager implements BiomeManager.Provider {

    private static final List<BiomeBase> d = Lists.newArrayList(new BiomeBase[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
    protected final Map<StructureGenerator<?>, Boolean> a = Maps.newHashMap();
    protected final Set<IBlockData> b = Sets.newHashSet();
    protected final Set<BiomeBase> c;

    protected WorldChunkManager(Set<BiomeBase> set) {
        this.c = set;
    }

    public List<BiomeBase> a() {
        return WorldChunkManager.d;
    }

    public Set<BiomeBase> a(int i, int j, int k, int l) {
        int i1 = i - l >> 2;
        int j1 = j - l >> 2;
        int k1 = k - l >> 2;
        int l1 = i + l >> 2;
        int i2 = j + l >> 2;
        int j2 = k + l >> 2;
        int k2 = l1 - i1 + 1;
        int l2 = i2 - j1 + 1;
        int i3 = j2 - k1 + 1;
        Set<BiomeBase> set = Sets.newHashSet();

        for (int j3 = 0; j3 < i3; ++j3) {
            for (int k3 = 0; k3 < k2; ++k3) {
                for (int l3 = 0; l3 < l2; ++l3) {
                    int i4 = i1 + k3;
                    int j4 = j1 + l3;
                    int k4 = k1 + j3;

                    set.add(this.getBiome(i4, j4, k4));
                }
            }
        }

        return set;
    }

    @Nullable
    public BlockPosition a(int i, int j, int k, int l, List<BiomeBase> list, Random random) {
        int i1 = i - l >> 2;
        int j1 = k - l >> 2;
        int k1 = i + l >> 2;
        int l1 = k + l >> 2;
        int i2 = k1 - i1 + 1;
        int j2 = l1 - j1 + 1;
        int k2 = j >> 2;
        BlockPosition blockposition = null;
        int l2 = 0;

        for (int i3 = 0; i3 < j2; ++i3) {
            for (int j3 = 0; j3 < i2; ++j3) {
                int k3 = i1 + j3;
                int l3 = j1 + i3;

                if (list.contains(this.getBiome(k3, k2, l3))) {
                    if (blockposition == null || random.nextInt(l2 + 1) == 0) {
                        blockposition = new BlockPosition(k3 << 2, j, l3 << 2);
                    }

                    ++l2;
                }
            }
        }

        return blockposition;
    }

    public float a(int i, int j) {
        return 0.0F;
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return (Boolean) this.a.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return this.c.stream().anyMatch((biomebase) -> {
                return biomebase.a(structuregenerator1);
            });
        });
    }

    public Set<IBlockData> b() {
        if (this.b.isEmpty()) {
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                BiomeBase biomebase = (BiomeBase) iterator.next();

                this.b.add(biomebase.s().a());
            }
        }

        return this.b;
    }
}
