package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IWorldReader;

public abstract class WorldChunkManager implements BiomeResolver {

    public static final Codec<WorldChunkManager> CODEC = BuiltInRegistries.BIOME_SOURCE.byNameCodec().dispatchStable(WorldChunkManager::codec, Function.identity());
    private final Supplier<Set<Holder<BiomeBase>>> possibleBiomes = Suppliers.memoize(() -> {
        return (Set) this.collectPossibleBiomes().distinct().collect(ImmutableSet.toImmutableSet());
    });

    protected WorldChunkManager() {}

    protected abstract Codec<? extends WorldChunkManager> codec();

    protected abstract Stream<Holder<BiomeBase>> collectPossibleBiomes();

    public Set<Holder<BiomeBase>> possibleBiomes() {
        return (Set) this.possibleBiomes.get();
    }

    public Set<Holder<BiomeBase>> getBiomesWithin(int i, int j, int k, int l, Climate.Sampler climate_sampler) {
        int i1 = QuartPos.fromBlock(i - l);
        int j1 = QuartPos.fromBlock(j - l);
        int k1 = QuartPos.fromBlock(k - l);
        int l1 = QuartPos.fromBlock(i + l);
        int i2 = QuartPos.fromBlock(j + l);
        int j2 = QuartPos.fromBlock(k + l);
        int k2 = l1 - i1 + 1;
        int l2 = i2 - j1 + 1;
        int i3 = j2 - k1 + 1;
        Set<Holder<BiomeBase>> set = Sets.newHashSet();

        for (int j3 = 0; j3 < i3; ++j3) {
            for (int k3 = 0; k3 < k2; ++k3) {
                for (int l3 = 0; l3 < l2; ++l3) {
                    int i4 = i1 + k3;
                    int j4 = j1 + l3;
                    int k4 = k1 + j3;

                    set.add(this.getNoiseBiome(i4, j4, k4, climate_sampler));
                }
            }
        }

        return set;
    }

    @Nullable
    public Pair<BlockPosition, Holder<BiomeBase>> findBiomeHorizontal(int i, int j, int k, int l, Predicate<Holder<BiomeBase>> predicate, RandomSource randomsource, Climate.Sampler climate_sampler) {
        return this.findBiomeHorizontal(i, j, k, l, 1, predicate, randomsource, false, climate_sampler);
    }

    @Nullable
    public Pair<BlockPosition, Holder<BiomeBase>> findClosestBiome3d(BlockPosition blockposition, int i, int j, int k, Predicate<Holder<BiomeBase>> predicate, Climate.Sampler climate_sampler, IWorldReader iworldreader) {
        Set<Holder<BiomeBase>> set = (Set) this.possibleBiomes().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());

        if (set.isEmpty()) {
            return null;
        } else {
            int l = Math.floorDiv(i, j);
            int[] aint = MathHelper.outFromOrigin(blockposition.getY(), iworldreader.getMinBuildHeight() + 1, iworldreader.getMaxBuildHeight(), k).toArray();
            Iterator iterator = BlockPosition.spiralAround(BlockPosition.ZERO, l, EnumDirection.EAST, EnumDirection.SOUTH).iterator();

            while (iterator.hasNext()) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
                int i1 = blockposition.getX() + blockposition_mutableblockposition.getX() * j;
                int j1 = blockposition.getZ() + blockposition_mutableblockposition.getZ() * j;
                int k1 = QuartPos.fromBlock(i1);
                int l1 = QuartPos.fromBlock(j1);
                int[] aint1 = aint;
                int i2 = aint.length;

                for (int j2 = 0; j2 < i2; ++j2) {
                    int k2 = aint1[j2];
                    int l2 = QuartPos.fromBlock(k2);
                    Holder<BiomeBase> holder = this.getNoiseBiome(k1, l2, l1, climate_sampler);

                    if (set.contains(holder)) {
                        return Pair.of(new BlockPosition(i1, k2, j1), holder);
                    }
                }
            }

            return null;
        }
    }

    @Nullable
    public Pair<BlockPosition, Holder<BiomeBase>> findBiomeHorizontal(int i, int j, int k, int l, int i1, Predicate<Holder<BiomeBase>> predicate, RandomSource randomsource, boolean flag, Climate.Sampler climate_sampler) {
        int j1 = QuartPos.fromBlock(i);
        int k1 = QuartPos.fromBlock(k);
        int l1 = QuartPos.fromBlock(l);
        int i2 = QuartPos.fromBlock(j);
        Pair<BlockPosition, Holder<BiomeBase>> pair = null;
        int j2 = 0;
        int k2 = flag ? 0 : l1;

        for (int l2 = k2; l2 <= l1; l2 += i1) {
            for (int i3 = SharedConstants.debugGenerateSquareTerrainWithoutNoise ? 0 : -l2; i3 <= l2; i3 += i1) {
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
                    Holder<BiomeBase> holder = this.getNoiseBiome(k3, i2, l3, climate_sampler);

                    if (predicate.test(holder)) {
                        if (pair == null || randomsource.nextInt(j2 + 1) == 0) {
                            BlockPosition blockposition = new BlockPosition(QuartPos.toBlock(k3), j, QuartPos.toBlock(l3));

                            if (flag) {
                                return Pair.of(blockposition, holder);
                            }

                            pair = Pair.of(blockposition, holder);
                        }

                        ++j2;
                    }
                }
            }
        }

        return pair;
    }

    @Override
    public abstract Holder<BiomeBase> getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler);

    public void addDebugInfo(List<String> list, BlockPosition blockposition, Climate.Sampler climate_sampler) {}
}
