package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class WorldChunkManagerHell extends WorldChunkManager {

    private final BiomeBase c;

    public WorldChunkManagerHell(BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration) {
        this.c = biomelayoutfixedconfiguration.a();
    }

    public BiomeBase getBiome(BlockPosition blockposition, @Nullable BiomeBase biomebase) {
        return this.c;
    }

    public BiomeBase[] getBiomes(int i, int j, int k, int l) {
        return this.getBiomeBlock(i, j, k, l);
    }

    public BiomeBase[] a(int i, int j, int k, int l, boolean flag) {
        BiomeBase[] abiomebase = new BiomeBase[k * l];

        Arrays.fill(abiomebase, 0, k * l, this.c);
        return abiomebase;
    }

    @Nullable
    public BlockPosition a(int i, int j, int k, List<BiomeBase> list, Random random) {
        return list.contains(this.c) ? new BlockPosition(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        Map map = this.a;
        BiomeBase biomebase = this.c;

        this.c.getClass();
        return (Boolean) map.computeIfAbsent(structuregenerator, biomebase::a);
    }

    public Set<IBlockData> b() {
        if (this.b.isEmpty()) {
            this.b.add(this.c.r().a());
        }

        return this.b;
    }

    public Set<BiomeBase> a(int i, int j, int k) {
        return Sets.newHashSet(new BiomeBase[] { this.c});
    }
}
