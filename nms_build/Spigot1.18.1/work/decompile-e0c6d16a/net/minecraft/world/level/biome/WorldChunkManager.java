package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class WorldChunkManager implements BiomeResolver {

    public static final Codec<WorldChunkManager> CODEC;
    private final Set<BiomeBase> possibleBiomes;
    private final List<WorldChunkManager.b> featuresPerStep;

    protected WorldChunkManager(Stream<Supplier<BiomeBase>> stream) {
        this((List) stream.map(Supplier::get).distinct().collect(ImmutableList.toImmutableList()));
    }

    protected WorldChunkManager(List<BiomeBase> list) {
        this.possibleBiomes = new ObjectLinkedOpenHashSet(list);
        this.featuresPerStep = this.buildFeaturesPerStep(list, true);
    }

    private List<WorldChunkManager.b> buildFeaturesPerStep(List<BiomeBase> list, boolean flag) {
        Object2IntMap<PlacedFeature> object2intmap = new Object2IntOpenHashMap();
        MutableInt mutableint = new MutableInt(0);
        Comparator<a> comparator = Comparator.comparingInt(a::step).thenComparingInt(a::featureIndex);
        Map<a, Set<a>> map = new TreeMap(comparator);
        int i = 0;
        Iterator iterator = list.iterator();

        ArrayList arraylist;
        int j;

        record a(int a, int b, PlacedFeature c) {

            private final int featureIndex;
            private final int step;
            private final PlacedFeature feature;

            a(int k, int l, PlacedFeature placedfeature) {
                this.featureIndex = k;
                this.step = l;
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
            BiomeBase biomebase = (BiomeBase) iterator.next();

            arraylist = Lists.newArrayList();
            List<List<Supplier<PlacedFeature>>> list1 = biomebase.getGenerationSettings().features();

            i = Math.max(i, list1.size());

            for (j = 0; j < list1.size(); ++j) {
                Iterator iterator1 = ((List) list1.get(j)).iterator();

                while (iterator1.hasNext()) {
                    Supplier<PlacedFeature> supplier = (Supplier) iterator1.next();
                    PlacedFeature placedfeature = (PlacedFeature) supplier.get();

                    arraylist.add(new a(object2intmap.computeIfAbsent(placedfeature, (object) -> {
                        return mutableint.getAndIncrement();
                    }), j, placedfeature));
                }
            }

            for (j = 0; j < arraylist.size(); ++j) {
                Set<a> set = (Set) map.computeIfAbsent((a) arraylist.get(j), (a0) -> {
                    return new TreeSet(comparator);
                });

                if (j < arraylist.size() - 1) {
                    set.add((a) arraylist.get(j + 1));
                }
            }
        }

        Set<a> set1 = new TreeSet(comparator);
        Set<a> set2 = new TreeSet(comparator);

        arraylist = Lists.newArrayList();
        Iterator iterator2 = map.keySet().iterator();

        while (iterator2.hasNext()) {
            a a0 = (a) iterator2.next();

            if (!set2.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }

            if (!set1.contains(a0)) {
                Objects.requireNonNull(arraylist);
                if (Graph.depthFirstSearch(map, set1, set2, arraylist::add, a0)) {
                    if (!flag) {
                        throw new IllegalStateException("Feature order cycle found");
                    }

                    ArrayList arraylist1 = new ArrayList(list);

                    int k;

                    do {
                        k = arraylist1.size();
                        ListIterator listiterator = arraylist1.listIterator();

                        while (listiterator.hasNext()) {
                            BiomeBase biomebase1 = (BiomeBase) listiterator.next();

                            listiterator.remove();

                            try {
                                this.buildFeaturesPerStep(arraylist1, false);
                            } catch (IllegalStateException illegalstateexception) {
                                continue;
                            }

                            listiterator.add(biomebase1);
                        }
                    } while (k != arraylist1.size());

                    throw new IllegalStateException("Feature order cycle found, involved biomes: " + arraylist1);
                }
            }
        }

        Collections.reverse(arraylist);
        Builder<WorldChunkManager.b> builder = ImmutableList.builder();

        for (j = 0; j < i; ++j) {
            List<PlacedFeature> list2 = (List) arraylist.stream().filter((a1) -> {
                return a1.step() == j;
            }).map(a::feature).collect(Collectors.toList());
            int l = list2.size();
            Object2IntMap<PlacedFeature> object2intmap1 = new Object2IntOpenCustomHashMap(l, SystemUtils.identityStrategy());

            for (int i1 = 0; i1 < l; ++i1) {
                object2intmap1.put((PlacedFeature) list2.get(i1), i1);
            }

            builder.add(new WorldChunkManager.b(list2, object2intmap1));
        }

        return builder.build();
    }

    protected abstract Codec<? extends WorldChunkManager> codec();

    public abstract WorldChunkManager withSeed(long i);

    public Set<BiomeBase> possibleBiomes() {
        return this.possibleBiomes;
    }

    public Set<BiomeBase> getBiomesWithin(int i, int j, int k, int l, Climate.Sampler climate_sampler) {
        int i1 = QuartPos.fromBlock(i - l);
        int j1 = QuartPos.fromBlock(j - l);
        int k1 = QuartPos.fromBlock(k - l);
        int l1 = QuartPos.fromBlock(i + l);
        int i2 = QuartPos.fromBlock(j + l);
        int j2 = QuartPos.fromBlock(k + l);
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

                    set.add(this.getNoiseBiome(i4, j4, k4, climate_sampler));
                }
            }
        }

        return set;
    }

    @Nullable
    public BlockPosition findBiomeHorizontal(int i, int j, int k, int l, Predicate<BiomeBase> predicate, Random random, Climate.Sampler climate_sampler) {
        return this.findBiomeHorizontal(i, j, k, l, 1, predicate, random, false, climate_sampler);
    }

    @Nullable
    public BlockPosition findBiomeHorizontal(int i, int j, int k, int l, int i1, Predicate<BiomeBase> predicate, Random random, boolean flag, Climate.Sampler climate_sampler) {
        int j1 = QuartPos.fromBlock(i);
        int k1 = QuartPos.fromBlock(k);
        int l1 = QuartPos.fromBlock(l);
        int i2 = QuartPos.fromBlock(j);
        BlockPosition blockposition = null;
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

                    if (predicate.test(this.getNoiseBiome(k3, i2, l3, climate_sampler))) {
                        if (blockposition == null || random.nextInt(j2 + 1) == 0) {
                            blockposition = new BlockPosition(QuartPos.toBlock(k3), j, QuartPos.toBlock(l3));
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

    @Override
    public abstract BiomeBase getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler);

    public void addMultinoiseDebugInfo(List<String> list, BlockPosition blockposition, Climate.Sampler climate_sampler) {}

    public List<WorldChunkManager.b> featuresPerStep() {
        return this.featuresPerStep;
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
