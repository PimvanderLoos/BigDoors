package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;

public abstract class WorldChunkManager implements BiomeManager.Provider {

    public static final Codec<WorldChunkManager> CODEC;
    protected final Map<StructureGenerator<?>, Boolean> supportedStructures;
    protected final Set<IBlockData> surfaceBlocks;
    protected final List<BiomeBase> possibleBiomes;

    protected WorldChunkManager(Stream<Supplier<BiomeBase>> stream) {
        this((List) stream.map(Supplier::get).collect(ImmutableList.toImmutableList()));
    }

    protected WorldChunkManager(List<BiomeBase> list) {
        this.supportedStructures = Maps.newHashMap();
        this.surfaceBlocks = Sets.newHashSet();
        this.possibleBiomes = list;
    }

    protected abstract Codec<? extends WorldChunkManager> a();

    public abstract WorldChunkManager a(long i);

    public List<BiomeBase> b() {
        return this.possibleBiomes;
    }

    public Set<BiomeBase> a(int i, int j, int k, int l) {
        int i1 = QuartPos.a(i - l);
        int j1 = QuartPos.a(j - l);
        int k1 = QuartPos.a(k - l);
        int l1 = QuartPos.a(i + l);
        int i2 = QuartPos.a(j + l);
        int j2 = QuartPos.a(k + l);
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
    public BlockPosition a(int i, int j, int k, int l, Predicate<BiomeBase> predicate, Random random) {
        return this.a(i, j, k, l, 1, predicate, random, false);
    }

    @Nullable
    public BlockPosition a(int i, int j, int k, int l, int i1, Predicate<BiomeBase> predicate, Random random, boolean flag) {
        int j1 = QuartPos.a(i);
        int k1 = QuartPos.a(k);
        int l1 = QuartPos.a(l);
        int i2 = QuartPos.a(j);
        BlockPosition blockposition = null;
        int j2 = 0;
        int k2 = flag ? 0 : l1;

        for (int l2 = k2; l2 <= l1; l2 += i1) {
            for (int i3 = -l2; i3 <= l2; i3 += i1) {
                boolean flag1 = Math.abs(i3) == l2;

                for (int j3 = -l2; j3 <= l2; j3 += i1) {
                    if (flag) {
                        boolean flag2 = Math.abs(j3) == l2;

                        if (!flag2 && !flag1) {
                            continue;
                        }
                    }

                    int k3 = j1 + j3;
                    int l3 = k1 + i3;

                    if (predicate.test(this.getBiome(k3, i2, l3))) {
                        if (blockposition == null || random.nextInt(j2 + 1) == 0) {
                            blockposition = new BlockPosition(QuartPos.b(k3), j, QuartPos.b(l3));
                            if (flag) {
                                return blockposition;
                            }
                        }

                        ++j2;
                    }
                }
            }
        }

        return blockposition;
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return (Boolean) this.supportedStructures.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return this.possibleBiomes.stream().anyMatch((biomebase) -> {
                return biomebase.e().a(structuregenerator1);
            });
        });
    }

    public Set<IBlockData> c() {
        if (this.surfaceBlocks.isEmpty()) {
            Iterator iterator = this.possibleBiomes.iterator();

            while (iterator.hasNext()) {
                BiomeBase biomebase = (BiomeBase) iterator.next();

                this.surfaceBlocks.add(biomebase.e().e().a());
            }
        }

        return this.surfaceBlocks;
    }

    static {
        IRegistry.a(IRegistry.BIOME_SOURCE, "fixed", (Object) WorldChunkManagerHell.CODEC);
        IRegistry.a(IRegistry.BIOME_SOURCE, "multi_noise", (Object) WorldChunkManagerMultiNoise.CODEC);
        IRegistry.a(IRegistry.BIOME_SOURCE, "checkerboard", (Object) WorldChunkManagerCheckerBoard.CODEC);
        IRegistry.a(IRegistry.BIOME_SOURCE, "vanilla_layered", (Object) WorldChunkManagerOverworld.CODEC);
        IRegistry.a(IRegistry.BIOME_SOURCE, "the_end", (Object) WorldChunkManagerTheEnd.CODEC);
        CODEC = IRegistry.BIOME_SOURCE.dispatchStable(WorldChunkManager::a, Function.identity());
    }
}
