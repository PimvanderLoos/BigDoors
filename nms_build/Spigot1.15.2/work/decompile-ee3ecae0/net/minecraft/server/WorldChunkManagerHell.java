package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class WorldChunkManagerHell extends WorldChunkManager {

    private final BiomeBase d;

    public WorldChunkManagerHell(BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration) {
        super(ImmutableSet.of(biomelayoutfixedconfiguration.a()));
        this.d = biomelayoutfixedconfiguration.a();
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return this.d;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, int k, int l, List<BiomeBase> list, Random random) {
        return list.contains(this.d) ? new BlockPosition(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1)) : null;
    }

    @Override
    public Set<BiomeBase> a(int i, int j, int k, int l) {
        return Sets.newHashSet(new BiomeBase[]{this.d});
    }
}
