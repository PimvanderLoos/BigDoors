package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class WorldChunkManager implements BiomeResolver {

    public static final Codec<WorldChunkManager> CODEC;
    private final Set<Holder<BiomeBase>> possibleBiomes;
    private final Supplier<List<WorldChunkManager.b>> featuresPerStep;

    protected WorldChunkManager(Stream<Holder<BiomeBase>> stream) {
        this(stream.distinct().toList());
    }

    protected WorldChunkManager(List<Holder<BiomeBase>> list) {
        this.possibleBiomes = new ObjectLinkedOpenHashSet(list);
        this.featuresPerStep = Suppliers.memoize(() -> {
            return this.buildFeaturesPerStep(list, true);
        });
    }

    private List<WorldChunkManager.b> buildFeaturesPerStep(List<Holder<BiomeBase>> list, boolean flag) {
        Object2IntMap<PlacedFeature> object2intmap = new Object2IntOpenHashMap();
        MutableInt mutableint = new MutableInt(0);
        Comparator<a> comparator = Comparator.comparingInt(a::step).thenComparingInt(a::featureIndex);
        Map<a, Set<a>> map = new TreeMap(comparator);
        int i = 0;
        Iterator iterator = list.iterator();

        record a(int a, int b, PlacedFeature c) {

            private final int featureIndex;
            private final int step;
            private final PlacedFeature feature;

            a(int j, int k, PlacedFeature placedfeature) {
                this.featureIndex = j;
                this.step = k;
                this.feature = placedfeature;
            }

            public int featureIndex() {
                return this.featureIndex;
            }

            public int step() {
                return this.step;
            }

            public PlacedFeature feature() {
                return this.feature;
            }
        }

        while (iterator.hasNext()) {
            Holder<BiomeBase> holder = (Holder) iterator.next();
            BiomeBase biomebase = (BiomeBase) holder.value();
            List<a> list1 = Lists.newArrayList();
            List<HolderSet<PlacedFeature>> list2 = biomebase.getGenerationSettings().features();

            i = Math.max(i, list2.size());

            int j;

            for (j = 0; j < list2.size(); ++j) {
                Iterator iterator1 = ((HolderSet) list2.get(j)).iterator();

                while (iterator1.hasNext()) {
                    Holder<PlacedFeature> holder1 = (Holder) iterator1.next();
                    PlacedFeature placedfeature = (PlacedFeature) holder1.value();

                    list1.add(new a(object2intmap.computeIfAbsent(placedfeature, (object) -> {
                        return mutableint.getAndIncrement();
                    }), j, placedfeature));
                }
            }

            for (j = 0; j < list1.size(); ++j) {
                Set<a> set = (Set) map.computeIfAbsent((a) list1.get(j), (a0) -> {
                    return new TreeSet(comparator);
                });

                if (j < list1.size() - 1) {
                    set.add((a) list1.get(j + 1));
                }
            }
        }

        Set<a> set1 = new TreeSet(comparator);
        Set<a> set2 = new TreeSet(comparator);
        List<a> list3 = Lists.newArrayList();
        Iterator iterator2 = map.keySet().iterator();

        while (iterator2.hasNext()) {
            a a0 = (a) iterator2.next();

            if (!set2.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }

            if (!set1.contains(a0)) {
                Objects.requireNonNull(list3);
                if (Graph.depthFirstSearch(map, set1, set2, list3::add, a0)) {
                    if (!flag) {
                        throw new IllegalStateException("Feature order cycle found");
                    }

                    ArrayList arraylist = new ArrayList(list);

                    int k;

                    do {
                        k = arraylist.size();
                        ListIterator listiterator = arraylist.listIterator();

                        while (listiterator.hasNext()) {
                            Holder<BiomeBase> holder2 = (Holder) listiterator.next();

                            listiterator.remove();

                            try {
                                this.buildFeaturesPerStep(arraylist, false);
                            } catch (IllegalStateException illegalstateexception) {
                                continue;
                            }

                            listiterator.add(holder2);
                        }
                    } while (k != arraylist.size());

                    throw new IllegalStateException("Feature order cycle found, involved biomes: " + arraylist);
                }
            }
        }

        Collections.reverse(list3);
        Builder<WorldChunkManager.b> builder = ImmutableList.builder();

        for (int l = 0; l < i; ++l) {
            List<PlacedFeature> list4 = (List) list3.stream().filter((a1) -> {
                return a1.step() == l;
            }).map(a::feature).collect(Collectors.toList());
            int i1 = list4.size();
            Object2IntMap<PlacedFeature> object2intmap1 = new Object2IntOpenCustomHashMap(i1, SystemUtils.identityStrategy());

            for (int j1 = 0; j1 < i1; ++j1) {
                object2intmap1.put((PlacedFeature) list4.get(j1), j1);
            }

            builder.add(new WorldChunkManager.b(list4, object2intmap1));
        }

        return builder.build();
    }

    protected abstract Codec<? extends WorldChunkManager> codec();

    public abstract WorldChunkManager withSeed(long i);

    public Set<Holder<BiomeBase>> possibleBiomes() {
        return this.possibleBiomes;
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
    public Pair<BlockPosition, Holder<BiomeBase>> findBiomeHorizontal(int i, int j, int k, int l, Predicate<Holder<BiomeBase>> predicate, Random random, Climate.Sampler climate_sampler) {
        return this.findBiomeHorizontal(i, j, k, l, 1, predicate, random, false, climate_sampler);
    }

    @Nullable
    public Pair<BlockPosition, Holder<BiomeBase>> findBiomeHorizontal(int i, int j, int k, int l, int i1, Predicate<Holder<BiomeBase>> predicate, Random random, boolean flag, Climate.Sampler climate_sampler) {
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
                        if (pair == null || random.nextInt(j2 + 1) == 0) {
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

    public List<WorldChunkManager.b> featuresPerStep() {
        return (List) this.featuresPerStep.get();
    }

    static {
        IRegistry.register(IRegistry.BIOME_SOURCE, "fixed", WorldChunkManagerHell.CODEC);
        IRegistry.register(IRegistry.BIOME_SOURCE, "multi_noise", WorldChunkManagerMultiNoise.CODEC);
        IRegistry.register(IRegistry.BIOME_SOURCE, "checkerboard", WorldChunkManagerCheckerBoard.CODEC);
        IRegistry.register(IRegistry.BIOME_SOURCE, "the_end", WorldChunkManagerTheEnd.CODEC);
        CODEC = IRegistry.BIOME_SOURCE.byNameCodec().dispatchStable(WorldChunkManager::codec, Function.identity());
    }

    public static record b(List<PlacedFeature> a, ToIntFunction<PlacedFeature> b) {

        private final List<PlacedFeature> features;
        private final ToIntFunction<PlacedFeature> indexMapping;

        public b(List<PlacedFeature> list, ToIntFunction<PlacedFeature> tointfunction) {
            this.features = list;
            this.indexMapping = tointfunction;
        }

        public List<PlacedFeature> features() {
            return this.features;
        }

        public ToIntFunction<PlacedFeature> indexMapping() {
            return this.indexMapping;
        }
    }
}
