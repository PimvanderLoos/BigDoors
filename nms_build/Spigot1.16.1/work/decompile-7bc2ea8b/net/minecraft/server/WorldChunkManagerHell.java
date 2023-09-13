package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class WorldChunkManagerHell extends WorldChunkManager {

    public static final Codec<WorldChunkManagerHell> e = IRegistry.BIOME.fieldOf("biome").xmap(WorldChunkManagerHell::new, (worldchunkmanagerhell) -> {
        return worldchunkmanagerhell.f;
    }).stable().codec();
    private final BiomeBase f;

    public WorldChunkManagerHell(BiomeBase biomebase) {
        super(ImmutableList.of(biomebase));
        this.f = biomebase;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerHell.e;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return this.f;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, int k, int l, int i1, List<BiomeBase> list, Random random, boolean flag) {
        return list.contains(this.f) ? (flag ? new BlockPosition(i, j, k) : new BlockPosition(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1))) : null;
    }

    @Override
    public Set<BiomeBase> a(int i, int j, int k, int l) {
        return Sets.newHashSet(new BiomeBase[]{this.f});
    }
}
