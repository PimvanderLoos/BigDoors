package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {

    public FeatureSorter() {}

    public static <T> List<FeatureSorter.b> buildFeaturesPerStep(List<T> list, Function<T, List<HolderSet<PlacedFeature>>> function, boolean flag) {
        Object2IntMap<PlacedFeature> object2intmap = new Object2IntOpenHashMap();
        MutableInt mutableint = new MutableInt(0);
        Comparator<a> comparator = Comparator.comparingInt(a::step).thenComparingInt(a::featureIndex);
        Map<a, Set<a>> map = new TreeMap(comparator);
        int i = 0;
        Iterator iterator = list.iterator();

        ArrayList arraylist;
        int j;

        record a(int featureIndex, int step, PlacedFeature feature) {

        }

        while (iterator.hasNext()) {
            T t0 = iterator.next();

            arraylist = Lists.newArrayList();
            List<HolderSet<PlacedFeature>> list1 = (List) function.apply(t0);

            i = Math.max(i, list1.size());

            for (j = 0; j < list1.size(); ++j) {
                Iterator iterator1 = ((HolderSet) list1.get(j)).iterator();

                while (iterator1.hasNext()) {
                    Holder<PlacedFeature> holder = (Holder) iterator1.next();
                    PlacedFeature placedfeature = (PlacedFeature) holder.value();

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
                            T t1 = listiterator.next();

                            listiterator.remove();

                            try {
                                buildFeaturesPerStep(arraylist1, function, false);
                            } catch (IllegalStateException illegalstateexception) {
                                continue;
                            }

                            listiterator.add(t1);
                        }
                    } while (k != arraylist1.size());

                    throw new IllegalStateException("Feature order cycle found, involved sources: " + arraylist1);
                }
            }
        }

        Collections.reverse(arraylist);
        Builder<FeatureSorter.b> builder = ImmutableList.builder();

        for (j = 0; j < i; ++j) {
            List<PlacedFeature> list2 = (List) arraylist.stream().filter((a1) -> {
                return a1.step() == j;
            }).map(a::feature).collect(Collectors.toList());

            builder.add(new FeatureSorter.b(list2));
        }

        return builder.build();
    }

    public static record b(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {

        b(List<PlacedFeature> list) {
            this(list, SystemUtils.createIndexLookup(list, (i) -> {
                return new Object2IntOpenCustomHashMap(i, SystemUtils.identityStrategy());
            }));
        }
    }
}
