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

    public static final Codec<WorldChunkManagerHell> CODEC = BiomeBase.CODEC.fieldOf("biome").xmap(WorldChunkManagerHell::new, (worldchunkmanagerhell) -> {
        return worldchunkmanagerhell.biome;
    }).stable().codec();
    private final Supplier<BiomeBase> biome;

    public WorldChunkManagerHell(BiomeBase biomebase) {
        this(() -> {
            return biomebase;
        });
    }

    public WorldChunkManagerHell(Supplier<BiomeBase> supplier) {
        super((List) ImmutableList.of((BiomeBase) supplier.get()));
        this.biome = supplier;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerHell.CODEC;
    }

    @Override
    public WorldChunkManager a(long i) {
        return this;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return (BiomeBase) this.biome.get();
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, int k, int l, int i1, Predicate<BiomeBase> predicate, Random random, boolean flag) {
        return predicate.test((BiomeBase) this.biome.get()) ? (flag ? new BlockPosition(i, j, k) : new BlockPosition(i - l + random.nextInt(l * 2 + 1), j, k - l + random.nextInt(l * 2 + 1))) : null;
    }

    @Override
    public Set<BiomeBase> a(int i, int j, int k, int l) {
        return Sets.newHashSet(new BiomeBase[]{(BiomeBase) this.biome.get()});
    }
}
