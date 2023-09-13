package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;

public class WorldChunkManagerHell extends WorldChunkManager {

    public static final Codec<WorldChunkManagerHell> e = BiomeBase.d.fieldOf("biome").xmap(WorldChunkManagerHell::new, (worldchunkmanagerhell) -> {
        return worldchunkmanagerhell.f;
    }).stable().codec();
    private final Supplier<BiomeBase> f;

    public WorldChunkManagerHell(BiomeBase biomebase) {
        this(() -> {
            return biomebase;
        });
    }

    public WorldChunkManagerHell(Supplier<BiomeBase> supplier) {
        super((List) ImmutableList.of(supplier.get()));
        this.f = supplier;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerHell.e;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return (BiomeBase) this.f.get();
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, int k, int l, int i1, Predicate<BiomeBase> predicate, Random random, boolean flag) {
        return predicate.test(this.f.get()) ? (flag ? new BlockPosition(i, j, k) : new BlockPosition(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1))) : null;
    }

    @Override
    public Set<BiomeBase> a(int i, int j, int k, int l) {
        return Sets.newHashSet(new BiomeBase[]{(BiomeBase) this.f.get()});
    }
}
